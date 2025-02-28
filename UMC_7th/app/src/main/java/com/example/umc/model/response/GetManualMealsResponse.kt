package com.example.umc.model.response

data class GetManualMealsResponse(
    val resultType: String,
    val error: GetManualMealsResponseError?,
    val success: List<GetManualMealsResponseSuccess>?
)

data class GetManualMealsResponseError(
    val errorCode: String,
    val reason: String,
    val data: String
)

data class GetManualMealsResponseSuccess(
    val MealUser: List<MealUser>,
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

data class MealUser(
    val mealDate: String,
    val time: String
)