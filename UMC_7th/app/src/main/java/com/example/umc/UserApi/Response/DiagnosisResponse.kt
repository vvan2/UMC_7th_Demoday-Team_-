package com.example.umc.UserApi.Response

data class DiagnosisResponse(
    val success: SuccessData?
)

data class SuccessData(
    val diagnosis: List<String>?,
    val advice: List<String>?
)
