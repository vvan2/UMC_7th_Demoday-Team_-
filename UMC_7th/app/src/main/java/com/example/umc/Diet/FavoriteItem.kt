package com.example.umc.Diet

data class FavoriteItem(
    val imageUrl: String,
    val name: String,
    val calories: String,
    val isSelected: Boolean = false,
    var isFavorite: Boolean = true
)
