package com.example.umc.Subscribe.SubscribeResponse.Get

data class MealResponse(
    val resultType: String,
    val error: String?,
    val success: List<MealData>
)

data class MealData(
    val orderAt: String,
    val mealSub: MealSub
)

data class MealSub(
    val mealDate: String,
    val time: String,
    val meal: Meal,
    val category: Category
)

data class Meal(
    val mealId: Int,
    val food: String,
    val calorieTotal: Int
)

data class Category(
    val name: String
)
