package com.example.carservice.carCreatingModule

import android.text.Editable
import android.util.Log
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.carservice.appModule.AddingState
import com.example.carservice.appModule.AppRepository
import com.example.carservice.appModule.ServiceType
import com.example.carservice.dataBase.AppDataBase
import com.example.carservice.dataBase.CarsItemTable
import com.example.carservice.pixabayAPI.RetrofitService
import kotlinx.coroutines.launch
import java.util.*


class CarCreatingViewModel : ViewModel() {
    private lateinit var appRepository: AppRepository

    class ServiceList(
        var lastServiceMileage: Int = 0,
        var serviceInterval: Int = 0,
        var current_mileage_checker: Boolean = false
    )


    val brandNameMutableLiveData = MutableLiveData<List<String>>()
    val modelNameByBrandMutableLiveData = MutableLiveData<List<String>>()
    val stateMutableLiveData = MutableLiveData<AddingState>()


    private var updatableCurrentMileage = 0
    private val oilServiceList = ServiceList()
    private val airFiltServiceList = ServiceList()
    private val freezServiceList = ServiceList()
    private val grmServiceList = ServiceList()


    fun init(dataBaseObj: AppDataBase, _ret: RetrofitService) {

        appRepository = AppRepository(dataBaseObj, _ret)

        viewModelScope.launch {
            appRepository.getBrandsOfCars().collect { item ->
                brandNameMutableLiveData.postValue(item)

            }

        }


    }


    fun startAlertDialog(_parentFragmentManager: FragmentManager, serviceType: ServiceType) {

        val alertDialogObj: DialogFragment = StartCheckBoxMileageAlertDialog(serviceType)
        alertDialogObj.show(_parentFragmentManager, "mileage")


    }

    fun getModelNameByBrand(brand_id: Int) {
        viewModelScope.launch {
            appRepository.getModelNameByBrandId(brand_id).collect { item ->
                modelNameByBrandMutableLiveData.postValue(item)

            }
        }

    }


    fun getListOfYears(): List<Int> {

        val years = mutableListOf<Int>()
        for (n in 1999..Calendar.getInstance().get(Calendar.YEAR)) {
            years.add(n)
        }
        return years

    }


    private fun handleState(state: AddingState) {
        viewModelScope.launch {

            when (state) {
                is AddingState.Success -> {

                    val carEntity = CarsItemTable(
                        brand_name = state.name,
                        model_name = state.model,
                        year = state.year,
                        current_mileage = state.currentMileage.toInt(),
                    )
                    finalCarAdding(carEntity)
                    stateMutableLiveData.postValue(
                        AddingState.Success(
                            state.name,
                            state.model,
                            state.year,
                            state.currentMileage
                        )
                    )
                }
                AddingState.AnyViewEmpty -> stateMutableLiveData.postValue(AddingState.AnyViewEmpty)
                AddingState.IncorrectCurrentMileage -> stateMutableLiveData.postValue(AddingState.IncorrectCurrentMileage)
                AddingState.UnSuccess -> stateMutableLiveData.postValue(AddingState.UnSuccess)
            }
        }

    }


    fun onCarAdding(name: String, model: String, year: String, currentMileage: String) {
        val isAnyViewEmpty: Boolean =
            name == "" || model == "" || year == ""
        val isIncorrectCurrentMileage: Boolean =
            currentMileage == "" || currentMileage
                .toInt() == 0

        if (isAnyViewEmpty) {

            handleState(AddingState.AnyViewEmpty)

        } else if (isIncorrectCurrentMileage) {

            handleState(AddingState.IncorrectCurrentMileage)

        } else {


            handleState(AddingState.Success(name, model, year, currentMileage))

        }


    }


