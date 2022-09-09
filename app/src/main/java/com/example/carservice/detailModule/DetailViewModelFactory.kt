package com.example.carservice.detailModule

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.carservice.appModule.AppRepository

class DetailViewModelFactory(private val appRepository: AppRepository) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(DetailViewModel::class.java)) {
            Log.v("Detail_factory", "Detail vm created")
            DetailViewModel(this.appRepository) as T

        } else {
            throw IllegalArgumentException("ViewModel Not Found")
        }


    }

}