package com.example.pickme.data.model

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class UserDatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "UserDatabase"
        const val TABLE_USERS = "users"
        const val COLUMN_ID = "id"
        const val COLUMN_ROLE = "role"
        const val COLUMN_PHOTO_URL = "photoUrl"
        const val COLUMN_FIRST_NAME = "firstName"
        const val COLUMN_LAST_NAME = "lastName"
        const val TOKEN = "token"
    }

    override fun onCreate(db: SQLiteDatabase) {
    val CREATE_USERS_TABLE = ("CREATE TABLE " +
            TABLE_USERS + "("
            + COLUMN_ID + " TEXT PRIMARY KEY," +
            COLUMN_ROLE + " INTEGER," +
            COLUMN_PHOTO_URL + " TEXT," +
            COLUMN_FIRST_NAME + " TEXT," +
            COLUMN_LAST_NAME + " TEXT," +
            TOKEN + " TEXT" + ")")
    db.execSQL(CREATE_USERS_TABLE)
}

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        onCreate(db)
    }

    fun addUser(user: User) {
        val values = ContentValues()
        values.put(COLUMN_ID, user.id)
        values.put(COLUMN_ROLE, user.role)
        values.put(COLUMN_PHOTO_URL, user.photoUrl)
        values.put(COLUMN_FIRST_NAME, user.firstName)
        values.put(COLUMN_LAST_NAME, user.lastName)
        values.put(TOKEN, user.token)
        val db = this.writableDatabase
        db.insert(TABLE_USERS, null, values)
        db.close()
    }

    // Add methods to retrieve user data from the database
    // In UserDatabaseHelper.kt
    @SuppressLint("Range")
    fun getUserRole(id: String): Int {
        val db = this.readableDatabase
        val cursor = db.query(
            TABLE_USERS,
            arrayOf(COLUMN_ROLE),
            "$COLUMN_ID=?",
            arrayOf(id),
            null,
            null,
            null
        )
        if (cursor != null && cursor.moveToFirst()) {
            val role = cursor.getInt(cursor.getColumnIndex(COLUMN_ROLE))
            cursor.close()
            return role
        }
        cursor.close()
        return -1 // return -1 if no user found
    }

    fun deleteUser(id: String) {
        val db = this.writableDatabase
        val whereClause = "id=?"
        val whereArgs = arrayOf(id)
        db.delete("YourTableName", whereClause, whereArgs)
        db.close()
    }

@SuppressLint("Range")
fun getAllUsers(): List<User> {
    val users = mutableListOf<User>()
    val db = this.readableDatabase
    val cursor = db.rawQuery("SELECT * FROM $TABLE_USERS", null)
    if (cursor.moveToFirst()) {
        do {
            val id = cursor.getString(cursor.getColumnIndex(COLUMN_ID))
            val role = cursor.getInt(cursor.getColumnIndex(COLUMN_ROLE))
            val photoUrl = cursor.getString(cursor.getColumnIndex(COLUMN_PHOTO_URL))
            val firstName = cursor.getString(cursor.getColumnIndex(COLUMN_FIRST_NAME))
            val lastName = cursor.getString(cursor.getColumnIndex(COLUMN_LAST_NAME))
            val token = cursor.getString(cursor.getColumnIndex(TOKEN))
            users.add(User(id, role, photoUrl, firstName, lastName, token))
        } while (cursor.moveToNext())
    }
    cursor.close()
    return users
}

    fun getUser(id: String): User? {
        val query = "SELECT * FROM $TABLE_USERS WHERE $COLUMN_ID = \"$id\""

        val db = this.readableDatabase
        val cursor = db.rawQuery(query, null)

        var user: User? = null
        if (cursor.moveToFirst()) {
            user = User(
                id = cursor.getString(0),
                role = cursor.getInt(1),
                photoUrl = cursor.getString(2),
                firstName = cursor.getString(3),
                lastName = cursor.getString(4),
                token = cursor.getString(5)
            )
            cursor.close()
        }
        db.close()
        return user
    }
}