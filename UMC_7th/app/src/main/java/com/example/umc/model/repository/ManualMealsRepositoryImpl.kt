package com.example.umc.model.repository

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.example.umc.UserApi.RetrofitClient
import com.example.umc.model.request.PostManualMealsRequest
import com.example.umc.model.response.GetManualMealsResponse
import com.example.umc.model.response.PostManualMealsResponse
import retrofit2.Response

class ManualMealsRepositoryImpl(private val context: Context) : ManualMealsRepository {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("UserPreferences", Context.MODE_PRIVATE)

    override suspend fun getManualMeals(token: String): Response<GetManualMealsResponse> {
        return RetrofitClient.mealApiService.getManualMeals("Bearer $token")
    }

    override suspend fun postManualMeals(request: PostManualMealsRequest): Response<PostManualMealsResponse> {
        return try {
            val response = RetrofitClient.mealApiService.postManualMeals(
                request,
                token = TODO()
            )

            if (response.isSuccessful) {
                Log.d("MealLogging", "postManualMeals 성공: ${response.body()}")
            } else {
                Log.e("MealLogging", "postManualMeals 실패: ${response.errorBody()?.string()}")
            }
            response
        } catch (e: Exception) {
            Log.e("MealLogging", "postManualMeals 오류 발생: ${e.message}")
            throw e
        }
    }

    // ✅ SharedPreferences에서 Token 가져오는 함수 추가
    fun getAuthToken(): String {
        val token = sharedPreferences.getString("auth_token", "") ?: ""
        Log.d("ManualMealsRepo", "SharedPreferences에서 가져온 토큰: $token") // 로그 추가
        return token
    }
}

