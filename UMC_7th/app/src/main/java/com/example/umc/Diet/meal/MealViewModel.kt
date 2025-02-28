package com.example.umc.meal

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.umc.UserApi.UserRepository
import com.example.umc.model.repository.MealRepository
import com.example.umc.model.repository.Result
import com.example.umc.model.response.MealRefreshSuccess
import com.example.umc.model.response.PostDailyMealSuccess
import com.example.umc.model.response.PostDailyMealSuccessWrapper
import kotlinx.coroutines.launch
import org.jetbrains.annotations.ApiStatus
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MealViewModel(
    private val context: Context,
    private val mealRepository: MealRepository
) : ViewModel() {
    private val _apiStatus = MutableLiveData<ApiStatus>()
    val apiStatus: LiveData<ApiStatus> = _apiStatus

    enum class ApiStatus {
        LOADING, SUCCESS, ERROR, NO_CONNECTION
    }

    private val _dailyMeals = MutableLiveData<PostDailyMealSuccessWrapper>()
    val dailyMeals: LiveData<PostDailyMealSuccessWrapper> = _dailyMeals

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    // 일일 식사 데이터를 가져오는 함수
    fun fetchDailyMeal(mealDate: String) {
        Log.d("MEAL_DEBUG", "fetchDailyMeal 시작")

        Log.d("MEAL_DEBUG", "요청 날짜: $mealDate")

        viewModelScope.launch {
            _apiStatus.value = ApiStatus.LOADING
            _errorMessage.value = ""

            try {
                Log.d("MEAL_DEBUG", "Repository 호출 시작")
                val result = mealRepository.getDailyMeal(mealDate)

                when (result) {
                    is Result.Success -> {
                        Log.d("MEAL_DEBUG", "데이터 fetch 성공")
                        Log.d("MEAL_DEBUG", "받은 데이터: ${result.data}")
                        Log.d("MEAL_DEBUG", "데이터 크기: ${result.data.existingMeals?.size ?: 0}")

                        _apiStatus.value = ApiStatus.SUCCESS
                        _dailyMeals.value = result.data

                        if (result.data.existingMeals.isNullOrEmpty()) {
                            Log.d("MEAL_DEBUG", "데이터 없음")
                            _errorMessage.value = "금일 식단 데이터가 없습니다."
                        }
                    }
                    is Result.Error -> {
                        Log.e("MEAL_DEBUG", "에러 발생: ${result.errorMessage}")
                        _apiStatus.value = ApiStatus.ERROR
                        _errorMessage.value = result.errorMessage
                    }
                }
            } catch (e: Exception) {
                Log.e("MEAL_DEBUG", "예외 발생", e)
                _apiStatus.value = ApiStatus.NO_CONNECTION
                _errorMessage.value = "네트워크 연결 실패: ${e.message}"
            }
        }
    }
    // 식사 새로고침 함수
    fun refreshMeal(mealId: Int) {
        viewModelScope.launch {
            _apiStatus.value = ApiStatus.LOADING
            _errorMessage.value = ""

            try {
                when (val result = mealRepository.refreshMeal(mealId)) {
                    is Result.Success -> {
                        val currentMeals = _dailyMeals.value?.existingMeals?.toMutableList() ?: mutableListOf()
                        val index = currentMeals.indexOfFirst { it.mealId == mealId }

                        if (index != -1) {
                            currentMeals[index] = result.data.toPostDailyMealSuccess()
                            _dailyMeals.value = PostDailyMealSuccessWrapper(
                                existingMeals = currentMeals
                            )
                        }
                        _apiStatus.value = ApiStatus.SUCCESS
                    }
                    is Result.Error -> {
                        _apiStatus.value = ApiStatus.ERROR
                        _errorMessage.value = result.errorMessage
                    }
                }
            } catch (e: Exception) {
                _apiStatus.value = ApiStatus.NO_CONNECTION
                _errorMessage.value = "네트워크 연결 실패: ${e.message}"
            }
        }
    }
}

    private fun MealRefreshSuccess.toPostDailyMealSuccess() = PostDailyMealSuccess(
        addedByUser = this.addedByUser,
        calorieDetail = this.calorieDetail,
        calorieTotal = this.calorieTotal,
        difficulty = this.difficulty,
        food = this.food,
        material = this.material,
        mealId = this.mealId,
        price = this.price,
        recipe = this.recipe
    )
