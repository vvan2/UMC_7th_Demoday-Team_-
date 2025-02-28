package com.example.umc.model.repository

import android.content.Context
import android.util.Log
import com.example.umc.model.MaterialData
import com.example.umc.model.response.GetMaterialAllResponse
import com.example.umc.model.response.GetMaterialRankAllData
import com.example.umc.model.service.MaterialApiService

class MaterialRepository(
    private val materialApiService: MaterialApiService,
    private val context: Context
) {
    suspend fun getMaterialRankAll(): Result<List<MaterialData>> {
        return try {
            Log.d("MaterialRepository", "1. Repository - API 호출 시작")

            val response = materialApiService.getMaterialRankAll()

            Log.d("MaterialRepository", "2. Repository - 응답 수신: ${response.code()}")
            Log.d("MaterialRepository", "3. Repository - 응답 본문: ${response.body()}")

            if (response.isSuccessful) {
                val responseBody = response.body()
                when {
                    responseBody?.resultType == "SUCCESS" -> {
                        val materialDataList = responseBody.success?.data?.map { data ->
                            MaterialData(
                                itemId = data.itemId,
                                materialId = data.materialId,
                                name = data.name,
                                delta = data.delta,
                                unit = data.unit,
                                varietyName = data.variety.name
                            )
                        } ?: emptyList()
                        Log.d("MaterialRepository", "4. Repository - 성공: $materialDataList")
                        Result.Success(materialDataList)
                    }
                    responseBody?.error != null -> {
                        Log.e("MaterialRepository", "4. Repository - 에러: ${responseBody.error}")
                        Result.Error(errorMessage = "서버 오류: ${responseBody.error}")
                    }
                    else -> Result.Error(errorMessage = "알 수 없는 응답 형식")
                }
            } else {
                Result.Error(errorMessage = "API 호출 실패: ${response.code()}")
            }
        } catch (e: Exception) {
            Log.e("MaterialRepository", "Repository - 예외 발생", e)
            Result.Error(errorMessage = "네트워크 오류: ${e.message}")
        }
    }
}


