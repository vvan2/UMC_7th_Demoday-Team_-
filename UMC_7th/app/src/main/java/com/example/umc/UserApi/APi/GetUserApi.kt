package com.example.umc.UserApi.APi

import com.example.umc.UserApi.Response.UserProfileResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header

interface GetUserApi {
    @GET("api/v1/users/mypage/profile")
    suspend fun getUserProfile(
        @Header("Authorization") token: String  // 헤더에 Authorization 추가
    ): Response<UserProfileResponse>
}