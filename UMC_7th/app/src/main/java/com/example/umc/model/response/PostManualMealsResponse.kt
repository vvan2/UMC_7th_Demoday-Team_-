package com.example.umc.model.response

data class PostManualMealsResponse(
    val resultType: String,
    val error: PostManualMealsError?,
    val success: PostManualMealsSuccess?
)

data class PostManualMealsSuccess(
    val mealId: Int,
    val food: String,
    val calorieTotal: Int,
    val material: String?,
    val calorieDetail: String?,
    val price: Int,
    val difficulty: String?,
    val recipe: String?,
    val addedByUser: Boolean
)

data class PostManualMealsError(
    val errorCode: String,
    val reason: String,
    val data: String
)

