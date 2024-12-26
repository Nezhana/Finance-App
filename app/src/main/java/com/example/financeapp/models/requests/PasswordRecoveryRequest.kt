package com.example.financeapp.models.requests

data class PasswordRecoveryRequest(
    val name: String,
    val email: String,
    val currency: String
)
