package com.example.financeapp.models.requests

data class UpdateUserRequest(
    val name: String,
    val currency: String,
    val role: String
)
