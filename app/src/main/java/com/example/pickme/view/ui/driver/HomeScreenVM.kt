package com.example.pickme.view.ui.driver


import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pickme.data.model.Passenger
import com.example.pickme.data.model.PickUp
import com.example.pickme.data.repository.AuthRepository
import com.example.pickme.data.repository.PickUpRepository
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.compose.runtime.mutableFloatStateOf
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import com.example.pickme.viewModel.PassengerViewModel
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import java.time.format.DateTimeParseException

class HomeScreenVMFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return HomeScreenVM(context) as T
    }
}

class HomeScreenVM(context: Context) : ViewModel() {
    private val pickUpRepository = PickUpRepository()
    val pickUps: LiveData<List<PickUp>> = pickUpRepository.getLivePickUps()
    private val authRepository = AuthRepository()
    val workingHoursRange = mutableStateOf(0f..23f)
    val radius = mutableFloatStateOf(1f)
    private val currentLocation = mutableStateOf<LatLng?>(null)

    init {
        val sharedPref = context.getSharedPreferences("driver_filters", Context.MODE_PRIVATE)
        workingHoursRange.value =
            sharedPref.getFloat("startWorkingTime", 0f)..sharedPref.getFloat("endWorkingTime", 23f)
        radius.floatValue = sharedPref.getFloat("radius", 1f)
    }

    fun getPassengerData(id: String): LiveData<Passenger?> {
        val passengerData = MutableLiveData<Passenger?>()
        viewModelScope.launch {
            passengerData.value = authRepository.getPassengerData(id)
        }
        return passengerData
    }

    fun filterPickUps(context: Context, pickUps: List<PickUp>): List<PickUp> {
        getCurrentLocation(context)
        val passengerViewModel = PassengerViewModel()
        val formatter = DateTimeFormatter.ofPattern("MMM dd yyyy, hh:mm a", Locale.ENGLISH)
        val startWorkingTime = LocalTime.of(workingHoursRange.value.start.toInt(), 0)
        val endWorkingTime = LocalTime.of(workingHoursRange.value.endInclusive.toInt(), 0)
        return pickUps.filter {
            val dateTime = try {
                LocalDateTime.parse(it.dateAndTime.replace(" am", " AM").replace(" pm", " PM"), formatter)
            } catch (e: DateTimeParseException) {
                // Handle invalid date format or return null
                null
            } ?: return@filter false

            val pickUpTime = dateTime.toLocalTime()
            val distance = currentLocation.value?.let { currentLocation ->
                passengerViewModel.calculateDistance(
                    currentLocation,
                    it.pickUpLatLng
                )
            } ?: 0.0
            pickUpTime in startWorkingTime..endWorkingTime && (distance <= radius.floatValue)
        }
    }

    private fun getCurrentLocation(context: Context) {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Request location permissions
            ActivityCompat.requestPermissions(
                context as Activity,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                0
            )
        } else {
            // Permissions already granted, get the location
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    // Use the location object as needed
                    location?.let {
                        currentLocation.value = LatLng(it.latitude, it.longitude)
                    }
                }
        }
    }

    fun saveFilters(context: Context) {
        val editor = context.getSharedPreferences("driver_filters", Context.MODE_PRIVATE).edit()
        editor.putFloat("radius", radius.floatValue)
        editor.putFloat("startWorkingTime", workingHoursRange.value.start)
        editor.putFloat("endWorkingTime", workingHoursRange.value.endInclusive)
        editor.apply()
    }
}