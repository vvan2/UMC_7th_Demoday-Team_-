package com.example.umc.Survey

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.umc.UserApi.UserRepository
import com.google.gson.Gson
import kotlinx.coroutines.launch
import retrofit2.HttpException

// ✅ `AndroidViewModel` 사용하여 `context` 문제 해결
class SurveyViewModel(application: Application) : AndroidViewModel(application) {
    private val _surveyData = MutableLiveData(SurveyData())  // LiveData로 변경
    val surveyData: LiveData<SurveyData> get() = _surveyData

    fun updateSurveyData(updatedFields: SurveyData) {
        _surveyData.value = _surveyData.value?.copy(
            goal = updatedFields.goal.ifEmpty { _surveyData.value!!.goal },
            meals = if (updatedFields.meals.isNotEmpty()) updatedFields.meals else _surveyData.value!!.meals,
            allergy = updatedFields.allergy.ifEmpty { _surveyData.value!!.allergy },
            allergyDetails = updatedFields.allergyDetails ?: _surveyData.value!!.allergyDetails,
            healthCondition = updatedFields.healthCondition.ifEmpty { _surveyData.value!!.healthCondition },
            healthConditionDetails = updatedFields.healthConditionDetails ?: _surveyData.value!!.healthConditionDetails,
            gender = updatedFields.gender.ifEmpty { _surveyData.value!!.gender },
            birthYear = if (updatedFields.birthYear > 0) updatedFields.birthYear else _surveyData.value!!.birthYear,
            height = if (updatedFields.height > 0) updatedFields.height else _surveyData.value!!.height,
            currentWeight = if (updatedFields.currentWeight > 0) updatedFields.currentWeight else _surveyData.value!!.currentWeight,
            targetWeight = if (updatedFields.targetWeight > 0) updatedFields.targetWeight else _surveyData.value!!.targetWeight,
            skeletalMuscleMass = if (updatedFields.skeletalMuscleMass > 0) updatedFields.skeletalMuscleMass else _surveyData.value!!.skeletalMuscleMass,
            bodyFatPercentage = if (updatedFields.bodyFatPercentage > 0) updatedFields.bodyFatPercentage else _surveyData.value!!.bodyFatPercentage,
            exerciseFrequency = if (updatedFields.exerciseFrequency > 0) updatedFields.exerciseFrequency else _surveyData.value!!.exerciseFrequency,
            job = updatedFields.job.ifEmpty { _surveyData.value!!.job }
        )
    }

    // ✅ 서버로 데이터 전송 (POST 또는 PUT)
    fun submitSurveyData(
        isUpdate: Boolean,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val token = UserRepository.getAuthToken(getApplication()) ?: ""
                Log.d("SurveyViewModel", "📢 서버 전송 시작! 토큰 확인: $token")

                if (token.isEmpty()) {
                    Log.e("SurveyViewModel", "🚨 저장된 토큰이 없습니다! 로그인 다시 확인 필요")
                    onError("토큰이 없습니다. 다시 로그인해주세요.")
                    return@launch
                }

                val currentData = _surveyData.value!!
                Log.d("SurveyViewModel", "📩 전송 데이터: ${Gson().toJson(currentData)}")

                // ✅ 요청 URL 직접 로깅
                val apiCall = if (isUpdate) {
                    Log.d("SurveyViewModel", "🌍 요청 URL: ${RetrofitClient.apiService.updateSurvey("Bearer $token", currentData)}")
                    RetrofitClient.apiService.updateSurvey("Bearer $token", currentData)
                } else {
                    Log.d("SurveyViewModel", "🌍 요청 URL: ${RetrofitClient.apiService.submitSurvey("Bearer $token", currentData)}")
                    RetrofitClient.apiService.submitSurvey("Bearer $token", currentData)
                }

                Log.d("SurveyViewModel", "✅ 서버 전송 완료!")
                onSuccess()

            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                Log.e("SurveyViewModel", "🚨 서버 응답 오류: $errorBody")
                onError(errorBody ?: "Unknown error")
            } catch (e: Exception) {
                Log.e("SurveyViewModel", "🚨 서버 전송 실패: ${e.message}")
                onError(e.message ?: "Unknown error")
            }
        }
    }

}
