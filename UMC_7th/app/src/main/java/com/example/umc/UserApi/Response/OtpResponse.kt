package com.example.umc.UserApi.Response

data class OtpResponse(
    val resultType: String,
    val error: String?,
    val success: SuccessOtpResponse
)
