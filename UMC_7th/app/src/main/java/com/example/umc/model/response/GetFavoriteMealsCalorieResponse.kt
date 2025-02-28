package com.example.umc.model.response

data class GetFavoriteMealsCalorieResponse(
    val error: GetFavoriteMealsCalorieError,
    val resultType: String,
    val success: List<GetFavoriteMealsCalorieSuccess>
)

data class GetFavoriteMealsCalorieSuccess(
    val isHate: Boolean,
    val isLike: Boolean,
    val isMark: Boolean,
    val meal : GetFavoriteMeals,
    val mealDate: String,
    val mealId: Int,
    val mealUserId: Int,
    val time: String,
    val userId: Int
)

data class GetFavoriteMealsCalorieError(
    val `data`: String,
    val errorCode: String,
    val reason: String
)