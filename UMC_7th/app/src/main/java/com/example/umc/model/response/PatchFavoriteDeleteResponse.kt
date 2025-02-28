package com.example.umc.model.response

data class PatchFavoriteDeleteResponse(
    val error: PatchFavoriteDeleteError,
    val resultType: String,
    val success: PatchFavoriteDeleteSuccess
)

data class PatchFavoriteDeleteSuccess(
    val count: Int
)

data class PatchFavoriteDeleteError(
    val `data`: String,
    val errorCode: String,
    val reason: String
)