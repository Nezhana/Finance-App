package com.example.financeapp.models.responses

data class CurrentBalanceCategoryResponse(
    val category: CategoryDetails
)

data class CategoryDetails(
    val title: String,
    val total: Double,
    val currency: String,
    val records: List<Record>
)

data class Record(
    val _id: String,
    val type: String,
    val title: String,
    val value: Double,
    val date: String
)

