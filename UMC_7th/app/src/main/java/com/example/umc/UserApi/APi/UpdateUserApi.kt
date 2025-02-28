package com.example.umc.UserApi.APi

import com.example.umc.UserApi.Request.UpdateUserRequest
import com.example.umc.UserApi.Response.UpdateUserResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.PUT

interface UpdateUserApi {
    @PUT("api/v1/users/mypage/update")
    suspend fun updateUserProfile(
        @Header("Authorization") token: String,  // 토큰 추가
        @Body request: UpdateUserRequest        // 요청 객체 추가
    ): Response<UpdateUserResponse>
}
