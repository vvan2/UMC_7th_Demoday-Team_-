package com.example.umc.model.response

data class PatchMealsDislikeResponse(
    val error: PatchMealsDislikeError,
    val resultType: String,
    val success: PatchMealsDislikeSuccess
)

data class PatchMealsDislikeSuccess(
    val count: Int
)

data class PatchMealsDislikeError(
    val data: String,
    val errorCode: String,
    val reason: String
)