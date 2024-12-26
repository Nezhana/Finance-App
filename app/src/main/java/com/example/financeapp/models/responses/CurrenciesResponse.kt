package com.example.financeapp.models.responses

data class CurrenciesResponse(
    val success: Boolean,
    val timestamp: Number,
    val base: String,
    val date: String,
    val rates: Rates
)

data class Rates(
    val USD: Number,
    val EUR: Number,
    val UAH: Number
)

