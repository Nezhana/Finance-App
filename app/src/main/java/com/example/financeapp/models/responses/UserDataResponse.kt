package com.example.financeapp.models.responses

data class UserDataResponse(
    val user: UpdatedUser
){
    data class UpdatedUser(
        val name: String,
        val email: String,
        val currency: String,
        val referalCode: String,
        val role: String
    )
}

