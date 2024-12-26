package com.example.financeapp.models.responses

data class CurrentBalanceCategoriesResponse(
    val currency: String,
    val currentMonth: Number,
    val categories: List<Category>
) {
    data class Category(
        val title: String,
        val total: Number,
        val categoryId: String
    )
}