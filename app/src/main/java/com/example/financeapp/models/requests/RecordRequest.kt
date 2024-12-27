package com.example.financeapp.models.requests

data class RecordRequest(
    val title: String,
    val type: String,
    val value: Double,
    val method: String,
    val date: String,
    val categoryId: String,
    val reccurent: Boolean = false,
    val repeating: String? = null
)
