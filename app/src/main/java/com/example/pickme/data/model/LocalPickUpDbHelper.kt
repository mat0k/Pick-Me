package com.example.pickme.data.model

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.google.android.gms.maps.model.LatLng

class LocalPickUpDbHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_VERSION = 1
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
        private const val COLUMN_DRIVER_ID = "driver_id"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTableQuery = "CREATE TABLE $TABLE_NAME (" +
                "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$COLUMN_PICKUP_TITLE TEXT, " +
                "$COLUMN_TARGET_TITLE TEXT, " +
                "$COLUMN_PICKUP_LAT REAL, " +
                "$COLUMN_PICKUP_LNG REAL, " +
                "$COLUMN_TARGET_LAT REAL, " +
                "$COLUMN_TARGET_LNG REAL, " +
                "$COLUMN_DISTANCE REAL, " +
                "$COLUMN_DATE_AND_TIME TEXT), " +
                "$COLUMN_DRIVER_ID"
        db?.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun insertLocalPickUp(localPickUp: LocalPickUp): Long {
        val db = this.writableDatabase
        val contentValues = ContentValues().apply {
            put(COLUMN_PICKUP_TITLE, localPickUp.pickUpTitle)
            put(COLUMN_TARGET_TITLE, localPickUp.targetTitle)
            put(COLUMN_PICKUP_LAT, localPickUp.pickUpLatLng.latitude)
            put(COLUMN_PICKUP_LNG, localPickUp.pickUpLatLng.longitude)
            put(COLUMN_TARGET_LAT, localPickUp.targetLatLng.latitude)
            put(COLUMN_TARGET_LNG, localPickUp.targetLatLng.longitude)
            put(COLUMN_DISTANCE, localPickUp.distance)
            put(COLUMN_DATE_AND_TIME, localPickUp.dateAndTime)
            put(COLUMN_DRIVER_ID, localPickUp.driverId)
        }
        return db.insert(TABLE_NAME, null, contentValues)
    }

    fun getAllLocalPickUps(): List<LocalPickUp> {
        val localPickUpList = mutableListOf<LocalPickUp>()
        val selectQuery = "SELECT * FROM $TABLE_NAME"
        val db = this.readableDatabase
        val cursor = db.rawQuery(selectQuery, null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
                val pickUpTitle = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PICKUP_TITLE))
                val targetTitle = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TARGET_TITLE))
                val pickUpLat = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_PICKUP_LAT))
                val pickUpLng = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_PICKUP_LNG))
                val targetLat = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_TARGET_LAT))
                val targetLng = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_TARGET_LNG))
                val distance = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_DISTANCE))
                val dateAndTime = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE_AND_TIME))
                val driverId = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DRIVER_ID))
                localPickUpList.add(
                    LocalPickUp(
                        id,
                        pickUpTitle,
                        targetTitle,
                        LatLng(pickUpLat, pickUpLng),
                        LatLng(targetLat, targetLng),
                        distance,
                        dateAndTime,
                        driverId
                    )
                )
            } while (cursor.moveToNext())
        }

        cursor.close()
        return localPickUpList
    }

    fun deleteLocalPickUp(id: Int): Int {
        val db = this.writableDatabase
        return db.delete(TABLE_NAME, "$COLUMN_ID=?", arrayOf(id.toString()))
    }
}