package com.example.financeapp.models.responses

data class CategoriesResponse(
    val categories: List<CategoryItem>
){
    data class CategoryItem(
        val id: String,
        val title: String,
        val balanceId: String
    )
}
