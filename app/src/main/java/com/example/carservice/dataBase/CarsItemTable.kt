package com.example.carservice.dataBase

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "cars_item_table")
data class CarsItemTable(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "brand_name")
    val brand_name: String = "",
    @ColumnInfo(name = "model_name")
    val model_name: String = "",
    @ColumnInfo(name = "year")
    val year: String = "",
    @ColumnInfo(name = "current_mileage")
    val current_mileage: Int = 0,
    @ColumnInfo(name = "oil_last_service_mileage")
    val oil_last_service_mileage: Int = 0,
    @ColumnInfo(name = "oil_mileage")
    val oil_mileage: Int = 0,
    @ColumnInfo(name = "air_filt_last_service_mileage")
    val air_filt_last_service_mileage: Int = 0,
    @ColumnInfo(name = "air_filt_mileage")
    val air_filt_mileage: Int = 0,
    @ColumnInfo(name = "freez_last_service_mileage")
    val freez_last_service_mileage: Int = 0,
    @ColumnInfo(name = "freez_mileage")
    val freez_mileage: Int = 0,
    @ColumnInfo(name = "grm_last_service_mileage")
    val grm_last_service_mileage: Int = 0,
    @ColumnInfo(name = "grm_mileage")
    val grm_mileage: Int = 0


    )

{
    @Ignore
    var image_url: String? = ""
}


