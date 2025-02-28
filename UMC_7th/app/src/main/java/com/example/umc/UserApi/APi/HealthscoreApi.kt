package com.example.umc.UserApi.APi

import com.example.umc.UserApi.Response.HealthScoreResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header

interface HealthScoreApi {
    @GET("api/v1/users/mypage/healthscore")
    suspend fun getHealthScore(@Header("Authorization") token: String): Response<HealthScoreResponse>
}
