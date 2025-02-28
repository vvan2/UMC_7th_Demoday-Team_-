package com.example.umc.model.response

data class GetFavoriteMealsResponse(
    val error: GetFavoriteMealsError,
    val resultType: String,
    val success: List<GetFavoriteMealsSuccess>
)

data class GetFavoriteMealsSuccess(
    val isHate: Boolean,
    val isLike: Boolean,
    val isMark: Boolean,
    val mealDate: String,
    val mealId: Int,
    val mealUserId: Int,
    val time: String,
    val userId: Int,
    val meal : GetFavoriteMeals
)

data class GetFavoriteMeals(
    val addedByUser: Boolean,
    val calorieDetail: String,
    val calorieTotal: Int,
    val difficulty: Int,
    val food: String,
    val material: String,
    val mealId: Int,
    val price: Int,
    val recipe: String
)

data class GetFavoriteMealsError(
    val `data`: String,
    val errorCode: String,
    val reason: String
)