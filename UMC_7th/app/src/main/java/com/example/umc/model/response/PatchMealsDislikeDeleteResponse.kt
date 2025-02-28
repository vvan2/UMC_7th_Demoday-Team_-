package com.example.umc.model.response

data class PatchMealsDislikeDeleteResponse(
    val error: PatchMealsDislikeDeleteError,
    val resultType: String,
    val success: PatchMealsDislikeDeleteSuccess
)

data class PatchMealsDislikeDeleteSuccess(
    val count: Int
)

data class PatchMealsDislikeDeleteError(
    val data: String,
    val errorCode: String,
    val reason: String
)