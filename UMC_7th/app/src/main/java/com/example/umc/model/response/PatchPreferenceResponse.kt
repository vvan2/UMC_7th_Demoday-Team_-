package com.example.umc.model.response

data class PatchPreferenceResponse (
    val error: PatchPreferenceError,
    val resultType: String,
    val success: PatchPreferenceSuccess
)

data class PatchPreferenceSuccess(
    val isLike: Boolean,
    val isMark: Boolean,
    val mealDate: String,
    val mealId: Int,
    val mealUserId: Int,
    val time: String,
    val userId: Int
)

data class PatchPreferenceError(
    val data: String,
    val errorCode: String,
    val reason: String
)
