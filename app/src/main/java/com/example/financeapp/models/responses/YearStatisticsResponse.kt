package com.example.financeapp.models.responses

 data class YearStatisticsResponse(
    override val currency: String,
    val resolvedYear: Int,
    val total: Double,
    override val categories: List<CategoryStatistics>,
    override val cashPercentage: Double,
    override val cardPercentage: Double
) : StatisticsResponse()
