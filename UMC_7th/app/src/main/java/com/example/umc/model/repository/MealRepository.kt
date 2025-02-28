package com.example.umc.model.repository

import android.content.Context
import android.util.Log
import com.example.umc.UserApi.UserRepository
import com.example.umc.model.request.MealRefreshRequest
import com.example.umc.model.request.PostDailyMealRequest
import com.example.umc.model.response.MealRefreshSuccess
import com.example.umc.model.response.PostDailyMealSuccessWrapper
import com.example.umc.model.service.MealApiService

class MealRepository(
    private val mealApiService: MealApiService,
    private val context: Context
) {
    suspend fun getDailyMeal(mealDate: String): Result<PostDailyMealSuccessWrapper> {
        return try {
            Log.e("ERROR_CHECK", "1. Repository - API 호출 시작")
            val token = UserRepository.getAuthToken(context)

            val request = PostDailyMealRequest(mealDate = mealDate)

            val response = mealApiService.getDailyMeal(
                dailyMealRequest = request,
                token = "Bearer $token"
            )

            Log.e("ERROR_CHECK", "2. Repository - 응답 수신: ${response.code()}")
            Log.e("ERROR_CHECK", "3. Repository - 응답 본문: ${response.body()}")

            if (response.isSuccessful) {
                val responseBody = response.body()
                when {
                    responseBody?.resultType == "SUCCESS" -> {
                        Log.e("ERROR_CHECK", "4. Repository - 성공: ${responseBody.success}")
                        Result.Success(responseBody.success ?: PostDailyMealSuccessWrapper())
                    }
                    responseBody?.error != null -> {
                        Log.e("ERROR_CHECK", "4. Repository - 에러: ${responseBody.error}")
                        Result.Error(errorMessage = "서버 오류: ${responseBody.error.reason}")
                    }
                    else -> Result.Error(errorMessage = "알 수 없는 응답 형식")
                }
            } else {
                Result.Error(errorMessage = "API 호출 실패: ${response.code()}")
            }
        } catch (e: Exception) {
            Log.e("ERROR_CHECK", "Repository - 예외 발생", e)
            Result.Error(errorMessage = "네트워크 오류: ${e.message}")
        }
    }

    suspend fun refreshMeal(mealId: Int): Result<MealRefreshSuccess> {
        return try {
            val request = MealRefreshRequest(mealId)
            val response = mealApiService.refreshMeal(request)

            when (response.resultType) {
                "SUCCESS" -> {
                    response.success?.let {
                        Result.Success(it)
                    } ?: Result.Error(errorMessage = "Success response with null data")
                }
                "ERROR" -> {
                    Result.Error(errorMessage = response.error?.reason ?: "Unknown error occurred")
                }
                else -> Result.Error(errorMessage = "Unknown result type")
            }
        } catch (e: Exception) {
            Result.Error(errorMessage = e.message ?: "Network error occurred")
        }
    }
}
