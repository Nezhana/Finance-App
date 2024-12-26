package com.example.financeapp.models.requests

data class RegisterRequest (
    val name: String,
    val email: String,
    val password: String,
    val currency: String,
    val referalCode: String?
)