package com.example.umc.Diet

data class DietItem(
    val mealDate: String,
    val week : String,
    val time: String,
    val food: String,
    var isChecked : Boolean
)
