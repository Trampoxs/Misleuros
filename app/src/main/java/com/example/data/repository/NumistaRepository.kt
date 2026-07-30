package com.example.data.repository

import android.util.Log
import com.example.BuildConfig
import com.example.data.api.NumistaApiService
import com.example.data.api.NumistaIssuer
import com.example.data.api.NumistaType
import com.example.data.model.CatalogCoin
import com.example.data.model.CoinDenomination
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.ConcurrentHashMap

object NumistaRepository {
    private const val TAG = "NumistaRepository"
    private const val BASE_URL = "https://api.numista.com/api/"

    private val memoryCache = ConcurrentHashMap<String, String>()
    private val activeJobs = ConcurrentHashMap<String, kotlinx.coroutines.Deferred<String?>>()

    private val apiService: NumistaApiService by lazy {
        val moshi = Moshi.Builder()
            .addLast(KotlinJsonAdapterFactory())
            .build()
        val okHttpClient = OkHttpClient.Builder().build()
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(NumistaApiService::class.java)
    }

    fun getApiKey(): String {
        return try {
            val keyFromBuildConfig = BuildConfig.NUMISTA_API_KEY
            if (keyFromBuildConfig.isNotBlank() && keyFromBuildConfig != "NUMISTA_API_KEY_DEFAULT_VALUE") {
                keyFromBuildConfig
            } else {
                "Hboy5SmSc4YrErCxmKupucIUGAqSRbSjuZ3A5Rv"
            }
        } catch (e: Throwable) {
            "Hboy5SmSc4YrErCxmKupucIUGAqSRbSjuZ3A5Rv"
        }
    }

