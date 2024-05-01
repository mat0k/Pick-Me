package com.example.pickme.data.repository

import android.net.Uri
import android.util.Log
import com.example.pickme.data.model.Driver
import com.example.pickme.data.model.Passenger
import com.example.pickme.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.math.BigInteger
import java.security.MessageDigest
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine


class AuthRepository() {
    private val database = FirebaseDatabase.getInstance()

    private fun hashPassword(password: String): String {
        val messageDigest = MessageDigest.getInstance("SHA-256")
        val hashedBytes = messageDigest.digest(password.toByteArray(Charsets.UTF_8))
        return BigInteger(1, hashedBytes).toString(16).padStart(32, '0')
    }

    suspend fun addPassenger(passenger: Passenger): Result<Unit> {
        val myRef = database.getReference("Passengers")
        val data = myRef.get().await()
        for (dataSnapshot in data.children) {
            val existingPassenger = dataSnapshot.getValue(Passenger::class.java)
            if (existingPassenger?.phone == passenger.phone) {
                return Result.failure(Exception("A passenger account with this phone number already exists."))
            }
        }

        val passengerObject = mapOf(
            "id" to myRef.push().key,
            "name" to passenger.name,
            "surname" to passenger.surname,
            "password" to hashPassword(passenger.password),
            "phone" to passenger.phone,
            "photoUrl" to passenger.photoUrl,
            "emergencyNumber" to passenger.emergencyNumber,
        )
        myRef.push().setValue(passengerObject)
        return Result.success(Unit)
    }

    suspend fun loginAsPassenger(phone: String, password: String): Result<User> {
        Log.d("AuthRepository", "loginAsPassenger: $phone $password")
        val myRef = database.getReference("Passengers")
        val data = myRef.get().await()
        for (dataSnapshot in data.children) {
            val passenger = dataSnapshot.getValue(Passenger::class.java)
            if (passenger?.phone == phone && passenger.password == hashPassword(password)) {
                val user = User(
                    id = dataSnapshot.key ?: "",
                    role = 0, // Assuming role 0 is for passengers
                    photoUrl = passenger.photoUrl,
                    firstName = passenger.name,
                    lastName = passenger.surname
                )
                val tokenResult = FirebaseAuth.getInstance().currentUser?.getIdToken(false)?.await()
                user.token = tokenResult?.token
                return Result.success(user)
            }
        }
        return Result.failure(Exception("Invalid phone number or password"))
    }

    suspend fun loginAsDriver(phone: String, password: String): Result<User> {
        Log.d("AuthRepository", "loginAsDriver: $phone $password")
        val myRef = database.getReference("Drivers")
        val data = myRef.get().await()
        for (dataSnapshot in data.children) {
            val driver = dataSnapshot.getValue(Driver::class.java)
            if (driver?.phone == phone && driver.password == hashPassword(password)) {
                val user = User(
                    id = dataSnapshot.key ?: "",
                    role = 1, // Assuming role 1 is for drivers
                    photoUrl = driver.driverPhotoUrl,
                    firstName = driver.firstName,
                    lastName = driver.lastName
                )
                val tokenResult = FirebaseAuth.getInstance().currentUser?.getIdToken(false)?.await()
                user.token = tokenResult?.token
                return Result.success(user)
            }
        }
        return Result.failure(Exception("Invalid phone number or password"))
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

    suspend fun addDriver(driver: Driver): Result<Unit> {
        val myRef = database.getReference("Drivers")
        val data = myRef.get().await()
        for (dataSnapshot in data.children) {
            val existingDriver = dataSnapshot.getValue(Driver::class.java)
            if (existingDriver?.phone == driver.phone) {
                return Result.failure(Exception("A driver account with this phone number already exists."))
            }
        }
        val driverObject = mapOf(
            "id" to myRef.push().key,
            "name" to driver.firstName,
            "surname" to driver.lastName,
            "password" to hashPassword(driver.password),
            "phone" to driver.phone,
            "carPlate" to driver.carPlate,
            "carPhoto" to driver.carPhotoUrl,
            "photo" to driver.driverPhotoUrl,
            "driverLicense" to driver.driverLicense,
            "verified" to checkDriverLicenseAndCarPlate(driver.driverLicense, driver.carPlate),
        )
        myRef.push().setValue(driverObject)
        return Result.success(Unit)
    }

    suspend fun updatePassenger(passenger: Passenger): Result<Unit> {
        val myRef = database.getReference("Passengers").child(passenger.id)
        val passengerObject = mapOf(
            "id" to passenger.id,
            "name" to passenger.name,
            "surname" to passenger.surname,
            "password" to passenger.password,
            "phone" to passenger.phone,
            "photoUrl" to passenger.photoUrl,
            "emergencyNumber" to passenger.emergencyNumber
        )
        myRef.updateChildren(passengerObject).await()
        return Result.success(Unit)
    }

    suspend fun uploadImageToFirebase(imageUri: Uri): String {
        if (imageUri == Uri.EMPTY) return ""
        return suspendCoroutine { continuation ->
            val storageRef =
                FirebaseStorage.getInstance().getReference("images/${imageUri.lastPathSegment}")
            storageRef.putFile(imageUri)
                .addOnSuccessListener { task ->
                    task.metadata!!.reference!!.downloadUrl
                        .addOnSuccessListener {
                            continuation.resume(it.toString())
                        }
                        .addOnFailureListener {
                            continuation.resumeWithException(it)
                        }
                }
                .addOnFailureListener {
                    continuation.resumeWithException(it)
                }
        }
    }

    suspend fun getPassengerData(id: String): Passenger? {
        val myRef = database.getReference("Passengers").child(id)
        val data = myRef.get().await()
        return data.getValue(Passenger::class.java)
    }
}