package com.example.data.api

import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface NumistaApiService {
    @GET("v3/types")
    suspend fun searchTypes(
        @Header("Numista-API-Key") apiKey: String,
        @Query("q") query: String,
        @Query("category") category: String = "coin",
        @Query("count") count: Int = 20
    ): NumistaSearchResponse

    @GET("v3/types/{id}")
    suspend fun getTypeDetail(
        @Header("Numista-API-Key") apiKey: String,
        @retrofit2.http.Path("id") id: Long,
        @Query("lang") lang: String = "es"
    ): NumistaTypeDetail
}

data class NumistaTypeDetail(
    val id: Long? = null,
    val title: String? = null,
    val obverse: NumistaSide? = null,
    val reverse: NumistaSide? = null
)

data class NumistaSide(
    val picture: String? = null
)

data class NumistaSearchResponse(
    val count: Int? = null,
    val types: List<NumistaType>? = null
)

data class NumistaType(
    val id: Long? = null,
    val title: String? = null,
    val min_year: Int? = null,
    val max_year: Int? = null,
    val obverse_thumbnail: String? = null,
    val reverse_thumbnail: String? = null,
    val issuer: NumistaIssuer? = null
)

data class NumistaIssuer(
    val code: String? = null,
    val name: String? = null
)
