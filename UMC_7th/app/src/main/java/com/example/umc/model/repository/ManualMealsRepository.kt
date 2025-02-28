package com.example.umc.model.repository

import com.example.umc.model.request.PostManualMealsRequest
import com.example.umc.model.response.GetManualMealsResponse
import com.example.umc.model.response.PostManualMealsResponse
import retrofit2.Response

interface ManualMealsRepository {
    suspend fun postManualMeals(request: PostManualMealsRequest): Response<PostManualMealsResponse>
    suspend fun getManualMeals(token: String): Response<GetManualMealsResponse>
}
