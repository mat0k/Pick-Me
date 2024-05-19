package com.example.pickme.viewModel

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.core.app.ActivityCompat
import com.example.pickme.data.model.DriverData
import com.example.pickme.data.model.GoogleMapDTO
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

class PassengerViewModel {


    fun calculateDistance(origin: LatLng, destination: LatLng): Double {
        val earthRadius = 6371 // Radius of the earth in kilometers
        // Convert latitude and longitude to radians
        val latOrigin = Math.toRadians(origin.latitude)
        val lonOrigin = Math.toRadians(origin.longitude)
        val latDestination = Math.toRadians(destination.latitude)
        val lonDestination = Math.toRadians(destination.longitude)

        // Calculate the differences between the coordinates
        val dLat = latDestination - latOrigin
        val dLon = lonDestination - lonOrigin

        // Apply the Haversine formula
        val a = sin(dLat / 2).pow(2) + cos(latOrigin) * cos(latDestination) * sin(dLon / 2).pow(2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        val distance = earthRadius * c

        // Format the distance to two decimal places
        return String.format("%.2f", distance).toDouble()
    }


    fun reverseGeocode(latLng: LatLng, context: Context): String? {
        val latitude = latLng.latitude
        val longitude = latLng.longitude

        return try {
            val geoCoder = Geocoder(context, Locale.getDefault())
            val addresses: List<Address>? = geoCoder.getFromLocation(latitude, longitude, 1)

            if (!addresses.isNullOrEmpty()) {
                val address = addresses[0]
                val addressLine = address.getAddressLine(0) ?: ""
                val streetName = address.thoroughfare ?: ""
                val fullAddress =
                    if (streetName.isNotEmpty()) "$streetName, $addressLine" else addressLine

                // Split the full address by commas
                val parts = fullAddress.split(",")

                // Check each part for lowercase letters
                val cleanedParts = parts.map { part ->
                    val containsLowercase = part.any { it.isLowerCase() }
                    if (!containsLowercase) {
                        // Remove the part if it doesn't contain any lowercase letters
                        null
                    } else {
                        part.trim()
                    }
                }.filterNotNull()

                cleanedParts.joinToString(", ") // Join the cleaned parts back with commas
            } else {
                null
            }
        } catch (e: IOException) {
            // Handle network or I/O error
            Log.e("ReverseGeocode", "IOException: ${e.message}")
            null
        } catch (e: IllegalArgumentException) {
            // Handle invalid latitude or longitude
            Log.e("ReverseGeocode", "IllegalArgumentException: ${e.message}")
            null
        } catch (e: Exception) {
            // Handle other exceptions
            Log.e("ReverseGeocode", "Exception: ${e.message}")
            null
        }
    }


    fun getCurrentLocation(
        context: Context,
        onLocationReceived: (Double, Double) -> Unit,
        function: () -> Unit
    ) {
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
                    //  Log.i("xxxx", "Location: ${location?.latitude}, ${location?.longitude}")
                    location?.let {
                        onLocationReceived(it.latitude, it.longitude)
                    } ?: WhenItIsNull(context)
                }
        }
    }


    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false

        return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
    }


    fun WhenItIsNull(context: Context) {
        val dialogBuilder = AlertDialog.Builder(context)
        dialogBuilder.apply {
            setTitle("Location Information")
            setMessage("Unable to retrieve your location. Please ensure that your GPS is turned on and that you have a good signal reception.")
            setPositiveButton("OK") { dialog: DialogInterface, _: Int ->
                dialog.dismiss()
            }
        }
        val dialog = dialogBuilder.create()
        dialog.show()
    }


    fun ShowWifiProblemDialog(context: Context) {
        val dialogBuilder = AlertDialog.Builder(context)
        dialogBuilder.apply {
            setTitle("Wifi Information")
            setMessage("Unable to connect to the internet. Please ensure that your WiFi is turned on and that you have a good signal reception.")
            setPositiveButton("OK") { dialog: DialogInterface, _: Int ->
                dialog.dismiss()
            }
        }
        val dialog = dialogBuilder.create()
        dialog.show()
    }


    @Composable
    fun InfoDialog(pickUpViewModel: PickUpViewModel, context: Context) {

        val text = "Pickup Title: ${pickUpViewModel.pickUpTitle.value} \n" +
                "Target Title: ${pickUpViewModel.targetTitle.value} \n" +
                "Pickup Latitude: ${pickUpViewModel.pickUpLatLng.value.latitude}\n" +
                "Pickup Longitude: ${pickUpViewModel.pickUpLatLng.value.longitude}\n" +
                "Target Latitude: ${pickUpViewModel.targetLatLng.value.latitude}\n" +
                "Target Longitude: ${pickUpViewModel.targetLatLng.value.longitude}\n" +
                "Distance: ${pickUpViewModel.distance.value} Km\n" +
                "Date and Time: ${pickUpViewModel.dateAndTime.value}"

        val dialogBuilder = AlertDialog.Builder(context)
        dialogBuilder.apply {
            setTitle("Information")
            setMessage(text)
            setPositiveButton("OK") { dialog: DialogInterface, _: Int ->
                dialog.dismiss()
            }
        }
        val dialog = dialogBuilder.create()
        dialog.show()
    }


    fun filterTrips(
        trips: List<Map<String, Any>>,
        isDriverVerified: Boolean,
        formattedDate: String,
        minRating: Int,
        seats: Int,
        searchRadius: Int,
        time: String,
        timeRange: Int,
        tripViewModel: TripViewModel
    ): List<Map<String, Any>> {
        // Convert the input time string to a LocalTime object
        val formatter = DateTimeFormatter.ofPattern("hh:mm a")
        val inputTime = LocalTime.parse(time, formatter)
        val startLatLng = tripViewModel.tripStartLatLng.value
        //val targetLatLng= tripViewModel.tripDestLatLng.value
        val passViewModel = PassengerViewModel()

        return trips.filter { trip ->
            val tripDate = trip["date"] as? String ?: ""
            val driverVerified = trip["verified"] as? Boolean ?: false
            val driverRating = trip["rate"]?.toString()?.toIntOrNull() ?: 0
            val tripSeats = trip["seats"]?.toString()?.toIntOrNull() ?: 1

            // Convert the trip time string to a LocalTime object
            val tripTimeStr = trip["time"] as? String ?: ""
            val fixedTimeStr = tripTimeStr.replace(" am", " AM").replace(" pm", " PM")
            val tripTime = LocalTime.parse(fixedTimeStr, formatter)

            // Calculate the difference in minutes between the input time and the trip time
            val minutesDiff = ChronoUnit.MINUTES.between(inputTime, tripTime).toInt()

            // Get the starting location of the trip
            val tripStartLatLngMap = trip["startingLatLng"] as? Map<String, Double> ?: emptyMap()
            val tripStartLatLng = LatLng(
                tripStartLatLngMap["latitude"] ?: 0.0,
                tripStartLatLngMap["longitude"] ?: 0.0
            )
            // Calculate the distance between the start location and the trip's starting location
            val distance = passViewModel.calculateDistance(startLatLng, tripStartLatLng)
            //  Log.i("xxxx","--- starting lat lng: $startLatLng, trip start lat lng: $tripStartLatLng, distance: $distance, and search radius: $searchRadius so display should be ${(searchRadius == 6 || distance <= searchRadius)}")

            // Check all conditions
            (driverVerified == isDriverVerified)
                    && (tripDate == formattedDate)
                    && (driverRating >= minRating)
                    && (tripSeats >= seats)
                    && (timeRange == 0 || (minutesDiff in -timeRange * 60..timeRange * 60)) // Convert timeRange from hours to minutes
                    && (searchRadius == 6 || distance <= searchRadius)
        }
    }


    fun calculateMidPoint(start: LatLng, target: LatLng): LatLng {
        val midLat = (start.latitude + target.latitude) / 2
        val midLng = (start.longitude + target.longitude) / 2
        return LatLng(midLat, midLng)
    }


    fun getDirectionURL(origin: LatLng, dest: LatLng): String {
        return "https://maps.googleapis.com/maps/api/directions/json" +
                "?origin=${origin.latitude},${origin.longitude}" +
                "&destination=${dest.latitude},${dest.longitude}" +
                "&mode=driving" +
                "&key=AIzaSyC4S_Vu2iCL1MjlKBFTpHiYPds7OoYZTYc"
    }


    fun updatePolyline(
        origin: LatLng,
        destination: LatLng,
        setPolylinePoints: (List<LatLng>) -> Unit,
        setDistance: (Double) -> Unit
    ) {
        val url = getDirectionURL(origin, destination)

        GlobalScope.launch(Dispatchers.IO) {
            val decodedPolyline = try {
                val client = OkHttpClient()
                val request = Request.Builder().url(url).build()
                val response = client.newCall(request).execute()
                val data = response.body!!.string()
                val respObj = Gson().fromJson(data, GoogleMapDTO::class.java)
                val path = ArrayList<LatLng>()

                for (i in 0 until respObj.routes[0].legs[0].steps.size) {
                    val decodedPoints =
                        decodePolyline(respObj.routes[0].legs[0].steps[i].polyline.points)
                    path.addAll(decodedPoints)
                }

                val distance = getDistance(path)

                withContext(Dispatchers.Main) {
                    setDistance(distance)
                }

                path
            } catch (e: Exception) {
                e.printStackTrace()
                Log.i("xxxx", "error  $e")
                emptyList<LatLng>()
            }

            withContext(Dispatchers.Main) {
                setPolylinePoints(decodedPolyline)
            }
        }
    }

    fun getDistance(path: List<LatLng>): Double {
        var distance = 0.0
        for (i in 1 until path.size) {
            distance += calculateDistance(path[i - 1], path[i])
        }
        return distance
    }


    fun decodePolyline(encoded: String): List<LatLng> {
        val poly = ArrayList<LatLng>()
        var index = 0
        val len = encoded.length
        var lat = 0
        var lng = 0

        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0
            do {
                b = encoded[index++].toInt() - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lat += dlat

            shift = 0
            result = 0
            do {
                b = encoded[index++].toInt() - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lng += dlng

            val latLng = LatLng((lat.toDouble() / 1E5), (lng.toDouble() / 1E5))
            poly.add(latLng)
        }
        return poly
    }


    suspend fun getDriverInfo(driverId: String,): DriverData {
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("Drivers").child(driverId)

        return suspendCoroutine { continuation ->
            myRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val driverMap = dataSnapshot.getValue(object : GenericTypeIndicator<Map<String, Any>>() {})
                    if (driverMap != null) {
                        val driver = DriverData(
                            firstName = driverMap["name"] as? String ?: "",
                            lastName = driverMap["surname"] as? String ?: "",
                            phoneNb = driverMap["phone"] as? String ?: "",
                            photo = driverMap["photo"] as? String ?: "",
                            carPhoto = driverMap["carPhoto"] as? String ?: "",
                            rate = driverMap["rate"] as? String ?: "00.0",
                            isVerified = driverMap["verified"] as? Boolean ?: false
                        )

                        continuation.resume(driver)
                    } else {
                        continuation.resumeWithException(RuntimeException("Driver not found"))
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    continuation.resumeWithException(databaseError.toException())
                }
            })
        }
    }



}



