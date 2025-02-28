package com.example.umc.Diet

data class MenuItem(

    val imageUrl: String,
    val name: String,
    val calories: String,
    val mealId: Int,
    val material: String?,       // 재료
    val recipe: String?,         // 레시피
    val calorieDetail: String?,  // 영양 정보
    val difficulty: Int?,        // 난이도를 Int?로 변경
    val price: Int?,              // 추가
    val addedByUser: Boolean,     // 추가

    val isSelected: Boolean = false,
    var isFavorite: Boolean = false,
    var isDietCompleted: Boolean = false
)
