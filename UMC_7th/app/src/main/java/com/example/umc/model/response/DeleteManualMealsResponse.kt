package com.example.umc.model.response

class DeleteManualMealsResponse (
    val error: DeleteManualMealsError,
    val resultType: String,
    val success: DeleteManualMealsSuccess
)

data class DeleteManualMealsSuccess(
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

data class DeleteManualMealsError(
    val `data`: String,
    val errorCode: String,
    val reason: String
)