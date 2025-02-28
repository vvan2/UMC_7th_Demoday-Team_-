package com.example.umc.Survey

// ✅ 서버로 전송할 데이터 모델
data class SurveyData(
    val goal: String = "",
    val meals: List<String> = listOf(),
    val allergy: String = "",
    val allergyDetails: String? = null,
    val healthCondition: String = "",
    val healthConditionDetails: String? = null,
    val gender: String = "",
    val birthYear: Int = 0,
    val height: Int = 0,
    val currentWeight: Int = 0,
    val targetWeight: Int = 0,
    val skeletalMuscleMass: Double = 0.0,
    val bodyFatPercentage: Double = 0.0,
    val exerciseFrequency: Int = 0,
    val job: String = ""
)
