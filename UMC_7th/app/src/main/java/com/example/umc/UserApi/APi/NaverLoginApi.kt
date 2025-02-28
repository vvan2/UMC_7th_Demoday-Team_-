package com.example.umc.UserApi.APi

import com.example.umc.UserApi.Response.NaverLoginResponse
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface NaverLoginApi {
    @GET("auth/naver")
    fun loginWithNaver(): Call<ResponseBody>

//    @GET("auth/naver/token")
//    fun getNaverAccessToken(
//        @Query("code") code: String,
//        @Query("state") state: String
//    ): Call<NaverLoginResponse>
    // 이거 엔드포인트 따로 없음
    @GET("auth/naver/callback")
    fun getNaverAccessToken(
        @Query("code") code: String,
        @Query("state") state: String
    ): Call<NaverLoginResponse>

}
