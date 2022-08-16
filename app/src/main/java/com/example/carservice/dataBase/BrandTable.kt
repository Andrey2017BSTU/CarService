package com.example.carservice.dataBase

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "brand_table")
data class BrandTable(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "brand_name")
    val brand_name: String
)