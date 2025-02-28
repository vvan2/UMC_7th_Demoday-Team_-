package com.example.umc.Diet.manual

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.umc.UserApi.RetrofitClient
import com.example.umc.model.ManualMeals
import com.example.umc.model.repository.ManualMealsRepositoryImpl
import com.example.umc.model.request.DeleteManualMealsRequest
import kotlinx.coroutines.launch

class DietAddConfirmViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = ManualMealsRepositoryImpl(application.applicationContext)

    private val _mealList = MutableLiveData<List<ManualMeals>>()
    val mealList: LiveData<List<ManualMeals>> get() = _mealList

    private fun getAuthToken(): String {
        var token = repository.getAuthToken()
        token = token.trim()
        return token
    }

    fun fetchManualMeals(context: Context, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val token = getAuthToken()
                if (token.isEmpty()) {
                    return@launch
                }

                val response = RetrofitClient.mealApiService.getManualMeals("Bearer $token")

                if (response.isSuccessful && response.body() != null) {
                    val successList = response.body()?.success ?: emptyList()
                    val mealList = successList.map { success ->
                        ManualMeals(
                            mealId = success.mealId,
                            calorieTotal = success.calorieTotal,
                            foods = success.food.split(",").map { it.trim() },
                            time = success.MealUser.firstOrNull()?.time ?: "",
                            mealDate = success.MealUser.firstOrNull()?.mealDate ?: ""
                        )
                    }
                    _mealList.value = mealList
                    onSuccess()
                } else {
                    onError("API 요청 실패: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                onError("오류 발생: ${e.message}")
            }
        }
    }

    fun deleteManualMeals(mealId: Int, position: Int) {
        viewModelScope.launch {
            try {
                val token = getAuthToken()
                if (token.isEmpty()) {
                    return@launch
                }

                val requestBody = DeleteManualMealsRequest(mealId)

                val response = RetrofitClient.mealApiService.deleteManualMeals(
                    requestBody, "Bearer $token"
                )

                if (response.isSuccessful) {
                    Log.d("MealLogging", "식단 삭제 성공")
                    val updatedList = _mealList.value?.toMutableList()
                    updatedList?.removeAt(position)
                    _mealList.value = updatedList
                } else {
                    Log.e("MealLogging", "API 요청 실패: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("MealLogging", "삭제 요청 오류: ${e.message}")
            }
        }
    }
}
