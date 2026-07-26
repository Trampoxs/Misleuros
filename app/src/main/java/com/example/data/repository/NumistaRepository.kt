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
    private const val BASE_URL = "https://api.numista.com/api/"

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
