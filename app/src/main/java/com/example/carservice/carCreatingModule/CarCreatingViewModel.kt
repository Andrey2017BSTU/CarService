package com.example.carservice.carCreatingModule

import android.os.Bundle
import android.text.Editable
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.carservice.appModule.AppRepository
import com.example.carservice.appModule.ServiceType
import com.example.carservice.appModule.SingleLiveEvent
import com.example.carservice.dataBase.CarsItemTable
import kotlinx.coroutines.launch
import java.util.*


class CarCreatingViewModel(private var appRepository: AppRepository) : ViewModel() {


    class ServiceList(
        var lastServiceMileage: Int = 0,
        var serviceInterval: Int = 0,
        var current_mileage_checker: Boolean = false
    )


    val imageUriMutableLiveData = MutableLiveData<String>()
    val brandNameMutableLiveData = MutableLiveData<List<String>>()
    val modelNameByBrandMutableLiveData = MutableLiveData<List<String>>()
    val updatableCurrentMileageMutableLiveDataSingle = SingleLiveEvent<Int>()
    val updatableCarBrandNameMutableLiveDataSingle = SingleLiveEvent<String>()
    val updatableCarModelNameMutableLiveDataSingle = SingleLiveEvent<String>()
    val updatableYearMutableLiveDataSingle = SingleLiveEvent<String>()
    val listOfYearsMutableLiveData = MutableLiveData<List<Int>>()

    val addingOrEditingStateMutableLiveData =
        SingleLiveEvent<AddingOrEditingState>()
    val checkBoxStateMutableLiveData =
        SingleLiveEvent<CheckBoxState>()

    private var isScreenRotated: Boolean = false
    private var updatableUri = ""
    private var updatableCurrentMileage = 0
    private val oilServiceList = ServiceList()
    private val airFiltServiceList = ServiceList()
    private val freezServiceList = ServiceList()
    private val grmServiceList = ServiceList()
    private var carIdFromBundle: Int = 0
    private var isEditingMode: Boolean = false


    fun carCreatingViewModelInit() {

        viewModelScope.launch {
            appRepository.getBrandsOfCars().collect { item ->
                brandNameMutableLiveData.postValue(item)
                listOfYearsMutableLiveData.postValue(getListOfYears())

            }

        }

        isEditingMode = false

    }

    fun carEditingViewModelInit(bundle: Bundle?) {

        viewModelScope.launch {
            if (bundle != null) {
                carIdFromBundle = bundle.getInt("EXTRA_ID")
            }
            if (bundle != null) {
                appRepository.getCarById(carIdFromBundle).collect {

                    if (!isScreenRotated){
                        imageUriMutableLiveData.postValue(it.image_uri.toString())
                    }else{
                        imageUriMutableLiveData.postValue(updatableUri)

                    }

                    if (!isScreenRotated) {
                        updatableCurrentMileageMutableLiveDataSingle.postValue(it.current_mileage)
                        updatableCarBrandNameMutableLiveDataSingle.postValue(it.brand_name)
                        updatableCarModelNameMutableLiveDataSingle.postValue(it.model_name)
                        updatableYearMutableLiveDataSingle.postValue(it.year)
                        listOfYearsMutableLiveData.postValue(getListOfYears())
                        Log.v("CreatingVm", "screen not rotated")
                    }


                }
            }
        }
        viewModelScope.launch {
            appRepository.getBrandsOfCars().collect { item ->
                brandNameMutableLiveData.postValue(item)

            }

        }

        isEditingMode = true

    }


    fun getModelNameByBrandId(brand_id: Int) {
        viewModelScope.launch {
            appRepository.getModelNameByBrandId(brand_id).collect { item ->
                modelNameByBrandMutableLiveData.postValue(item)

            }
        }

    }

    fun getModelNameByBrandName(brand_name: String) {
        viewModelScope.launch {
            appRepository.getBrandIdByBrandName(brand_name.uppercase()).collect { item ->
                getModelNameByBrandId(item)

            }
        }

    }


    private fun getListOfYears(): List<Int> {

        val years = mutableListOf<Int>()
        for (n in 1999..Calendar.getInstance().get(Calendar.YEAR)) {
            years.add(n)
        }
        return years

    }

    fun onImageAdded(_uri : String){
        updatableUri = _uri
        imageUriMutableLiveData.postValue(updatableUri)
    }


    private fun handleAddingOrEditingState(state: AddingOrEditingState) {
        viewModelScope.launch {

            when (state) {
                is AddingOrEditingState.SuccessfulCarAdding -> {

                    val carEntity = CarsItemTable(
                        brand_name = state.name,
                        model_name = state.model,
                        year = state.year,
                        current_mileage = state.currentMileage.toInt(),
                        image_uri = updatableUri,
                    )
                    finalCarAdding(carEntity)
                    addingOrEditingStateMutableLiveData.postValue(
                        AddingOrEditingState.SuccessfulCarAdding(
                            state.name,
                            state.model,
                            state.year,
                            state.currentMileage
                        )
                    )
                }
                is AddingOrEditingState.SuccessfulCarEditing -> {
                    val carEntity = CarsItemTable(
                        brand_name = state.name,
                        model_name = state.model,
                        year = state.year,
                        current_mileage = state.currentMileage.toInt(),
                        image_uri = updatableUri,
                    )
                    finalCarEditing(carEntity)
                    addingOrEditingStateMutableLiveData.postValue(
                        AddingOrEditingState.SuccessfulCarEditing(
                            state.name,
                            state.model,
                            state.year,
                            state.currentMileage
                        )
                    )
                }
                AddingOrEditingState.AnyViewEmpty -> addingOrEditingStateMutableLiveData.postValue(
                    AddingOrEditingState.AnyViewEmpty
                )
                AddingOrEditingState.IncorrectCurrentMileage -> addingOrEditingStateMutableLiveData.postValue(
                    AddingOrEditingState.IncorrectCurrentMileage
                )
                AddingOrEditingState.UnSuccessfulAdding -> addingOrEditingStateMutableLiveData.postValue(
                    AddingOrEditingState.UnSuccessfulAdding
                )
                AddingOrEditingState.UnSuccessfulEditing -> addingOrEditingStateMutableLiveData.postValue(
                    AddingOrEditingState.UnSuccessfulEditing
                )
            }
        }

    }

