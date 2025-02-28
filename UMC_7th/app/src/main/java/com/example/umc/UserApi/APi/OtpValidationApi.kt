package com.example.umc.UserApi.APi

import com.example.umc.UserApi.Request.OtpValidationRequest
import com.example.umc.UserApi.Response.OtpResponse
import com.example.umc.UserApi.Response.OtpValidationResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface OtpValidationApi {
    @POST("api/v1/users/signup/otp/validation")
    suspend fun validateOtp(@Body request: OtpValidationRequest): OtpValidationResponse // OtpResponse → OtpValidationResponse 변경
}
