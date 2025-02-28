package com.example.umc.Quote

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.umc.UserApi.RetrofitClient
import com.example.umc.model.RankingItem
import com.example.umc.model.response.KamisPriceResponse
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class PriceViewModel : ViewModel() {
    val hotMaterialList = MutableLiveData<List<RankingItem>>()

    private val _selectedItemId = MutableLiveData<String?>()
    val selectedItemId: LiveData<String?> get() = _selectedItemId

    private val _priceData = MutableLiveData<KamisPriceResponse?>() // 가격 정보 LiveData 추가
    val priceData: LiveData<KamisPriceResponse?> get() = _priceData

    fun setSelectedItemId(itemId: String) {
        _selectedItemId.value = itemId
        Log.d("PriceViewModel", "Selected ItemId set to: $itemId")
    }

    fun fetchHotMaterialList() {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.materialApiService.getMaterialRankAll()
                if (response.isSuccessful) {
                    val data = response.body()?.success?.data ?: emptyList()

                    // 가격 정보와 함께 RankingItem 목록 만들기
                    val mappedRankingItems = data.mapIndexed { index, item ->
                        RankingItem(
                            variety = item.variety.name,
                            name = item.name,
                            rank = (index + 1).toString(),
                            itemId = item.itemId.toString(),
                            unit = item.unit,
                            imgUrl = "https://example.com/default.jpg",
                            delta = item.delta,
                            price = "" // 가격은 KAMIS API로 받아온 값으로 나중에 업데이트
                        )
                    }

                    hotMaterialList.postValue(mappedRankingItems)

                    // 가격 데이터 가져오기
                    mappedRankingItems.forEach { rankingItem ->
                        fetchRecentlyPriceForItem(rankingItem.itemId)
                    }
                } else {
                    hotMaterialList.postValue(emptyList())
                }
            } catch (e: Exception) {
                hotMaterialList.postValue(emptyList())
                Log.e("PriceViewModel", "Error fetching material list: ${e.message}")
            }
        }
    }

    fun fetchRecentlyPriceForItem(itemId: String) {
        val call = RetrofitClient.kamisService.getDailySalesList(
            productNo = itemId,
            regDay = "2025-02-21", // 요청 날짜, 필요시 동적으로 변경
            certKey = "1177e9f8-8f03-45ec-9cef-318101246a8d",
            certId = "rmarkdalswn@naver.com",
            returnType = "json"
        )

        call.enqueue(object : Callback<KamisPriceResponse> {
            override fun onResponse(
                call: Call<KamisPriceResponse>,
                response: Response<KamisPriceResponse>
            ) {
                if (response.isSuccessful) {
                    val price = response.body()?.price?.firstOrNull()?.d10.toString() ?: "0"
                    // 가격 업데이트 로직
                    updatePriceForItem(itemId, price)
                } else {
                    Log.e("PriceViewModel", "가격 정보를 가져오는데 실패했습니다: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<KamisPriceResponse>, t: Throwable) {
                Log.e("PriceViewModel", "가격 정보를 요청하는 데 실패했습니다: ${t.message}")
            }
        })
    }

    private fun updatePriceForItem(itemId: String, price: String) {
        val updatedList = hotMaterialList.value?.map { item ->
            if (item.itemId == itemId) {
                item.copy(price = price)
            } else {
                item
            }
        }
        hotMaterialList.postValue(updatedList)
    }
}