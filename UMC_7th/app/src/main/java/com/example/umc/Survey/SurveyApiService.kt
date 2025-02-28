package com.example.umc.Survey

import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT

interface SurveyApiService {
    @POST("/api/v1/users/survey")
    suspend fun submitSurvey(
        @Header("Authorization") token: String,  // ✅ 헤더에 토큰 추가
        @Body surveyData: SurveyData
    )

    @PUT("/api/v1/users/survey")
    suspend fun updateSurvey(
        @Header("Authorization") token: String,  // ✅ 헤더에 토큰 추가
        @Body surveyData: SurveyData
    )
}


