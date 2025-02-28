package com.example.umc.model.response

data class PostMealRefreshResponse(
    val error: Any,
    val resultType: String,
    val success: MealRefreshSuccess
)

data class MealRefreshSuccess(
    val mealId: Int,
    val food: String,
    val calorieTotal: Int,
    val material: String,
    val calorieDetail: String,
    val price: Int,
    val difficulty: Int,
    val recipe: String,
    val addedByUser: Boolean
)

data class MealRefreshError(
    val errorCode: String,
    val reason: String,
    val data: String
)

data class MealRefreshResponse(
    val resultType: String,
    val error: MealRefreshError?,
    val success: MealRefreshSuccess?
)