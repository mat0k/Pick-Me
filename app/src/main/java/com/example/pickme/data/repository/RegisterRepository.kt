package com.example.pickme.data.repository

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.pickme.data.model.Passenger
import org.json.JSONObject

class RegisterRepository(private val context: Context) {
    private val requestQueue: RequestQueue by lazy {
        Volley.newRequestQueue(context.applicationContext)
    }

    fun addPassenger(passenger: Passenger) {
        val url = "http://10.0.2.2/pickmeup/addPassenger.php"

        val stringRequest = object : StringRequest(
            Method.POST, url,
            Response.Listener<String> { response ->
                val toast = Toast.makeText(context, response, Toast.LENGTH_SHORT)
                Log.e("RESPONSE", response)
            },
            Response.ErrorListener { error ->
                val toast = Toast.makeText(context, error.toString(), Toast.LENGTH_SHORT)
                //write error message to logcat
                Log.e("ERROR", error.toString())
            }
        ) {
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["id"] = passenger.id.toString()
                params["name"] = passenger.name
                params["surname"] = passenger.surname
                params["password"] = passenger.password
                params["phone"] = passenger.phone
                params["photo"] = passenger.photo
                params["emergencyNumber"] = passenger.emergencyNumber
                return params
            }
        }

        requestQueue.add(stringRequest)
    }
}