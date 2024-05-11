package com.example.pickme.data.repository

import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

class OneSignalNotificationSender {
    private val oneSignalApiUrl = "https://api.onesignal.com/notifications"
    private val apiKey = "ZDgxNzc5MzctZmM1Mi00YTExLWI4MDItNjNhYjMzMTJmMDAy"

    fun sendNotification(message: String, recipientIDs: List<String>) {
        val client = OkHttpClient()
        val json = JSONObject()
        json.put("app_id", "2e6ceeec-973c-494d-a6c2-619d833f261b")
        json.put("include_subscription_ids", recipientIDs)
        json.put("contents", mapOf("en" to message))

        val body = RequestBody.create("application/json; charset=utf-8".toMediaTypeOrNull(), json.toString())
        val request = Request.Builder()
            .url(oneSignalApiUrl)
            .post(body)
            .addHeader("Authorization", "Basic $apiKey")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // Handle failure
                println("Failed to send notification: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                // Handle success
                if (!response.isSuccessful) {
                    println("Failed to send notification: ${response.message}")
                } else {
                    println("Notification sent successfully")
                }

            }
        })
    }
}