package com.example.financeapp.models.interfaces

data class User(
    val name: String,
    val email: String,
    val password: String,
    val referalCode: String? = null,
    val role: String = "user",
    val currency: String,
    val token: String? = null
)
