package com.example.carservice.detailModule

import android.os.Bundle
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.carservice.appModule.AppRepository
import com.example.carservice.appModule.JsonResponseModel
import com.example.carservice.dataBase.CarsItemTable
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailViewModel(private var appRepository: AppRepository) : ViewModel() {
    val brandNameMutableLiveData = MutableLiveData<String>()
    val carItemMutableLiveData = MutableLiveData<CarsItemTable>()
    val carURLMutableLiveData = MutableLiveData<String?>()
    val carCurrentMileageMutableLiveData = MutableLiveData<Int>()
    private var carId: Int = 0

    fun detailViewModelInit(carBundle: Bundle?) {
        viewModelScope.launch {
            if (carBundle != null) {
                brandNameMutableLiveData.postValue(carBundle.getString("EXTRA_BRAND_NAME"))
                carId = carBundle.getInt("EXTRA_ID")
////                modelNameMutableLiveData.postValue(carBundle.getString("EXTRA_MODEL_NAME"))
////                yearMutableLiveData.postValue(carBundle.getString("EXTRA_YEAR"))
////                currentMileageMutableLiveData.postValue(carBundle.getInt("EXTRA_CURRENT_MILEAGE"))
////                oilMileageMutableLiveData.postValue(carBundle.getInt("EXTRA_OIL_MILEAGE"))
////                airFiltMileageMutableLiveData.postValue(carBundle.getInt("EXTRA_AIR_FILT_MILEAGE"))
////                freezMileageMutableLiveData.postValue(carBundle.getInt("EXTRA_FREEZ_MILEAGE"))
////                grmMileageMutableLiveData.postValue(carBundle.getInt("EXTRA_GRM_MILEAGE"))
//                val carItem = CarsItemTable(
//                    brand_name = carBundle.getString("EXTRA_BRAND_NAME").toString(),
//                    model_name = carBundle.getString("EXTRA_MODEL_NAME").toString(),
//                    year = carBundle.getString("EXTRA_YEAR").toString(),
//                    current_mileage = carBundle.getInt("EXTRA_CURRENT_MILEAGE"),
//                    oil_last_service_mileage = carBundle.getInt("EXTRA_OIL_LAST_SERVICE"),
//                    oil_mileage = carBundle.getInt("EXTRA_OIL_MILEAGE"),
//                    air_filt_last_service_mileage = carBundle.getInt("EXTRA_AIR_FILT_LAST_SERVICE"),
//                    air_filt_mileage = carBundle.getInt("EXTRA_AIR_FILT_MILEAGE"),
//                    freez_last_service_mileage = carBundle.getInt("EXTRA_FREEZ_LAST_SERVICE"),
//                    freez_mileage = carBundle.getInt("EXTRA_FREEZ_MILEAGE"),
//                    grm_last_service_mileage = carBundle.getInt("EXTRA_GRM_LAST_SERVICE"),
//                    grm_mileage = carBundle.getInt("EXTRA_GRM_MILEAGE")
//
//                )
//                carItemMutableLiveData.postValue(carItem)
//
//
            }

            if (carBundle != null) {

                appRepository.getCarById(carId).collect {

                    carItemMutableLiveData.postValue(it)
                    // carCurrentMileageMutableLiveData.postValue(it.current_mileage)

                    appRepository.getUriByQuery(it.brand_name + "+" + it.model_name)
                        .enqueue(object : Callback<JsonResponseModel> {
                            override fun onResponse(
                                call: Call<JsonResponseModel>,
                                response: Response<JsonResponseModel>
                            ) {


                                carURLMutableLiveData.postValue(getUrlFromResponse(response))

                            }

                            override fun onFailure(call: Call<JsonResponseModel>, t: Throwable) {

                            }

                            fun getUrlFromResponse(responseBody: Response<JsonResponseModel>) =
                                if (responseBody.body() != null && responseBody.body()!!.total != 0) {
                                    responseBody.body()?.hits?.get(0)?.webformatURL
                                } else ""


                        })

                }


            }


        }


        viewModelScope.launch {
            if (carBundle != null) {
                appRepository.getCurrentMileageById(carId).collect {
                    carCurrentMileageMutableLiveData.postValue(it)
                }

            }
        }


    }

    fun onPositiveDelete(carId: Int) {
        viewModelScope.launch {
            appRepository.deleteCarById(carId)
        }

        Log.v("VM_Detail", "Positive_delete $carId")

    }

    fun onPositiveUpdateCurrentMileage(carId: Int, updatedCurrentMileage: Int) {

        viewModelScope.launch{
            appRepository.updateCurrentMileageById(carId,updatedCurrentMileage)

        }

        Log.v("VM_Detail", "Positive_update $updatedCurrentMileage")

    }

    override fun onCleared() {
        super.onCleared()
        Log.i("VM_Detail", "vm Cleared")
    }
}