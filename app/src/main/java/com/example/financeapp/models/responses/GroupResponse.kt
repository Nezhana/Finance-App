package com.example.financeapp.models.responses

data class GroupResponse(
    val referalCode: String,
    val currency: String,
    val users: List<User>
) {
    data class User (
        val _id: String,
        val name: String,
        val email: String,
        val role: String
    )
}
