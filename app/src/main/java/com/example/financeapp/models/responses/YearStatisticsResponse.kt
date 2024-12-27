package com.example.financeapp.models.responses

data class YearStatisticsResponse(
    val currency: String,
    val resolvedYear: Int,
    val total: Double,
    val categories: List<CategoryStatistics>,
    val cashPercentage: Double,
    val cardPercentage: Double
)
