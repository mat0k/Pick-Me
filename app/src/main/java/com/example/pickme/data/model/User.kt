package com.example.pickme.data.model

data class User(
    val id: String = "",
    val role: Int = 0,
    val photoUrl: String = "",
    val firstName: String = "",
    val lastName: String = "",
    var token: String? = null
)