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


    fun getBrandsOfCars(): Flow<List<String>> {
        return dataBaseObj.dao().getAllBrands()


    }

    fun getCarsFromDataBase(): Flow<List<CarsItemTable>> {
        return dataBaseObj.dao().getAllCars()

    }

    fun getModelNameByBrandId(brand_id: Int): Flow<List<String>> {
        return dataBaseObj.dao().getModelNameByBrandId(brand_id)

    }


    suspend fun addCarToDataBase(carItem: CarsItemTable): Long {

        return dataBaseObj.dao().addCar(carItem)

    }


    fun getUriByQuery(@Query("q") q: String) = _retrofitService.getImageUriByQ(q)


}