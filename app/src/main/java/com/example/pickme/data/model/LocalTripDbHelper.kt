package com.example.pickme.data.model

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.google.android.gms.maps.model.LatLng
import java.util.UUID

class LocalTripDbHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_VERSION = 5
        private const val DATABASE_NAME = "local_trips.db"
        private const val TABLE_NAME = "local_trips"
        private const val COLUMN_ID = "id"
        private const val COLUMN_DRIVER_ID = "driver_id"
        private const val COLUMN_TITLE = "title"
        private const val COLUMN_SEATS = "seats"
        private const val COLUMN_STARTING = "starting"
        private const val COLUMN_END = "end"
        private const val COLUMN_STARTING_LAT = "starting_lat"
        private const val COLUMN_STARTING_LNG = "starting_lng"
        private const val COLUMN_DESTINATION_LAT = "destination_lat"
        private const val COLUMN_DESTINATION_LNG = "destination_lng"
        private const val COLUMN_DATE = "date"
        private const val COLUMN_TIME = "time"
        private const val COLUMN_TRIP_DISTANCE = "trip_distance"
        private const val COLUMN_VERIFIED = "verified"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTableQuery = "CREATE TABLE $TABLE_NAME (" +
                "$COLUMN_ID TEXT PRIMARY KEY, " +
                "$COLUMN_DRIVER_ID TEXT, " +
                "$COLUMN_TITLE TEXT, " +
                "$COLUMN_SEATS INTEGER, " +
                "$COLUMN_STARTING TEXT, " +
                "$COLUMN_END TEXT, " +
                "$COLUMN_STARTING_LAT REAL, " +
                "$COLUMN_STARTING_LNG REAL, " +
                "$COLUMN_DESTINATION_LAT REAL, " +
                "$COLUMN_DESTINATION_LNG REAL, " +
                "$COLUMN_DATE TEXT, " +
                "$COLUMN_TIME TEXT, " +
                "$COLUMN_TRIP_DISTANCE REAL, " +
                "$COLUMN_VERIFIED INTEGER)"
        db?.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun insertTrip(localTrip: LocalTrip): Long {
        val db = this.writableDatabase
        val contentValues = ContentValues().apply {
            // Generate a unique ID for each trip
            put(COLUMN_ID, UUID.randomUUID().toString())
            put(COLUMN_DRIVER_ID, localTrip.driverId)
            put(COLUMN_TITLE, localTrip.title)
            put(COLUMN_SEATS, localTrip.seats)
            put(COLUMN_STARTING, localTrip.starting)
            put(COLUMN_END, localTrip.end)
            put(COLUMN_STARTING_LAT, localTrip.startingLatLng.latitude)
            put(COLUMN_STARTING_LNG, localTrip.startingLatLng.longitude)
            put(COLUMN_DESTINATION_LAT, localTrip.destinationLatLng.latitude)
            put(COLUMN_DESTINATION_LNG, localTrip.destinationLatLng.longitude)
            put(COLUMN_DATE, localTrip.date)
            put(COLUMN_TIME, localTrip.time)
            put(COLUMN_TRIP_DISTANCE, localTrip.tripDistance)
            put(COLUMN_VERIFIED, if (localTrip.verified) 1 else 0)
        }
        return db.insert(TABLE_NAME, null, contentValues)
    }

    fun getAllTrips(): List<LocalTrip> {
        val localTripList = mutableListOf<LocalTrip>()
        val selectQuery = "SELECT * FROM $TABLE_NAME"
        val db = this.readableDatabase
        val cursor = db.rawQuery(selectQuery, null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ID))
                val driverId = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DRIVER_ID))
                val title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE))
                val seats = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SEATS))
                val starting = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STARTING))
                val end = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_END))
                val startingLat = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_STARTING_LAT))
                val startingLng = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_STARTING_LNG))
                val destinationLat = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_DESTINATION_LAT))
                val destinationLng = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_DESTINATION_LNG))
                val date = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE))
                val time = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TIME))
                val tripDistance = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_TRIP_DISTANCE))
                val verified = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_VERIFIED)) == 1

                localTripList.add(
                    LocalTrip(
                        id,
                        driverId,
                        title,
                        seats,
                        starting,
                        end,
                        LatLng(startingLat, startingLng),
                        LatLng(destinationLat, destinationLng),
                        date,
                        time,
                        tripDistance,
                        verified
                    )
                )
                Log.i("TripsDB", "Trip: $id, $driverId, $title, $seats, $starting, $end, $startingLat, $startingLng, $destinationLat, $destinationLng, $date, $time, $tripDistance, $verified")

            } while (cursor.moveToNext())
        }

        cursor.close()
        return localTripList
    }

    fun deleteTrip(id: String): Int {
        val db = this.writableDatabase
        return db.delete(TABLE_NAME, "$COLUMN_ID=?", arrayOf(id))
    }
}