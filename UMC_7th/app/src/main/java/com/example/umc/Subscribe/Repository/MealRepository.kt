package com.example.umc.Subscribe.Repository

import android.content.Context
import com.example.umc.Subscribe.Retrofit.RetrofitClient
import com.example.umc.UserApi.UserRepository
import com.example.umc.Subscribe.SubscribeResponse.Get.MealResponse
import retrofit2.Response

class MealRepository(private val context: Context) {

    // 날짜별 식단 데이터를 요청하는 메서드
    suspend fun getMealsByDate(date: String): Result<MealResponse> {
        val token = UserRepository.getAuthToken(context) ?: return Result.failure(Exception("인증 토큰이 없습니다."))

        return try {
            val response: Response<MealResponse> = RetrofitClient.getMealApi(context)
                .getMealsByDate(date, "Bearer $token")

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)  // 응답이 성공하면 데이터를 반환
            } else {
                Result.failure(Exception("식단 정보 조회 실패: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)  // 예외 처리
        }
    }
}
