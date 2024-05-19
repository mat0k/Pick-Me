package com.example.pickme.viewModel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng

class PickUpViewModel : ViewModel() {

    // data for pick up
    val pickUpTitle: MutableState<String> = mutableStateOf("")
    val targetTitle: MutableState<String> = mutableStateOf("")

    val pickUpLatLng: MutableState<LatLng> = mutableStateOf(LatLng(0.0,0.0))
    val targetLatLng: MutableState<LatLng> = mutableStateOf(LatLng(0.0, 0.0))

    val distance:   MutableState<Double> = mutableStateOf(0.0)

    val dateAndTime: MutableState<String> = mutableStateOf("")

    val pickUpPrice: MutableState<Double> = mutableStateOf(1.0)
    // data for preview pick up
    val prevPickUpTitle:   MutableState<String> = mutableStateOf("")
    val prevTargetTitle:   MutableState<String> = mutableStateOf("")
    val prevPickUPLatLng:  MutableState<LatLng> = mutableStateOf(LatLng(0.0,0.0))
    val prevTargetLatLng:  MutableState<LatLng> = mutableStateOf(LatLng(0.0,0.0))
    val prevDistance :     MutableState<Double> = mutableStateOf(0.0)
    val dateDialogState:    MutableState<Boolean> = mutableStateOf(false)

    fun setPickUpTitle(title: String) {
        pickUpTitle.value = title
    }

    fun setTargetTitle(title: String) {
        targetTitle.value = title
    }

    fun setPickUpLatLng(latLng: LatLng){
        pickUpLatLng.value = latLng
    }
    fun setTargetLatLng(latLng: LatLng){
        targetLatLng.value = latLng
    }
    fun setDistance(dist: Double){
        distance.value= dist
    }
    fun setDateAndTime(dateTime: String) {
        dateAndTime.value = dateTime
    }
    fun setPickUpPrice(price: Double){
        pickUpPrice.value= price
    }


    fun setPrevPickUpTitle(title: String) {
        prevPickUpTitle.value = title
    }

    fun setPrevTargetTitle(title: String) {
        prevTargetTitle.value = title
    }

    fun setPrevPickUPLatLng(latLng: LatLng){
        prevPickUPLatLng.value = latLng
    }

    fun setPrevTargetLatLng(latLng: LatLng){
        prevTargetLatLng.value = latLng
    }

    fun setPrevDistance(distance: Double){
        prevDistance.value = distance
    }

    fun setDialogState(state: Boolean){
        dateDialogState.value= state
    }
}
