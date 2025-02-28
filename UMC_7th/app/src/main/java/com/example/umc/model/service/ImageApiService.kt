package com.example.umc.model.service

import com.example.umc.model.response.GetMaterialImageResponse
import com.example.umc.model.response.GetMealImageResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ImageApiService {
    @GET("api/v1/images/material")
    suspend fun getMaterialImage(
        @Query("name") name: String
    ): Response<GetMaterialImageResponse>

    @GET("api/v1/images/meal")
    suspend fun getMealImage(
        @Query("name") name: String
    ): Response<GetMealImageResponse>
}