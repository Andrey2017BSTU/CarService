package com.example.carservice.appModule

import com.example.carservice.dataBase.AppDataBase
import com.example.carservice.dataBase.CarsItemTable
import com.example.carservice.pixabayAPI.RetrofitService
import kotlinx.coroutines.flow.Flow
import retrofit2.http.Query

class AppRepository(
    private val dataBaseObj: AppDataBase,
    private val _retrofitService: RetrofitService

) {


    fun getBrandsOfCars(): Flow<List<String>> = dataBaseObj.dao().getAllBrands()

    fun getCarsFromDataBase(): Flow<List<CarsItemTable>> = dataBaseObj.dao().getAllCars()

    fun getModelNameByBrandId(brand_id: Int): Flow<List<String>> =
        dataBaseObj.dao().getModelNameByBrandId(brand_id)

    suspend fun addCarToDataBase(carItem: CarsItemTable): Long = dataBaseObj.dao().addCar(carItem)

    fun getCarById(carId: Int): Flow<CarsItemTable> = dataBaseObj.dao().getCarById(carId)


    fun getUriByQuery(@Query("q") q: String) = _retrofitService.getImageUriByQ(q)

    suspend fun deleteCarById(carId: Int) = dataBaseObj.dao().deleteCarById(carId)

    fun getCurrentMileageById(carId: Int) = dataBaseObj.dao().getCurrentMileageById(carId)

    suspend fun updateCurrentMileageById (carId: Int, updatedCurrentMileage: Int) = dataBaseObj.dao().updateCurrentMileageById(carId,updatedCurrentMileage)


}