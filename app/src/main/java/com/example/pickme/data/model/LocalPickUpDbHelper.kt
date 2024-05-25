package com.example.pickme.data.model

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.google.android.gms.maps.model.LatLng

class LocalPickUpDbHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_VERSION = 6
        private const val DATABASE_NAME = "local_pickups.db"
        private const val TABLE_NAME = "local_pickups"
        private const val COLUMN_ID = "id"
        private const val COLUMN_PICKUP_TITLE = "pickup_title"
        private const val COLUMN_TARGET_TITLE = "target_title"
        private const val COLUMN_PICKUP_LAT = "pickup_lat"
        private const val COLUMN_PICKUP_LNG = "pickup_lng"
        private const val COLUMN_TARGET_LAT = "target_lat"
        private const val COLUMN_TARGET_LNG = "target_lng"
        private const val COLUMN_DISTANCE = "distance"
        private const val COLUMN_DATE_AND_TIME = "date_and_time"
        private const val COLUMN_PASSENGER_ID = "passenger_id"
        private const val COLUMN_DRIVER_ID = "driver_id"
        private const val COLUMN_PRICE = "price"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTableQuery = "CREATE TABLE $TABLE_NAME (" +
                "$COLUMN_ID TEXT, " +
                "$COLUMN_PICKUP_TITLE TEXT, " +
                "$COLUMN_TARGET_TITLE TEXT, " +
                "$COLUMN_PICKUP_LAT REAL, " +
                "$COLUMN_PICKUP_LNG REAL, " +
                "$COLUMN_TARGET_LAT REAL, " +
                "$COLUMN_TARGET_LNG REAL, " +
                "$COLUMN_DISTANCE REAL, " +
                "$COLUMN_DATE_AND_TIME TEXT, " +
                "$COLUMN_PASSENGER_ID TEXT, " +
                "$COLUMN_DRIVER_ID TEXT, " +
                "$COLUMN_PRICE REAL)"
        db?.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun insertLocalPickUp(pickUp: PickUp): Long {
        Log.d("insertLocalPickUp", "Inserting pickup with ID: ${pickUp.id}")
        val db = this.writableDatabase
        val contentValues = ContentValues().apply {
            put(COLUMN_ID, pickUp.id)
            put(COLUMN_PICKUP_TITLE, pickUp.pickUpTitle)
            put(COLUMN_TARGET_TITLE, pickUp.targetTitle)
            put(COLUMN_PICKUP_LAT, pickUp.pickUpLatLng.latitude)
            put(COLUMN_PICKUP_LNG, pickUp.pickUpLatLng.longitude)
            put(COLUMN_TARGET_LAT, pickUp.targetLatLng.latitude)
            put(COLUMN_TARGET_LNG, pickUp.targetLatLng.longitude)
            put(COLUMN_DISTANCE, pickUp.distance)
            put(COLUMN_DATE_AND_TIME, pickUp.dateAndTime)
            put(COLUMN_PASSENGER_ID, pickUp.passengerId)
            put(COLUMN_DRIVER_ID, pickUp.driverId)
            put(COLUMN_PRICE, pickUp.price)
        }
        return db.insert(TABLE_NAME, null, contentValues)
    }

    fun getAllLocalPickUps(passengerId: String): List<PickUp> {
        val localPickUpList = mutableListOf<PickUp>()
        val selectQuery = "SELECT * FROM $TABLE_NAME WHERE $COLUMN_PASSENGER_ID = ?"
        val db = this.readableDatabase
        val cursor = db.rawQuery(selectQuery, arrayOf(passengerId))

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ID))
                val pickUpTitle =
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PICKUP_TITLE))
                val targetTitle =
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TARGET_TITLE))
                val pickUpLat = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_PICKUP_LAT))
                val pickUpLng = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_PICKUP_LNG))
                val targetLat = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_TARGET_LAT))
                val targetLng = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_TARGET_LNG))
                val distance = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_DISTANCE))
                val dateAndTime =
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE_AND_TIME))
                val driverId = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DRIVER_ID))
                Log.d("getAllLocalPickUps", "Driver ID: $driverId") // Print the driver ID
                val price =
                    cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_PRICE)) // Retrieve price
                localPickUpList.add(
                    PickUp(
                        id,
                        pickUpTitle,
                        targetTitle,
                        LatLng(pickUpLat, pickUpLng),
                        LatLng(targetLat, targetLng),
                        distance,
                        dateAndTime,
                        passengerId,
                        driverId,
                        price
                    )
                )
            } while (cursor.moveToNext())
        }

        cursor.close()

        return localPickUpList
    }

    fun deleteLocalPickUp(id: String): Int {
        val db = this.writableDatabase
        return db.delete(TABLE_NAME, "$COLUMN_ID=?", arrayOf(id))
    }

    fun updateDriverId(id: String, newDriverId: String): Int {
        Log.d("updateDriverId", "Updating driver ID for pickup with ID: $id to $newDriverId")
        if (newDriverId.isEmpty()) {
            Log.w("updateDriverId", "New driver ID is null or empty")
        }

        val db = this.writableDatabase
        val contentValues = ContentValues().apply {
            put(COLUMN_DRIVER_ID, newDriverId)
        }
        val rowsUpdated = db.update(TABLE_NAME, contentValues, "$COLUMN_ID=?", arrayOf(id))

        if (rowsUpdated == 0) {
            Log.w(
                "updateDriverId",
                "No rows updated, check if pickup with ID: $id exists in the database"
            )
        }

        return rowsUpdated
    }
}