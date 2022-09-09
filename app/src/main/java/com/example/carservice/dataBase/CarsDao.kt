package com.example.carservice.dataBase

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

    @Query("SELECT * FROM cars_item_table")
    fun getAllCars(): Flow<List<CarsItemTable>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addCar(item: CarsItemTable): Long

    @Query("SELECT * FROM cars_item_table WHERE id = (:mCarId)")
    fun getCarById(mCarId: Int): Flow<CarsItemTable>

    @Query("DELETE FROM cars_item_table WHERE ID =(:mCarId)")
    suspend fun deleteCarById(mCarId: Int)

    @Query("SELECT current_mileage FROM cars_item_table WHERE id = (:mCarId)")
    fun getCurrentMileageById(mCarId: Int): Flow<Int>

    @Query("UPDATE cars_item_table SET current_mileage = (:mUpdatedCurrentMileage) WHERE id =(:mCarId)")
    suspend fun updateCurrentMileageById(mCarId: Int, mUpdatedCurrentMileage: Int)


}