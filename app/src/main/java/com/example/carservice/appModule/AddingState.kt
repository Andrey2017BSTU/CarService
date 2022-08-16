package com.example.carservice.appModule

sealed class AddingState {
    object UnSuccess : AddingState()
    object AnyViewEmpty : AddingState()
    object IncorrectCurrentMileage : AddingState()

    data class Success (val name: String, val model: String, val year: String, val currentMileage: String) : AddingState()

}
