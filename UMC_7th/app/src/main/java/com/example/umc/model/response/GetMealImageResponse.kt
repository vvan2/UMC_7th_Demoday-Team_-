package com.example.umc.model.response

data class GetMealImageResponse(
    val error: GetMealImageError,
    val resultType: String,
    val success: GetMealImageSuccess
)

data class GetMealImageSuccess(
    val imageUrl: String
)

data class GetMealImageError(
    val data: String,
    val errorCode: String,
    val reason: String
)