package com.example.carservice.dataBase

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface CarsDao {
    @Query("SELECT brand_name  FROM brand_table")
    fun getAllBrands(): Flow<List<String>>

    @Query("SELECT model_name FROM model_table WHERE brand_id = :mBrandId")
    fun getModelNameByBrandId(mBrandId: Int): Flow<List<String>>

    @Query("SELECT id FROM brand_table WHERE brand_name = :mBrandName")
    fun getBrandIdByBrandName(mBrandName: String): Flow<Int>

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

    @Query("SELECT current_mileage FROM cars_item_table WHERE id = (:mCarId)")
    suspend fun getCurrentMileageByIdSingle(mCarId: Int): Int

    @Query("SELECT brand_name FROM cars_item_table WHERE id = (:mCarId) ")
    fun getBrandNameByCarId(mCarId: Int): Flow<String>

    @Query("SELECT model_name FROM cars_item_table WHERE id = (:mCarId) ")
    fun getModelNameById(mCarId: Int): Flow<String>

    @Query("UPDATE cars_item_table SET current_mileage = (:mUpdatedCurrentMileage) WHERE id =(:mCarId)")
    suspend fun updateCurrentMileageById(mCarId: Int, mUpdatedCurrentMileage: Int)

    @Query("UPDATE cars_item_table SET oil_last_service_mileage = (:updatedMileage) WHERE id = (:mCarId)")
    suspend fun updateOilMileageToService(updatedMileage: Int, mCarId: Int)

    @Query("UPDATE cars_item_table SET air_filt_last_service_mileage = (:updatedMileage) WHERE id = (:mCarId)")
    suspend fun updateAirFiltMileageToService(updatedMileage: Int, mCarId: Int)

    @Query("UPDATE cars_item_table SET freez_last_service_mileage = (:updatedMileage) WHERE id = (:mCarId)")
    suspend fun updateFreezMileageToService(updatedMileage: Int, mCarId: Int)

    @Query("UPDATE cars_item_table SET grm_last_service_mileage = (:updatedMileage) WHERE id = (:mCarId)")
    suspend fun updateGRMMileageToService(updatedMileage: Int, mCarId: Int)

    @Query("SELECT brand_name FROM cars_item_table WHERE id = (:mCarId) ")
    suspend fun getBrandNameByCarIdSingle(mCarId: Int): String

    @Query("SELECT model_name FROM cars_item_table WHERE id = (:mCarId) ")
    suspend fun getModelNameByIdSingle(mCarId: Int): String

    @Query("SELECT * FROM cars_item_table WHERE id = (:mCarId)")
    suspend fun getCarByIdSingle(mCarId: Int): CarsItemTable

    @Update
    suspend fun editCar(carsItem: CarsItemTable)




}