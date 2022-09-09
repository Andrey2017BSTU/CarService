package com.example.carservice.appModule

data class JsonResponseModel(
    val hits: List<Hit>,
    val total: Int,
    val totalHits: Int
)