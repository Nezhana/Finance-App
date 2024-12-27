package com.example.financeapp.models.responses

sealed class StatisticsResponse {
    abstract val cashPercentage: Double
    abstract val cardPercentage: Double
    abstract val currency: String
    abstract val categories: List<CategoryStatistics>
}

data class MonthStatisticsResponse(
    override val currency: String,
    val resolvedMonth: Int,
    val resolvedYear: Int,
    val total: Double,
    override val categories: List<CategoryStatistics>,
    override val cashPercentage: Double,
    override val cardPercentage: Double
)  : StatisticsResponse()

data class CategoryStatistics(
    val title: String,
    val total: Double,
    val categoryId: String,
    val percentage: Double
)
