package com.example.financeapp.models.responses

data class MonthStatisticsResponse(
    val currency: String,
    val resolvedMonth: Int,
    val resolvedYear: Int,
    val total: Double,
    val categories: List<CategoryStatistics>,
    val cashPercentage: Double,
    val cardPercentage: Double
)

data class CategoryStatistics(
    val title: String,
    val total: Double,
    val categoryId: String,
    val percentage: Double
)
