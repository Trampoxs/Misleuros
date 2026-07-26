package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CoinCollectionDao {

    @Query("SELECT * FROM user_coin_collection")
    fun getAllUserCoins(): Flow<List<CoinCollectionEntity>>

    @Query("SELECT * FROM user_coin_collection WHERE coinCatalogId = :id")
    suspend fun getUserCoinById(id: String): CoinCollectionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertCoinStatus(entity: CoinCollectionEntity)

    @Query("DELETE FROM user_coin_collection WHERE coinCatalogId = :id")
    suspend fun deleteUserCoin(id: String)

    @Query("DELETE FROM user_coin_collection")
    suspend fun clearAllUserCoins()

    // Custom coins
    @Query("SELECT * FROM custom_catalog_coins")
    fun getAllCustomCoins(): Flow<List<CustomCatalogCoinEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCustomCoin(coin: CustomCatalogCoinEntity)

    @Query("DELETE FROM custom_catalog_coins WHERE id = :id")
    suspend fun deleteCustomCoin(id: String)
}
