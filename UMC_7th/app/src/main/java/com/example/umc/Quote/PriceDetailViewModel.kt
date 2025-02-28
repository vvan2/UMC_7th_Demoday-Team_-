package com.example.umc.Quote

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.umc.UserApi.RetrofitClient
import com.example.umc.model.response.GetMaterialVariety
import com.example.umc.model.response.KamisPriceResponse
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PriceDetailViewModel : ViewModel() {
    val varietyMaterialList = MutableLiveData<List<FoodItem>>()

    fun fetchVarietyMaterialList(variety: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.materialApiService.getMaterialsVariety(variety)

                Log.d("PriceDetailViewModel", "API Response: ${response.body()}")
                if (response.isSuccessful) {
                    val data = response.body()?.success?.data ?: emptyList()
                    Log.d("PriceDetailViewModel", "API Success: Received ${data.size} items for variety: $variety")

                    val mappedFoodItems = data.map { item ->
                        FoodItem(
                            variety = item.variety.name,  // 품종명
                            name = item.name.split("/")[0], // "/" 앞부분만 가져오기
                            price = "0", // 가격을 나중에 업데이트할 예정
                            unit = item.unit,
                            imageUrl = "", // 이미지 URL 추가 가능
                            itemId = item.itemId.toString(), // itemId 추가 (가격을 가져오기 위해 필요)
                            delta = item.delta
                        )
                    }

                    varietyMaterialList.postValue(mappedFoodItems)

                    // 각 FoodItem의 가격을 가져오기 위해 fetchPrice 호출
                    mappedFoodItems.forEach { foodItem ->
                        fetchPriceForItem(foodItem)
                    }
                } else {
                    Log.e("PriceDetailViewModel", "API Error: ${response.code()} - ${response.message()}")
                    varietyMaterialList.postValue(emptyList())
                }
            } catch (e: Exception) {
                Log.e("PriceDetailViewModel", "API Exception: ${e.message}", e)
                varietyMaterialList.postValue(emptyList())
            }
        }
    }

    // 가격을 가져오는 메서드
    private fun fetchPriceForItem(foodItem: FoodItem) {
        val call = RetrofitClient.kamisService.getDailySalesList(
            productNo = foodItem.itemId,
            regDay = "2025-02-21", // 날짜 설정
            certKey = "1177e9f8-8f03-45ec-9cef-318101246a8d", // 인증 키
            certId = "rmarkdalswn@naver.com", // 인증 ID
            returnType = "json" // 응답 타입
        )

        call.enqueue(object : Callback<KamisPriceResponse> {
            override fun onResponse(
                call: Call<KamisPriceResponse>,
                response: Response<KamisPriceResponse>
            ) {
                if (response.isSuccessful) {
                    val price = response.body()?.price?.firstOrNull()?.d10.toString() ?: "0"
                    // 가격 업데이트
                    updateFoodItemPrice(foodItem.itemId, price)
                } else {
                    Log.e("PriceDetailViewModel", "가격 정보를 가져오는데 실패했습니다: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<KamisPriceResponse>, t: Throwable) {
                Log.e("PriceDetailViewModel", "가격 정보를 요청하는 데 실패했습니다: ${t.message}")
            }
        })
    }

    // 가격 업데이트 메서드
    private fun updateFoodItemPrice(itemId: String, price: String) {
        val updatedList = varietyMaterialList.value?.map { foodItem ->
            if (foodItem.itemId == itemId) {
                foodItem.copy(price = price) // 가격 업데이트
            } else {
                foodItem
            }
        }
        varietyMaterialList.postValue(updatedList)
    }
}