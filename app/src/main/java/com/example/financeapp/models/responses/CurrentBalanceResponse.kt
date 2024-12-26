package com.example.financeapp.models.responses

data class CurrentBalanceResponse(
    val currency: String,
    val currentMonth: Number,
    val incomeTotal: Number,
    val expenseTotal: Number,
    val total: Number
)
