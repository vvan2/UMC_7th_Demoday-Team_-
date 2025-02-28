package com.example.umc.model.response

data class GetMealsDetailResponse(
    val error: GetMealsDetailError,
    val resultType: String,
    val success: GetMealsDetailSuccess
)

data class GetMealsDetailMealDetail(
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

data class GetMealsDetailSuccess(
    val mealDetail: GetMealsDetailMealDetail,
    val mealUser:  GetMealsDetailMealUser
)

data class GetMealsDetailError(
    val `data`: String,
    val errorCode: String,
    val reason: String
)

data class GetMealsDetailMealUser(
    val isHate: Boolean,
    val isLike: Boolean,
    val isMark: Boolean,
    val mealDate: String,
    val mealId: Int,
    val mealUserId: Int,
    val time: String,
    val userId: Int
)