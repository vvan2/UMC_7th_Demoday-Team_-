package com.example.umc.model.response

data class GetMaterialImageResponse(
    val error: GetMaterialImageError,
    val resultType: String,
    val success: GetMaterialImageSuccess
)

data class GetMaterialImageSuccess(
    val imageUrl: String
)

data class GetMaterialImageError(
    val data: String,
    val errorCode: String,
    val reason: String
)