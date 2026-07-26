package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_coin_collection")
data class CoinCollectionEntity(
    @PrimaryKey val coinCatalogId: String,
    val status: String, // "OWNED", "MISSING", "WISHLIST"
    val quantity: Int = 1,
    val grade: String = "CIRC", // "UNC", "EBC", "MBC", "BC", "CIRC"
    val notes: String = "",
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "custom_catalog_coins")
data class CustomCatalogCoinEntity(
    @PrimaryKey val id: String,
    val countryCode: String,
    val countryName: String,
    val year: Int,
    val denominationCode: String,
    val title: String,
    val description: String = "",
    val isCommemorative: Boolean = true,
    val mintageInfo: String = ""
)
