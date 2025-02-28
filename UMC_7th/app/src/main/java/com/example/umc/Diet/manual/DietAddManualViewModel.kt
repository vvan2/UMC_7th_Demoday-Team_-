package com.example.umc.Diet.manual

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import com.example.umc.UserApi.RetrofitClient
import com.example.umc.model.ManualMeals
import com.example.umc.model.request.PostManualMealsRequest
import android.util.Log

class DietAddManualViewModel : ViewModel() {

    private val _mealList = MutableLiveData<List<ManualMeals>>()
    val mealList: LiveData<List<ManualMeals>> get() = _mealList

    fun postManualMeals(context: Context, request: PostManualMealsRequest, accessToken: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.mealApiService.postManualMeals(request, "Bearer $accessToken")
                if (response.isSuccessful && response.body() != null) {
                    val meal = response.body()!!.success
                    val newMeal = meal?.let {
                        ManualMeals(
                            mealId = 0,
                            calorieTotal = it.calorieTotal,
                            foods = it.food.split(", ").map { it.trim() },
                            time = "",
                            mealDate = "",
                        )
                    }
                    if (newMeal != null) {
                        updateMealList(newMeal)
                    }
                    onSuccess()
                } else {
                    onError("API 요청 실패: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                onError("오류 발생: ${e.message}")
            }
        }
    }

    private fun updateMealList(newMeal: ManualMeals) {
        val currentList = _mealList.value ?: emptyList()
        _mealList.value = currentList + newMeal
    }
}
