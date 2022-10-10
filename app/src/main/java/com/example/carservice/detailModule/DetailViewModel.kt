package com.example.carservice.detailModule

import android.os.Bundle
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.carservice.appModule.AppRepository
import com.example.carservice.appModule.JsonResponseModel
import com.example.carservice.appModule.ServiceType
import com.example.carservice.appModule.SingleLiveEvent
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
    val warningsMutableLiveData = MutableLiveData<HashMap<ServiceType, Boolean>>()
    val updateMileageStateMutableLiveData = SingleLiveEvent<UpdateMileageState>()
    val editCarMutableLiveData = MutableLiveData<CarsItemTable>()

    private val warningsItem = HashMap<ServiceType, Boolean>()
    private var carIdFromBundle: Int = 0
    private val warningMileageToServiceConst = 1500


    fun detailViewModelInit(carBundle: Bundle?) {

        viewModelScope.launch {
            if (carBundle != null) {

                brandNameMutableLiveData.postValue(carBundle.getString("EXTRA_BRAND_NAME"))
                carIdFromBundle = carBundle.getInt("EXTRA_ID")

            }

            if (carBundle != null) {

                appRepository.getCarById(carIdFromBundle).collect {

                    carItemMutableLiveData.postValue(it)

                    warningsItem[ServiceType.OIL] =
                        checkWarningForService(
                            it.oil_mileage,
                            it.current_mileage,
                            it.oil_last_service_mileage
                        )
                    warningsItem[ServiceType.AIR_FILT] =
                        checkWarningForService(
                            it.air_filt_mileage,
                            it.current_mileage,
                            it.air_filt_last_service_mileage
                        )
                    warningsItem[ServiceType.FREEZ] =
                        checkWarningForService(
                            it.freez_mileage,
                            it.current_mileage,
                            it.freez_last_service_mileage
                        )
                    warningsItem[ServiceType.GRM] =
                        checkWarningForService(
                            it.grm_mileage,
                            it.current_mileage,
                            it.grm_last_service_mileage
                        )
                    warningsMutableLiveData.postValue(warningsItem)

                }


            }


        }


        viewModelScope.launch {
            if (carBundle != null) {
                appRepository.getCurrentMileageById(carIdFromBundle).collect {
                    carCurrentMileageMutableLiveData.postValue(it)
                }

            }
        }

        viewModelScope.launch {
            if (carBundle != null) {

                appRepository.getUriByQuery(
                    appRepository.getBrandNameByIdSingle(carIdFromBundle) + " " + appRepository.getModelNameByIdSingle(
                        carIdFromBundle
                    )
                )
                    .enqueue(object : Callback<JsonResponseModel> {
                        override fun onResponse(
                            call: Call<JsonResponseModel>,
                            response: Response<JsonResponseModel>
                        ) {

                            carURLMutableLiveData.postValue(getUrlFromResponse(response))

                        }

                        override fun onFailure(
                            call: Call<JsonResponseModel>,
                            t: Throwable
                        ) {

                        }

                        fun getUrlFromResponse(responseBody: Response<JsonResponseModel>) =
                            if (responseBody.body() != null && responseBody.body()!!.total != 0) {
                                responseBody.body()?.hits?.get(0)?.webformatURL
                            } else ""


                    })

            }


        }

        viewModelScope.launch {

            editCarMutableLiveData.postValue(appRepository.getCarByIdSingle(carIdFromBundle))

        }

        Log.v("VM_Detail", "Init")

    }

    private fun handleUpdateMileageState(state: UpdateMileageState) {
        viewModelScope.launch {
            when (state) {
                is UpdateMileageState.IncorrectEnter -> updateMileageStateMutableLiveData.postValue(
                    UpdateMileageState.IncorrectEnter
                )
                UpdateMileageState.EmptyOrNullEnter -> updateMileageStateMutableLiveData.postValue(
                    UpdateMileageState.EmptyOrNullEnter
                )
                is UpdateMileageState.SuccessUpdate -> {
                    finalUpdateMileage(state.updatedMileage)
                    updateMileageStateMutableLiveData.postValue(
                        UpdateMileageState.SuccessUpdate(
                            state.carId,
                            state.updatedMileage
                        )
                    )

                }

            }
        }


    }

    private fun finalUpdateMileage(updatedMileage: Int) {
        viewModelScope.launch {

            appRepository.updateCurrentMileageById(carIdFromBundle, updatedMileage)

        }
    }

    private fun checkWarningForService(
        serviceInterval: Int,
        currentMileage: Int,
        lastServiceMileage: Int
    ): Boolean {

        return if (lastServiceMileage == 0) false
        else serviceInterval - (currentMileage - lastServiceMileage) <= warningMileageToServiceConst

    }

    private fun finalRefreshMileage(serviceType: ServiceType) {

        viewModelScope.launch {

            when (serviceType) {
                ServiceType.OIL -> appRepository.updateOilMileageToService(
                    appRepository.getCurrentMileageByIdSingle(carIdFromBundle),
                    carIdFromBundle
                )
                ServiceType.AIR_FILT -> appRepository.updateAirFiltMileageToService(
                    appRepository.getCurrentMileageByIdSingle(carIdFromBundle),
                    carIdFromBundle
                )
                ServiceType.FREEZ -> appRepository.updateFreezMileageToService(
                    appRepository.getCurrentMileageByIdSingle(carIdFromBundle),
                    carIdFromBundle
                )
                ServiceType.GRM -> appRepository.updateGRMMileageToService(
                    appRepository.getCurrentMileageByIdSingle(carIdFromBundle),
                    carIdFromBundle
                )
            }

        }

    }

    fun onRefreshMileageButtonClick(serviceType: ServiceType) {

        when (serviceType) {

            ServiceType.OIL -> finalRefreshMileage(serviceType)
            ServiceType.AIR_FILT -> finalRefreshMileage(serviceType)
            ServiceType.FREEZ -> finalRefreshMileage(serviceType)
            ServiceType.GRM -> finalRefreshMileage(serviceType)

        }
    }


    fun onPositiveDelete(carId: Int) {

        viewModelScope.launch {
            appRepository.deleteCarById(carId)
        }

        Log.v("VM_Detail", "Positive_delete $carId")

    }

    fun onPositiveUpdateCurrentMileage(carId: Int, updatedCurrentMileage: String) {

        if (updatedCurrentMileage.isBlank()) handleUpdateMileageState(UpdateMileageState.EmptyOrNullEnter)
        else if (updatedCurrentMileage.toInt() <= 0) handleUpdateMileageState(UpdateMileageState.IncorrectEnter)
        else handleUpdateMileageState(
            UpdateMileageState.SuccessUpdate(
                carId,
                updatedCurrentMileage.toInt()
            )
        )


        Log.v("VM_Detail", "Positive_update $updatedCurrentMileage")

    }

    override fun onCleared() {
        super.onCleared()
        Log.i("VM_Detail", "vm Cleared")
    }


}