@file:Suppress("SpellCheckingInspection")

package com.example.carservice.pixabayAPI

import com.example.carservice.appModule.JsonResponseModel
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query


interface RetrofitService {

    @GET("/api/?key=14324202-2291f6c2aacfd3768d6f3ed60")
    fun getImageUriByQ(@Query("q") query: String): Call<JsonResponseModel>

    companion object {

        operator fun invoke(): RetrofitService {

            val baseUrl =
                "https://pixabay.com"


            return Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(RetrofitService::class.java)

        }


    }
}