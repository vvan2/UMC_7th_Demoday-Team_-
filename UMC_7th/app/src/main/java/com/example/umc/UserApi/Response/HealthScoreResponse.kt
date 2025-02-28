package com.example.umc.UserApi.Response

data class HealthScoreResponse(
    val resultType: String,
    val error: Any?,
    val success: HealthScoreData?
)

data class HealthScoreData(
    val healthscoreId: Int,
    val userId: Int,
    val healthScore: Int,
    val comparison: String,
    val createdAt: String,
    val updateAt: String
)
