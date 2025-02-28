package com.example.umc.UserApi.APi

import com.example.umc.UserApi.Response.DiagnosisResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header

interface DiagnosisApi {
    @GET("api/v1/users/mypage/result")
    suspend fun getDiagnosisResult(@Header("Authorization") token: String): Response<DiagnosisResponse>
}
