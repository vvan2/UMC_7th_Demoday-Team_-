package com.example.umc.model

data class CartRequest(
    val kartSub: KartSubRequest
)

data class KartSubRequest(
    val kartId: Int = 0,
    val userId: Int = 0,
    val mealSubId: Int,
    val cnt: Int
)

data class CartResponse(
    val kartSub: KartSub
)

data class KartSub(
    val kartId: Int,
    val userId: Int,
    val mealSubId: Int,
    val cnt: Int
)