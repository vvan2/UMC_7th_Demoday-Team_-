package com.example.umc.Diet

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.umc.UserApi.RetrofitClient
import com.example.umc.model.repository.ManualMealsRepositoryImpl
import com.example.umc.model.response.GetFavoriteMeals
import com.example.umc.model.response.GetFavoriteMealsResponse
import kotlinx.coroutines.launch

class DietFavoriteViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = ManualMealsRepositoryImpl(application.applicationContext)

    private val _favoriteMeals = MutableLiveData<List<GetFavoriteMeals>>()
    val favoriteMeals: LiveData<List<GetFavoriteMeals>> get() = _favoriteMeals

    fun fetchFavoriteMeals() {
        viewModelScope.launch {
            try {
                val token = getAuthToken()
                if (token.isEmpty()) {
                    Log.e("MealLogging", "토큰이 없습니다..")
                    return@launch
                }

                val response = RetrofitClient.mealApiService.getFavoriteLatest("Bearer $token")
                if (response.isSuccessful && response.body() != null) {
                    // GetFavoriteMealsResponse로 캐스팅 후 success 리스트를 추출
                    val meals = response.body()?.success // This is the List<GetFavoriteMealsSuccess>
                    if (meals != null) {
                        // Extract the 'meal' field from each GetFavoriteMealsSuccess item
                        _favoriteMeals.value = meals.map { it.meal }
                    } else {
                        Log.e("MealLogging", "예상치 못한 응답 형식")
                    }
                } else {
                    Log.e("MealLogging", "API 요청 실패: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("MealLogging", "오류 발생: ${e.message}")
            }
        }
    }

    fun getFavoriteMealsCalorie() {
        viewModelScope.launch {
            try {
                val token = getAuthToken()
                if (token.isEmpty()) {
                    Log.e("MealLogging", "토큰이 없습니다..")
                    return@launch
                }

                val response = RetrofitClient.mealApiService.getFavoriteCalorie("Bearer $token")
                if (response.isSuccessful && response.body() != null) {
                    // GetFavoriteMealsResponse로 캐스팅 후 success 리스트를 추출
                    val meals = response.body()?.success // This is the List<GetFavoriteMealsSuccess>
                    if (meals != null) {
                        // Extract the 'meal' field from each GetFavoriteMealsSuccess item
                        _favoriteMeals.value = meals.map { it.meal }
                    } else {
                        Log.e("MealLogging", "예상치 못한 응답 형식")
                    }
                } else {
                    Log.e("MealLogging", "API 요청 실패: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("MealLogging", "오류 발생: ${e.message}")
            }
        }
    }

    private fun getAuthToken(): String {
        var token = repository.getAuthToken()
        token = token.trim() // 불필요한 문자 제거
        Log.d("DietFavoriteViewModel", "불러온 토큰: $token")
        return token
    }
}