    private fun handleCheckBoxState(state: CheckBoxState) {
        viewModelScope.launch {
            when (state) {
                is CheckBoxState.IncorrectCurrentMileage -> checkBoxStateMutableLiveData.postValue(
                    CheckBoxState.IncorrectCurrentMileage
                )

                is CheckBoxState.IncorrectEnter -> checkBoxStateMutableLiveData.postValue(
                    CheckBoxState.IncorrectEnter
                )
                is CheckBoxState.SuccessCurrentMileageCheckBoxOn -> {
                    finalCheckBoxPositiveClick(
                        state.service_interval,
                        state.last_service_mileage,
                        state.current_mileage_checker,
                        state.serviceType
                    )
                    checkBoxStateMutableLiveData.postValue(
                        CheckBoxState.SuccessCurrentMileageCheckBoxOn(
                            state.service_interval,
                            state.last_service_mileage,
                            state.current_mileage_checker,
                            state.serviceType
                        )
                    )
                }
                is CheckBoxState.SuccessCurrentMileageCheckBoxOff -> {
                    finalCheckBoxPositiveClick(
                        state.service_interval,
                        state.last_service_mileage,
                        state.current_mileage_checker,
                        state.serviceType
                    )
                    checkBoxStateMutableLiveData.postValue(
                        CheckBoxState.SuccessCurrentMileageCheckBoxOff(
                            state.service_interval,
                            state.last_service_mileage,
                            state.current_mileage_checker,
                            state.serviceType
                        )
                    )
                }
            }


        }


    }

    private fun finalCheckBoxPositiveClick(
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
    }

    fun onCarAddingOrEditing(name: String, model: String, year: String, currentMileage: String) {
        val isAnyViewEmpty: Boolean =
            name == "" || model == "" || year == ""
        val isIncorrectCurrentMileage: Boolean =
            currentMileage == "" || currentMileage
                .toInt() == 0

        if (isAnyViewEmpty) {

            handleAddingOrEditingState(AddingOrEditingState.AnyViewEmpty)

        } else if (isIncorrectCurrentMileage) {

            handleAddingOrEditingState(AddingOrEditingState.IncorrectCurrentMileage)

        } else if (isEditingMode) {

            handleAddingOrEditingState(AddingOrEditingState.SuccessfulCarEditing(name, model, year, currentMileage))

        } else {

            handleAddingOrEditingState(AddingOrEditingState.SuccessfulCarAdding(name, model, year, currentMileage))

        }


    }

    private fun finalCarEditing(carItem: CarsItemTable) {
        viewModelScope.launch {
            val carItemVm = CarsItemTable(
                id = carIdFromBundle,
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
                grm_mileage = grmServiceList.serviceInterval,
                image_uri = updatableUri,
            )

            appRepository.editCar(carItemVm)

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
                grm_mileage = grmServiceList.serviceInterval,
                image_uri = updatableUri,
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


    fun onPositiveButtonClickedCheckBox(
        service_interval: String,
        last_service_mileage: String,
        current_mileage_checker: Boolean,
        currentMileageTextView: Editable,
        serviceType: ServiceType
    ) {


        val isIncorrectEnter: Boolean =
            service_interval.isBlank() || last_service_mileage.isBlank() || !current_mileage_checker && last_service_mileage.isBlank() || service_interval.toInt() <= 0 || last_service_mileage.toInt() <= 0


        val isIncorrectCurrentMileage: Boolean =
            currentMileageTextView.isBlank() || currentMileageTextView.toString()
                .toInt() <= 0
// TODO: Развернуть цепочку условий полиморфизмом
        if (!current_mileage_checker && (isIncorrectEnter)) {

            handleCheckBoxState(CheckBoxState.IncorrectEnter)

        } else if (current_mileage_checker && isIncorrectCurrentMileage) {

            handleCheckBoxState(CheckBoxState.IncorrectCurrentMileage)


        } else if (current_mileage_checker) {

            handleCheckBoxState(
                CheckBoxState.SuccessCurrentMileageCheckBoxOn(
                    service_interval.toInt(),
                    currentMileageTextView.toString().toInt(),
                    true,
                    serviceType
                )
            )

        } else {

            handleCheckBoxState(
                CheckBoxState.SuccessCurrentMileageCheckBoxOff(
                    service_interval.toInt(),
                    last_service_mileage.toInt(),
                    false,
                    serviceType
                )
            )

        }



        Log.i(
            "CarCreatingVm",
            "PositiveClickCheckBox $service_interval $last_service_mileage $current_mileage_checker $serviceType"
        )
    }


    fun onNeutralButtonClickedCheckBox(serviceType: ServiceType) {
        Log.i("CarCreatingVm", "NeutralClickCheckBox")
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

    fun onActivityRecreatedByFirstOpen() {
        isScreenRotated = false
    }

    fun onActivityRecreatedByScreenRotation() {
        isScreenRotated = true
    }


}