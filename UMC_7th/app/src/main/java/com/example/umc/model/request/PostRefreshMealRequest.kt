package com.example.umc.model.request

data class PostRefreshMealRequest(
    val mealDate: String,
    val mealId: Int,
    val time: String,
    val userId: Int
)
data class MealRefreshRequest(
    val mealId: Int
)