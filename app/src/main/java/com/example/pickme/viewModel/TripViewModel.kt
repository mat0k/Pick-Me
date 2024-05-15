package com.example.pickme.viewModel


import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import java.time.LocalDate

class TripViewModel : ViewModel() {

    val tripTitle: MutableState<String> = mutableStateOf("")

    val seats: MutableState<Int> = mutableStateOf(0)
    val tripStartTitle: MutableState<String> = mutableStateOf("Starting point")
    val tripDestTitle: MutableState<String> = mutableStateOf("Destination")

    val tripStartLatLng: MutableState<LatLng> = mutableStateOf(LatLng(33.8938,35.5018))
    val tripDestLatLng: MutableState<LatLng> = mutableStateOf(LatLng(33.8938,35.5018))

    val searchedTripStartLatLng: MutableState<LatLng> = mutableStateOf(LatLng(33.8938,35.5018))
    val searchedTripDestLatLng:  MutableState<LatLng> = mutableStateOf(LatLng(33.8938,35.5018))

    val distance:   MutableState<Double> = mutableStateOf(0.0)

    val tripDateAndTime: MutableState<String> = mutableStateOf("")

    val pickedDate: MutableState<LocalDate> = mutableStateOf(LocalDate.now())

    val selectedTripId: MutableState<String> = mutableStateOf("")
    fun setTripTitle(title: String){
        tripTitle.value = title
    }
    fun setPickUpTitle(title: String) {
        tripStartTitle.value = title
    }

    fun setTargetTitle(title: String) {
        tripDestTitle.value = title
    }

    fun setPickUpLatLng(title: LatLng){
        tripStartLatLng.value = title
    }
    fun setTargetLatLng(title: LatLng){
        tripDestLatLng.value = title
    }
    fun setDistance(title: Double){
        distance.value= title
    }
    fun setTripDateAndTime(dateTime: String) {
        tripDateAndTime.value = dateTime
    }
    fun setSearchedTripStartLatLng(title: LatLng){
        searchedTripStartLatLng.value = title
    }
    fun setSearchedTripDestLatLng(title: LatLng){
        searchedTripDestLatLng.value = title
    }
    fun setFormattedDate(date: LocalDate){
        pickedDate.value = date
    }
    fun setSelectedTripId(id: String){
        selectedTripId.value = id
    }
}
