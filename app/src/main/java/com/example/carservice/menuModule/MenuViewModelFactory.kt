package com.example.carservice.menuModule

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.carservice.appModule.AppRepository

class MenuViewModelFactory(private val appRepository: AppRepository) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(MenuViewModel::class.java)) {
            Log.v("Menu_factory", "Menu vm created")
            MenuViewModel(this.appRepository) as T

        } else {
            throw IllegalArgumentException("ViewModel Not Found")
        }


    }

}