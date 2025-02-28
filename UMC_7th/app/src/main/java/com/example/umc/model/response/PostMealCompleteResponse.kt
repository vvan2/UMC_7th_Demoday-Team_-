package com.example.umc.model.response

data class PostCompleteMealResponse(
    val resultType: String,
    val error: PostCompleteMealErrorResponse? = null
)

data class PostCompleteMealErrorResponse(
    val errorCode: String?,
    val reason: String?,
    val data: String?
)