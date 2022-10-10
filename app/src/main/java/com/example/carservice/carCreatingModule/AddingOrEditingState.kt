package com.example.carservice.carCreatingModule

sealed class AddingOrEditingState {
    object UnSuccessfulAdding : AddingOrEditingState()
    object UnSuccessfulEditing : AddingOrEditingState()
    object AnyViewEmpty : AddingOrEditingState()
    object IncorrectCurrentMileage : AddingOrEditingState()

    data class SuccessCarAdding (val name: String, val model: String, val year: String, val currentMileage: String) : AddingOrEditingState()

    data class SuccessCarEditing (val name: String, val model: String, val year: String, val currentMileage: String) : AddingOrEditingState()

}