    suspend fun fetchCoinObverseImage(coin: CatalogCoin): String? = withContext(Dispatchers.IO) {
        val official = com.example.ui.components.OfficialEuroCoinImageProvider.getImageUrl(coin)
        if (official.isNotBlank()) {
            return@withContext official
        }

        val cacheKey = "${coin.countryCode}_${coin.year}_${coin.title.trim()}"
        memoryCache[cacheKey]?.let { cached ->
            return@withContext cached.ifEmpty { null }
        }

        // Deduplicate in-flight requests for the same coin
        val existingJob = activeJobs[cacheKey]
        if (existingJob != null) {
            return@withContext existingJob.await()
        }

        val deferred = async {
            try {
                val key = getApiKey()
                val keywords = extractCleanKeywords(coin.title)
                val countryName = getEnglishCountryName(coin.countryCode, coin.countryName)
                val denomStr = getDenominationString(coin.denomination)
                val isComm = coin.isCommemorative || coin.denomination == CoinDenomination.EURO_2_COMMEMORATIVE

                // Search query variations ordered by specificity
                val queries = mutableListOf<String>()
                if (keywords.isNotEmpty()) {
                    queries.add("$countryName $denomStr ${keywords.take(3).joinToString(" ")}")
                    queries.add("$countryName ${keywords.take(3).joinToString(" ")}")
                }
                if (isComm && coin.year > 0) {
                    queries.add("$countryName $denomStr ${coin.year}")
                }
                queries.add("$countryName $denomStr")
                queries.add("$countryName euro")

                var bestCandidate: NumistaType? = null
                var bestScore = 0

                for (query in queries.distinct()) {
                    Log.d(TAG, "Searching Numista API with query: '$query' for coin '${coin.title}'")
                    val response = try {
                        apiService.searchTypes(apiKey = key, query = query)
                    } catch (e: Exception) {
                        null
                    }

                    val results = response?.types ?: emptyList()
                    if (results.isNotEmpty()) {
                        for (type in results) {
                            val score = calculateScore(type, coin, keywords)
                            if (score > bestScore) {
                                bestScore = score
                                bestCandidate = type
                            }
                        }
                    }

                    // High confidence score reached -> stop searching further queries
                    if (bestScore >= 200) {
                        break
                    }
                }

                // Minimum score threshold to avoid false positives (wrong country, wrong monarch, etc.)
                if (bestCandidate != null && bestScore >= 60) {
                    var imageUrl = bestCandidate.obverse_thumbnail
                    if (!imageUrl.isNullOrBlank()) {
                        if (imageUrl.startsWith("/")) {
                            imageUrl = "https://en.numista.com$imageUrl"
                        }
                        memoryCache[cacheKey] = imageUrl
                        Log.d(TAG, "Matched coin '${coin.title}' with Numista type '${bestCandidate.title}' (Score: $bestScore) -> $imageUrl")
                        return@async imageUrl
                    }
                } else {
                    Log.w(TAG, "No reliable Numista match for '${coin.title}' (Best score: $bestScore)")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error fetching from Numista API for coin: ${coin.title}", e)
            }
            memoryCache[cacheKey] = "" // Cache empty result to avoid hammering failing queries
            null
        }

        activeJobs[cacheKey] = deferred
        try {
            return@withContext deferred.await()
        } finally {
            activeJobs.remove(cacheKey)
        }
    }

    suspend fun preFetchImagesForCoins(coins: List<CatalogCoin>) = withContext(Dispatchers.IO) {
        val uncachedCoins = coins.filter { coin ->
            val cacheKey = "${coin.countryCode}_${coin.year}_${coin.title.trim()}"
            !memoryCache.containsKey(cacheKey)
        }.take(30)

        uncachedCoins.chunked(5).forEach { chunk ->
            chunk.map { coin ->
                async { fetchCoinObverseImage(coin) }
            }.awaitAll()
        }
    }

    private fun calculateScore(type: NumistaType, coin: CatalogCoin, keywords: List<String>): Int {
        var score = 0

        val typeTitle = type.title?.lowercase() ?: ""

        // 0. Currency Strict Filtering - Must be Euro / Cent coin, reject legacy currencies
        val legacyCurrencies = listOf(
            "peseta", "pesetas", "franc", "francs", "mark", "pfennig", "lira", "lire",
            "real", "reales", "maravedi", "escudo", "escudos", "drachm", "schilling",
            "guilder", "gulden", "ecu", "ecus", "krona", "kruunu", "sovran", "sovereign",
            "dinar", "ruble", "rubles", "dollar", "dollars"
        )
        if (legacyCurrencies.any { typeTitle.contains(it) }) {
            return -10000
        }

        if (!typeTitle.contains("euro") && !typeTitle.contains("cent") && !typeTitle.contains("ct")) {
            return -10000
        }

        // 1. Issuer verification (STRICT)
        if (!isIssuerMatch(type.issuer, coin.countryCode)) {
            return -10000 // Reject wrong country immediately
        }
        score += 100

        val coinTitleLower = coin.title.lowercase()
        val isComm = coin.isCommemorative || coin.denomination == CoinDenomination.EURO_2_COMMEMORATIVE

        // 2. Year Matching
        if (coin.year > 0) {
            val minYr = type.min_year
            val maxYr = type.max_year
            if (minYr != null && maxYr != null) {
                if (coin.year in minYr..maxYr) {
                    score += 100
                    if (minYr == maxYr && isComm) {
                        score += 50
                    }
                } else {
                    val diff = kotlin.math.min(
                        kotlin.math.abs(coin.year - minYr),
                        kotlin.math.abs(coin.year - maxYr)
                    )
                    if (diff <= 1) {
                        score -= 30
                    } else {
                        score -= 200
                    }
                }
            }
        }

        // 3. Commemorative vs Standard Alignment
        val typeIsCommemorative = typeTitle.contains("commemorative") ||
                typeTitle.contains("commémorative") ||
                (type.min_year != null && type.min_year == type.max_year)

        if (isComm) {
            if (typeIsCommemorative) score += 80 else score -= 60
        } else {
            if (!typeIsCommemorative) score += 80 else score -= 60
        }

        // 4. Monarch / Subject Specific Matching
        if (coinTitleLower.contains("felipe")) {
            if (typeTitle.contains("felipe")) score += 150 else if (typeTitle.contains("juan carlos")) score -= 300
        }
        if (coinTitleLower.contains("juan carlos")) {
            if (typeTitle.contains("juan carlos")) score += 150 else if (typeTitle.contains("felipe")) score -= 300
        }
        if (coinTitleLower.contains("cervantes")) {
            if (typeTitle.contains("cervantes")) score += 150
        }
        if (coinTitleLower.contains("santiago")) {
            if (typeTitle.contains("santiago")) score += 150
        }
        if (coinTitleLower.contains("beatrix") || coinTitleLower.contains("beatriz")) {
            if (typeTitle.contains("beatrix")) score += 150
        }
        if (coinTitleLower.contains("willem") || coinTitleLower.contains("guillermo")) {
            if (typeTitle.contains("willem")) score += 150
        }

        // 5. Keyword Overlap
        keywords.forEach { kw ->
            val kwLower = kw.lowercase()
            if (typeTitle.contains(kwLower)) {
                score += 70
            }
        }

        // 6. Denomination Check
        val targetDenomNumber = when (coin.denomination) {
            CoinDenomination.EURO_2, CoinDenomination.EURO_2_COMMEMORATIVE -> "2"
            CoinDenomination.EURO_1 -> "1"
            CoinDenomination.CENT_50 -> "50"
            CoinDenomination.CENT_20 -> "20"
            CoinDenomination.CENT_10 -> "10"
            CoinDenomination.CENT_5 -> "5"
            CoinDenomination.CENT_2 -> "2"
            CoinDenomination.CENT_1 -> "1"
            else -> null
        }

        if (targetDenomNumber != null) {
            if (typeTitle.contains("$targetDenomNumber euro") || typeTitle.contains("$targetDenomNumber cent")) {
                score += 80
            } else if (typeTitle.contains("$targetDenomNumber ")) {
                score += 30
            }
        }

        return score
    }

    private fun isIssuerMatch(issuer: NumistaIssuer?, countryCode: String): Boolean {
        if (issuer == null) return true
        val code = issuer.code?.lowercase() ?: ""
        val name = issuer.name?.lowercase() ?: ""
        val expected = getIssuerKeywords(countryCode)
        return expected.any { kw -> code.contains(kw) || name.contains(kw) }
    }

    private fun getIssuerKeywords(countryCode: String): List<String> {
        return when (countryCode.uppercase()) {
            "ES" -> listOf("espagne", "spain", "españa")
            "DE" -> listOf("allemagne", "germany", "deutschland")
            "FR" -> listOf("france")
            "IT" -> listOf("italie", "italy", "italia")
            "AT" -> listOf("autriche", "austria", "österreich", "osterreich")
            "BE" -> listOf("belgique", "belgium", "belgië", "belgie")
            "NL" -> listOf("pays-bas", "netherlands", "nederland")
            "PT" -> listOf("portugal")
            "FI" -> listOf("finlande", "finland", "suomi")
            "GR" -> listOf("grece", "greece", "grèce", "hellas")
            "IE" -> listOf("irlande", "ireland", "éire", "eire")
            "LU" -> listOf("luxembourg", "lëtzebuerg")
            "SK" -> listOf("slovaquie", "slovakia", "slovensko")
            "SI" -> listOf("slovenie", "slovenia", "slovenija")
            "CY" -> listOf("chypre", "cyprus", "kypros")
            "MT" -> listOf("malte", "malta")
            "EE" -> listOf("estonie", "estonia", "eesti")
            "LV" -> listOf("lettonie", "latvia", "latvija")
            "LT" -> listOf("lituanie", "lithuania", "lietuva")
            "HR" -> listOf("croatie", "croatia", "hrvatska")
            "AD" -> listOf("andorre", "andorra")
            "MC" -> listOf("monaco")
            "SM" -> listOf("saint-marin", "san marino", "san_marino")
            "VA" -> listOf("vatican", "città del vaticano")
            else -> listOf(countryCode.lowercase())
        }
    }

    private fun extractCleanKeywords(title: String): List<String> {
        val stopWords = setOf(
            "españa", "spain", "germany", "francia", "france", "italia", "italy", "austria", "belgica", "belgium",
            "euros", "euro", "centimos", "centimo", "cents", "cent", "centimes", "centime",
            "conmemorativa", "conmemorativo", "moneda", "serie", "estado", "patrimonio",
            "mundial", "unesco", "aniversario", "centenario", "de", "del", "la", "el", "los",
            "las", "en", "y", "o", "por", "para", "con", "un", "una", "se", "es", "al", "rey"
        )

        val words = title
            .replace(Regex("(?i)España \\d{4} - "), " ")
            .replace(Regex("(?i)Alemania \\d{4} - "), " ")
            .replace(Regex("(?i)Francia \\d{4} - "), " ")
            .replace(Regex("(?i)Italia \\d{4} - "), " ")
            .replace(Regex("\\d{4}"), " ")
            .replace(Regex("[():;\"'’‘“”\\-–—,.]"), " ")
            .split(Regex("\\s+"))
            .map { it.trim() }
            .filter { word ->
                word.length >= 3 && word.lowercase() !in stopWords && !word.all { it.isDigit() }
            }

        return words.distinct()
    }

    private fun getEnglishCountryName(countryCode: String, fallback: String): String {
        return when (countryCode.uppercase()) {
            "ES" -> "Spain"
            "DE" -> "Germany"
            "FR" -> "France"
            "IT" -> "Italy"
            "AT" -> "Austria"
            "BE" -> "Belgium"
            "NL" -> "Netherlands"
            "PT" -> "Portugal"
            "FI" -> "Finland"
            "GR" -> "Greece"
            "IE" -> "Ireland"
            "LU" -> "Luxembourg"
            "SK" -> "Slovakia"
            "SI" -> "Slovenia"
            "CY" -> "Cyprus"
            "MT" -> "Malta"
            "EE" -> "Estonia"
            "LV" -> "Latvia"
            "LT" -> "Lithuania"
            "HR" -> "Croatia"
            "AD" -> "Andorra"
            "MC" -> "Monaco"
            "SM" -> "San Marino"
            "VA" -> "Vatican"
            else -> fallback
        }
    }

    private fun getDenominationString(denom: CoinDenomination): String {
        return when (denom) {
            CoinDenomination.EURO_2, CoinDenomination.EURO_2_COMMEMORATIVE -> "2 Euros"
            CoinDenomination.EURO_1 -> "1 Euro"
            CoinDenomination.CENT_50 -> "50 Centimes"
            CoinDenomination.CENT_20 -> "20 Centimes"
            CoinDenomination.CENT_10 -> "10 Centimes"
            CoinDenomination.CENT_5 -> "5 Centimes"
            CoinDenomination.CENT_2 -> "2 Centimes"
            CoinDenomination.CENT_1 -> "1 Centime"
            else -> "Euro"
        }
    }
}

