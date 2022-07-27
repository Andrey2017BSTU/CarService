package com.example.carservice

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CarsDao {
    @Query("SELECT brand_name  FROM brand_table")
    fun getAllBrands(): Flow<List<String>>

    @Query("SELECT model_name FROM model_table WHERE brand_id = :mBrandId")
    fun getModelNameByBrandId(mBrandId: Int): Flow<List<String>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addCar(item: CarsItemTable): Long
}