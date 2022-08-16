package com.example.carservice.dataBase

import androidx.room.*

@Entity(
    tableName = "model_table",
    foreignKeys = [ForeignKey(
        entity = BrandTable::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("brand_id"),
        onDelete = ForeignKey.NO_ACTION
    )], indices = [Index(value = arrayOf("brand_id")), Index(value = arrayOf("model_name"))]
)
data class ModelTable(
    @PrimaryKey(autoGenerate = true)
    val model_id: Int,
    @ColumnInfo(name = "model_name")
    val model_name: String = "",
    @ColumnInfo(name = "brand_id")
    val brand_id: Int = 0
)