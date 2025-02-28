package com.example.umc.Subscribe

data class OrderItem(
    val deliveryDate: String,
    val deliveryStatus: String,
    val deliveryLocation: String,
    val menuName: String,
    val breakfastMenu: String,
    val lunchMenu: String,
    val imageUrl: String,
    val isReviewEnabled: Boolean = true
)