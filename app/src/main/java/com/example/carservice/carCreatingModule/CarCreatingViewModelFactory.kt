package com.example.carservice.carCreatingModule

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.carservice.appModule.AppRepository


class CarCreatingViewModelFactory (private val appRepository: AppRepository) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(CarCreatingViewModel::class.java)) {
            Log.v("Car_creating_factory", "Car_creating vm created")
            CarCreatingViewModel(this.appRepository) as T

        } else {
            throw IllegalArgumentException("ViewModel Not Found")
        }


    }

}