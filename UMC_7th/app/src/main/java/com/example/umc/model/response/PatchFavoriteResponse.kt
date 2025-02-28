package com.example.umc.model.response

data class PatchFavoriteResponse(
    val error: PatchFavoriteError,
    val resultType: String,
    val success: PatchFavoriteSuccess
)

data class PatchFavoriteSuccess(
    val isLike: Boolean,
    val isMark: Boolean,
    val mealDate: String,
    val mealId: Int,
    val mealUserId: Int,
    val time: String,
    val userId: Int
)

data class PatchFavoriteError(
    val data: String,
    val errorCode: String,
    val reason: String
)


