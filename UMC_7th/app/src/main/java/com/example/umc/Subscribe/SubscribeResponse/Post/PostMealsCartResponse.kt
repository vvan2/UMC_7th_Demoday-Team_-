package com.example.umc.Subscribe.SubscribeResponse.Post

data class PostMealsCartResponse(
    val error: PostMealsCartResponseError,
    val resultType: String,
    val success: PostMealsCartResponseSuccess
)

data class PostMealsCartResponseSuccess(
    val kartSub: KartSub
)

data class PostMealsCartResponseError(
    val `data`: String,
    val errorCode: String,
    val reason: String
)

data class KartSub(
    val cnt: Int,
    val kartId: Int,
    val mealSubId: Int,
    val userId: Int
)