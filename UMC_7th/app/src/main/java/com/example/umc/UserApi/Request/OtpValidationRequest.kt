package com.example.umc.UserApi.Request

data class OtpValidationRequest(
    val phoneNumber: String,
    val code: String
)