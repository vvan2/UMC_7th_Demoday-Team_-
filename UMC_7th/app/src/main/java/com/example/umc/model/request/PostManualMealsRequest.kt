package com.example.umc.model.request

data class PostManualMealsRequest(
    val addedByUser: Boolean,
    val calorieTotal: Int,
    val foods: List<String>,
    val mealDate: String,
    val time: String
)