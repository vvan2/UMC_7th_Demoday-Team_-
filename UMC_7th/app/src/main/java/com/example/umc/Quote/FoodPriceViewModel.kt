package com.example.umc.Quote

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.umc.UserApi.RetrofitClient
import com.example.umc.model.response.KamisPriceResponse
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FoodPriceViewModel : ViewModel() {
    private val _isMark = MutableLiveData<Boolean>()
    val isMark: LiveData<Boolean> get() = _isMark

    private val _priceData = MutableLiveData<KamisPriceResponse>()
    val priceData: LiveData<KamisPriceResponse> get() = _priceData

    // 선택된 식재료의 itemId를 저장할 변수
    private var selectedItemId: String? = null

    // itemId 설정 함수
    fun setSelectedItemId(itemId: String) {
        selectedItemId = itemId
    }

    // 선택된 itemId로 가격 데이터를 가져오는 함수
    fun fetchRecentlyPriceTrendList(
        regDay: String = "2025-02-20",
        certKey: String = "1177e9f8-8f03-45ec-9cef-318101246a8d",
        certId: String = "rmarkdalswn@naver.com",
        returnType: String = "Json"
    ) {
        // selectedItemId가 null이 아닌 경우에만 요청
        selectedItemId?.let {
            Log.d("KamisAPI", "Fetching price trend for product $it...")

            // Retrofit API 요청
            val call = RetrofitClient.kamisService.getDailySalesList(
                productNo = it,  // 여기서 itemId를 productNo로 사용
                regDay = regDay,
                certKey = certKey,
                certId = certId
            )

            call.enqueue(object : Callback<KamisPriceResponse> {
                override fun onResponse(call: Call<KamisPriceResponse>, response: Response<KamisPriceResponse>) {
                    if (response.isSuccessful) {
                        Log.d("KamisAPI", "Response received successfully: ${response.body()}")
                        _priceData.postValue(response.body())
                    } else {
                        Log.e("KamisAPI", "Error: ${response.code()} ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<KamisPriceResponse>, t: Throwable) {
                    Log.e("KamisAPI", "Failed to connect: ${t.message}")
                }
            })
        } ?: Log.e("FoodPriceViewModel", "itemId is not set")
    }
}
