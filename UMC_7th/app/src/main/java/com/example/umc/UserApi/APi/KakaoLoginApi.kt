package com.example.umc.UserApi.APi

import com.example.umc.UserApi.Response.LoginResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface KakaoLoginApi {

    @GET("auth/kakao")  // 카카오 로그인 API 엔드포인트 (GET 방식으로 수정)
    fun loginWithKakao(@Query("access_token") accessToken: String): Call<LoginResponse>
}
