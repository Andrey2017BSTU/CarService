package com.example.carservice.carCreatingModule

sealed class AddingOrEditingState {
    object UnSuccessfulAdding : AddingOrEditingState()
    object UnSuccessfulEditing : AddingOrEditingState()
    object AnyViewEmpty : AddingOrEditingState()
    object IncorrectCurrentMileage : AddingOrEditingState()

    data class SuccessfulCarAdding (val name: String, val model: String, val year: String, val currentMileage: String) : AddingOrEditingState()

    data class SuccessfulCarEditing (val name: String, val model: String, val year: String, val currentMileage: String) : AddingOrEditingState()

}
