package com.example.carservice

import kotlinx.coroutines.flow.Flow

class AppRepository(private val dataBaseObj: AppDataBase) {
    fun getBrand(): Flow<List<String>> {
        return dataBaseObj.dao().getAllBrands()


    }

    fun getModelNameByBrandId(brand_id: Int): Flow<List<String>> {
        return dataBaseObj.dao().getModelNameByBrandId(brand_id)

    }

    suspend fun addCar(carItem: CarsItemTable): Long {

        return dataBaseObj.dao().addCar(carItem)

    }
}