package com.example.umc.model.response

data class PostDailyMealResponse(
    val resultType: String,
    val success: PostDailyMealSuccessWrapper?,
    val error: PostDailyMealError? = null,
    val mealType: String? = null
)
data class PostDailyMealSuccessWrapper(
    val mealDate: String? = null,
    val existingMeals: List<PostDailyMealSuccess>? = null
)
data class PostDailyMealSuccess(
    val mealId: Int,
    val food: String?,
    val calorieTotal: Int?,
    val calorieDetail: String?,
    val material: String?,
    val price: Int?,
    val difficulty: Int?,
    val recipe: String?,
    val addedByUser: Boolean,
    val mealType: String = ""
)

data class PostDailyMealError(
    val `data`: String,
    val errorCode: String,
    val reason: String
)