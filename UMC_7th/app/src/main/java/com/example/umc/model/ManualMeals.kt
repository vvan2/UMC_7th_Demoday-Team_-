package com.example.umc.model

data class ManualMeals(
    val mealId: Int,
    val calorieTotal: Int,
    val foods: List<String>,
    val mealDate: String,
    val time: String
)