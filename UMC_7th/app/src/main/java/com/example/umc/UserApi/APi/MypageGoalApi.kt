package com.example.umc.UserApi.APi

import com.example.umc.UserApi.Response.MypageGoalResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header

interface MypageGoalApi {

    // 목표 정보 가져오기
    @GET("api/v1/users/mypage/goal")
    fun getMypageGoal(
        @Header("Authorization") token: String
    ): Call<MypageGoalResponse>
}
