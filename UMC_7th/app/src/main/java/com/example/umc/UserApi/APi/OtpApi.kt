package com.example.umc.UserApi.APi

import com.example.umc.UserApi.Request.OtpRequest
import com.example.umc.UserApi.Response.OtpResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface OtpApi {
    @POST("api/v1/users/signup/otp")
    suspend fun requestOtp(@Body request: OtpRequest): Response<OtpResponse>
}