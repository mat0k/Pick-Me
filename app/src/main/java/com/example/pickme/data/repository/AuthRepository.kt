package com.example.pickme.data.repository

import android.net.Uri
import android.util.Log
import com.example.pickme.data.model.Driver
import com.example.pickme.data.model.Passenger
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await
import java.math.BigInteger
import java.security.MessageDigest
import com.google.firebase.storage.FirebaseStorage


class AuthRepository {
    private val database = FirebaseDatabase.getInstance()

    private fun hashPassword(password: String): String {
        val messageDigest = MessageDigest.getInstance("SHA-256")
        val hashedBytes = messageDigest.digest(password.toByteArray(Charsets.UTF_8))
        return BigInteger(1, hashedBytes).toString(16).padStart(32, '0')
    }

    fun addPassenger(passenger: Passenger) {
        val myRef = database.getReference("Passengers")
        val passengerObject = mapOf(
            "name" to passenger.name,
            "surname" to passenger.surname,
            "password" to hashPassword(passenger.password),
            "phone" to passenger.phone,
            "photo" to passenger.photo,
            "emergencyNumber" to passenger.emergencyNumber
        )
        myRef.push().setValue(passengerObject)
    }

    fun loginAsPassenger(phone: String, password: String) {
        val myRef = database.getReference("Passengers")
        myRef.get().addOnSuccessListener {
            for (data in it.children) {
                val passenger = data.getValue(Passenger::class.java)
                if (passenger?.phone == phone && passenger.password == hashPassword(password)) {
                    Log.d("Login", "Login successful")
                }
            }
        }
    }

    fun loginAsDriver(phone: String, password: String) {
        val myRef = database.getReference("Drivers")
        myRef.get().addOnSuccessListener {
            for (data in it.children) {
                val driver = data.getValue(Driver::class.java)
                if (driver?.phone == phone && driver.password == hashPassword(password)) {
                    //get all the data from the driver object
                }
            }
        }
    }

    private suspend fun checkDriverLicenseAndCarPlate(license: String, carPlate: String): Boolean {
        val myRef = database.getReference("verifiedlicenses")
        var exists = false

        val data = myRef.get().await()
        for (snapshot in data.children) {
            val driverLicense = snapshot.child("driverLicense").getValue(String::class.java)
            val licensePlate = snapshot.child("licensePlate").getValue(String::class.java)

            if (driverLicense == license && licensePlate == carPlate) {
                exists = true
                break
            }
        }

        return exists
    }
    suspend fun addDriver(driver: Driver) {
        val myRef = database.getReference("Drivers")
        val driverObject = mapOf(
            "name" to driver.firstName,
            "surname" to driver.lastName,
            "password" to hashPassword(driver.password),
            "phone" to driver.phone,
            "carPlate" to driver.carPlate,
            "carPhoto" to driver.carPhoto,
            "photo" to driver.driverPhoto,
            "driverLicense" to driver.driverLicense,
            "verified" to checkDriverLicenseAndCarPlate(driver.driverLicense, driver.carPlate)
        )
        myRef.push().setValue(driverObject)
    }

}