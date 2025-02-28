package com.example.umc.Subscribe

data class OrderMenuItem(
    val menuDate: String,          // "1/22 (수) 식단 - 배송 완료"
    val breakfastName: String,     // "하루 시작 포케 - 2인분"
    val breakfastKcal: Int,        // 420
    val breakfastPrice: Int,       // 20000
    val lunchName: String,         // "고등어 조림 한상 - 1인분"
    val lunchKcal: Int,            // 840
    val lunchPrice: Int            // 12000
)