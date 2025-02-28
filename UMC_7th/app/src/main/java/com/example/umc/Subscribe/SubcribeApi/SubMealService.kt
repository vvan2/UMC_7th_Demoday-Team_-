package com.example.umc.Subscribe.SubcribeApi

import com.example.umc.Subscribe.SubscribeRequest.PostSubMealsCartRequest
import com.example.umc.Subscribe.SubscribeResponse.Get.GetSubMealsListResponse
import com.example.umc.Subscribe.SubscribeResponse.Post.DeliveryAddressresponse
import com.example.umc.Subscribe.SubscribeResponse.Post.PostMealsCartResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface SubMealService {
    @GET("api/v1/subscribes/meals/list")
    suspend fun getSubMealsList(
        @Query("Category") category: String,
        @Header("Authorization") token: String
    ): Response<GetSubMealsListResponse>

    @POST("api/v1/subscribes/meals/cart")
    suspend fun postMealsCart(
        @Body request: PostSubMealsCartRequest,
        @Header("Authorization") token: String
    ): Response<PostMealsCartResponse>
}