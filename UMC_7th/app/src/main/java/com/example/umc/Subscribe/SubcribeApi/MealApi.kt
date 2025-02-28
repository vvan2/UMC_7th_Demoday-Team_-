package com.example.umc.Subscribe.SubscribeApi

import com.example.umc.Subscribe.SubscribeResponse.Get.MealResponse
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.Response
import retrofit2.http.Header

interface MealApi {

    @GET("api/v1/subscribes/users/search")
    suspend fun getMealsByDate(
        @Query("date") date: String,  // "date" 파라미터로 날짜를 전달
        @Header("Authorization") token: String  // 인증 토큰을 헤더로 전달
    ): Response<MealResponse>  // MealResponse는 API 응답을 위한 데이터 클래스
}
