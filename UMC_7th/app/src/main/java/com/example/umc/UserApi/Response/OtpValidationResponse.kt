package com.example.umc.UserApi.Response

data class OtpValidationResponse(
    val resultType: String,
    val error: String?,
    val success: SuccessValiResponse?
)