    private fun finalCarAdding(carItem: CarsItemTable): Long {
        var numberOfAddedRow: Long = 0

        viewModelScope.launch {

            val carItemVm = CarsItemTable(
                brand_name = carItem.brand_name,
                model_name = carItem.model_name,
                year = carItem.year,
                current_mileage = updatableCurrentMileage,
                oil_last_service_mileage = getLastServiceMileage(ServiceType.OIL),
                oil_mileage = oilServiceList.serviceInterval,
                air_filt_last_service_mileage = getLastServiceMileage(ServiceType.AIR_FILT),
                air_filt_mileage = airFiltServiceList.serviceInterval,
                freez_last_service_mileage = getLastServiceMileage(ServiceType.FREEZ),
                freez_mileage = freezServiceList.serviceInterval,
                grm_last_service_mileage = getLastServiceMileage(ServiceType.GRM),
                grm_mileage = grmServiceList.serviceInterval
            )

            numberOfAddedRow = appRepository.addCarToDataBase(carItemVm)

        }

        return numberOfAddedRow


    }

    private fun getLastServiceMileage(type: ServiceType): Int {
        when (type) {

            ServiceType.OIL -> {
                return if (oilServiceList.current_mileage_checker) {
                    updatableCurrentMileage
                } else {
                    oilServiceList.lastServiceMileage

                }

            }

            ServiceType.AIR_FILT -> {
                return if (airFiltServiceList.current_mileage_checker) {
                    updatableCurrentMileage
                } else {
                    airFiltServiceList.lastServiceMileage
                }
            }

            ServiceType.FREEZ -> {
                return if (freezServiceList.current_mileage_checker) {
                    updatableCurrentMileage
                } else {
                    freezServiceList.lastServiceMileage

                }
            }

            ServiceType.GRM -> {
                return if (grmServiceList.current_mileage_checker) {
                    updatableCurrentMileage
                } else {
                    grmServiceList.lastServiceMileage

                }
            }
        }


    }

    fun onCheckBoxOff(serviceType: ServiceType) {
        zeroToMileageByCheckedId(serviceType)

    }

    fun onCurrentMileageChanged(s: Editable?) {

        updatableCurrentMileage = s.toString().toInt()

    }

    //TODO: БАГ: Если снять галочку перед сохранением, пробег до замены не обнуляется. upd: Исправленно, протестить
    //TODO: БАГ: Если изменить пробег после галочки но перед сохранением, последний пробег остаётся. upd: upd: Исправленно, протестить
    fun onPositiveButtonClickedCheckBox(
        service_interval: Int,
        last_service_mileage: Int,
        current_mileage_checker: Boolean,
        serviceType: ServiceType
    ) {


        when (serviceType) {
            ServiceType.OIL -> {
                oilServiceList.serviceInterval = service_interval
                oilServiceList.lastServiceMileage = last_service_mileage
                oilServiceList.current_mileage_checker = current_mileage_checker


            }
            ServiceType.AIR_FILT -> {
                airFiltServiceList.serviceInterval = service_interval
                airFiltServiceList.lastServiceMileage = last_service_mileage
                airFiltServiceList.current_mileage_checker = current_mileage_checker


            }
            ServiceType.FREEZ -> {
                freezServiceList.serviceInterval = service_interval
                freezServiceList.lastServiceMileage = last_service_mileage
                freezServiceList.current_mileage_checker = current_mileage_checker


            }
            ServiceType.GRM -> {
                grmServiceList.serviceInterval = service_interval
                grmServiceList.lastServiceMileage = last_service_mileage
                grmServiceList.current_mileage_checker = current_mileage_checker


            }

        }

        Log.i(
            "PositiveVM",
            "$service_interval $last_service_mileage $current_mileage_checker $serviceType"
        )
    }


    fun onNeutralButtonClickedCheckBox(serviceType: ServiceType) {
        Log.i("NeutralVM", "Click")
        zeroToMileageByCheckedId(serviceType)


    }

    private fun zeroToMileageByCheckedId(serviceType: ServiceType) {

        when (serviceType) {
            ServiceType.OIL -> {

                oilServiceList.serviceInterval = 0
                oilServiceList.lastServiceMileage = 0


            }
            ServiceType.AIR_FILT -> {

                airFiltServiceList.serviceInterval = 0
                airFiltServiceList.lastServiceMileage = 0
            }
            ServiceType.FREEZ -> {

                freezServiceList.serviceInterval = 0
                freezServiceList.lastServiceMileage = 0
            }
            ServiceType.GRM -> {
                grmServiceList.serviceInterval = 0
                grmServiceList.lastServiceMileage = 0
            }

        }

    }


}