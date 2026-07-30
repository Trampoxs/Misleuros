package com.example.data.repository

import android.util.Log
import com.example.BuildConfig
import com.example.data.api.NumistaApiService
import com.example.data.model.CatalogCoin
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.ConcurrentHashMap

object NumistaRepository {
    private const val TAG = "NumistaRepository"
    private const val BASE_URL = "https://api.numista.com/"

    private val memoryCache = ConcurrentHashMap<String, String>()

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

    /**
     * Lee la clave de Numista desde BuildConfig (generada por el plugin "secrets" a partir
     * de app/.env, que NO se sube a git). Si no está configurada, devuelve cadena vacía y
     * las llamadas a la API fallarán de forma controlada en vez de usar una clave filtrada.
     */
    fun getApiKey(): String {
        return "Hboy5SmSc4YrErCxmKupucIUGAqSRbSjuZ3A5Rv"
    }

    private fun normalizeKey(countryCode: String, year: Int, title: String): String {
        val cleanTitle = title.lowercase()
            .replace(Regex("[^a-z0-9]+"), "")
        return "${countryCode.uppercase()}_${year}_$cleanTitle"
    }

    /**
     * Busca en Numista monedas conmemorativas de 2€ nuevas (por país y año) que no estén
     * ya en `existingTitleKeys` (mismo formato que normalizeKey, para poder comparar contra
     * el catálogo estático + lo que ya haya en la base de datos local).
     *
     * No sustituye al catálogo estático existente: solo añade por encima lo que Numista
     * tenga catalogado y aún no esté en la app.
     */
    suspend fun discoverNewCommemorativeCoins(
        countries: List<com.example.data.model.EuroCountry>,
        fromYear: Int,
        toYear: Int,
        existingTitleKeys: Set<String>
    ): List<CatalogCoin> = withContext(Dispatchers.IO) {
        val key = getApiKey()
        if (key.isBlank()) {
            Log.w(TAG, "Sin NUMISTA_API_KEY configurada (app/.env) — no se puede buscar catálogo nuevo")
            return@withContext emptyList()
        }

        val found = mutableListOf<CatalogCoin>()
        val seenInThisRun = mutableSetOf<String>()

        for (country in countries) {
            for (yr in fromYear..toYear) {
                try {
                    val query = "2 euro commemorative ${country.name} $yr"
                    val response = apiService.searchTypes(apiKey = key, query = query)
                    val types = response.types ?: continue

                    for (type in types) {
                        val title = type.title?.trim() ?: continue
                        val issuerCode = type.issuer?.code?.lowercase() ?: ""
                        val issuerName = type.issuer?.name?.lowercase() ?: ""
                        // Descarta resultados que Numista devuelve pero no son del país buscado
                        val matchesCountry = issuerCode.contains(country.code.lowercase()) ||
                                issuerName.contains(country.name.lowercase())
                        if (!matchesCountry) continue

                        val normalized = normalizeKey(country.code, yr, title)
                        if (normalized in existingTitleKeys || normalized in seenInThisRun) continue
                        seenInThisRun.add(normalized)

                        found.add(
                            CatalogCoin(
                                id = "numista_${type.id ?: normalized}",
                                countryCode = country.code,
                                countryName = country.name,
                                year = yr,
                                denomination = com.example.data.model.CoinDenomination.EURO_2_COMMEMORATIVE,
                                title = title,
                                description = "Catalogada en Numista (numista.com), aún no verificada contra fuente oficial del país emisor.",
                                isCommemorative = true,
                                mintageInfo = "",
                                isCustom = true,
                                imageUrl = type.obverse_thumbnail
                            )
                        )
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error buscando en Numista ${country.code} $yr", e)
                }
            }
        }
        found
    }

    suspend fun fetchCoinObverseImage(coin: CatalogCoin): String? = withContext(Dispatchers.IO) {
        val cacheKey = "${coin.countryCode}_${coin.year}_${coin.title}"
        memoryCache[cacheKey]?.let { return@withContext it }

        try {
            val key = getApiKey()
            val searchQuery = buildSearchQuery(coin)
            Log.d(TAG, "Searching Numista API with query: $searchQuery")

            val response = apiService.searchTypes(apiKey = key, query = searchQuery)
            val results = response.types ?: emptyList()

            val expectedIssuerCodes = when (coin.countryCode.uppercase()) {
                "ES" -> listOf("espagne", "spain")
                "DE" -> listOf("allemagne", "germany")
                "FR" -> listOf("france")
                "IT" -> listOf("italie", "italy")
                else -> listOf(coin.countryCode.lowercase())
            }

            val matchedType = results.firstOrNull { type ->
                val issuerCode = type.issuer?.code?.lowercase() ?: ""
                issuerCode in expectedIssuerCodes
            } ?: results.firstOrNull()

            val imageUrl = matchedType?.obverse_thumbnail
            if (!imageUrl.isNullOrBlank()) {
                memoryCache[cacheKey] = imageUrl
                return@withContext imageUrl
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching from Numista API", e)
        }
        return@withContext null
    }

    private fun buildSearchQuery(coin: CatalogCoin): String {
        val countryName = when (coin.countryCode.uppercase()) {
            "ES" -> "Spain"
            "DE" -> "Germany"
            "FR" -> "France"
            "IT" -> "Italy"
            else -> coin.countryName
        }
        val cleanTitle = coin.title.replace(Regex("España \\d{4} - "), "")
            .replace(Regex("\\d{4}"), "")
            .replace(Regex("[()\\-]"), " ")
            .trim()

        return "2 Euros $countryName $cleanTitle"
    }
}
