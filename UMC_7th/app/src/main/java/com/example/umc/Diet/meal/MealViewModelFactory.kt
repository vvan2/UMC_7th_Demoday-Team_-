package com.example.umc.Diet.meal

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.umc.meal.MealViewModel
import com.example.umc.model.repository.MealRepository

class MealViewModelFactory(
    private val context: Context,
    private val mealRepository: MealRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MealViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MealViewModel(context, mealRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}