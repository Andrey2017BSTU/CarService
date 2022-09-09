package com.example.carservice.carCreatingModule

import com.example.carservice.appModule.ServiceType

sealed class CheckBoxState {
    object IncorrectEnter : CheckBoxState()
    object IncorrectCurrentMileage : CheckBoxState()
    data class SuccessCurrentMileageCheckBoxOn(
        val service_interval: Int,
        val last_service_mileage: Int,
        val current_mileage_checker: Boolean,
        val serviceType: ServiceType
    ) : CheckBoxState()

    data class SuccessCurrentMileageCheckBoxOff(
        val service_interval: Int,
        val last_service_mileage: Int,
        val current_mileage_checker: Boolean,
        val serviceType: ServiceType
    ) : CheckBoxState()

}
