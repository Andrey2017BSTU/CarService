package com.example.carservice.menuModule

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.carservice.appModule.AppRepository
import com.example.carservice.dataBase.CarsItemTable
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MenuViewModel(private var appRepository: AppRepository) : ViewModel() {


    val recyclerListLiveData = MutableLiveData<List<CarsItemTable>>()


    fun menuViewModelInit() {
        viewModelScope.launch {
            appRepository.getCarsFromDataBase().collect { item ->

                recyclerListLiveData.postValue(item)


                for (n in item.indices) {

                    Log.v("Item_name", item[n].brand_name)

                    val brandModelNamesQuery = item[n].brand_name + "+" + item[n].model_name

                    appRepository.getUriByQuery(brandModelNamesQuery)
                        .enqueue(object : Callback<JsonCl> {
                            override fun onResponse(
                                call: Call<JsonCl>,
                                response: Response<JsonCl>
                            ) {

                                item[n].image_url = getUrlFromResponse(response)
                                recyclerListLiveData.postValue(item)

                            }

                            override fun onFailure(call: Call<JsonCl>, t: Throwable) {

                            }
                        })

                }

            }

        }
    }


    fun getUrlFromResponse(responseBody: Response<JsonCl>?) = if (responseBody!!.body()!!.total != 0) responseBody.body()?.hits?.get(0)?.webformatURL else ""


    override fun onCleared() {
        super.onCleared()
        Log.v("VM_MENU", "vm Cleared")
    }


}