package com.example.carservice.detailModule

sealed class UpdateMileageState{

    object IncorrectEnter: UpdateMileageState()

    object EmptyOrNullEnter: UpdateMileageState()

    data class SuccessUpdate(val carId: Int,val updatedMileage: Int): UpdateMileageState()


}
