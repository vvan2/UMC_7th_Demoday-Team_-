package com.example.umc.model

data class CartItem(
    val date: String,
    val time: String,
    val menu: String,
    var serving: Int,
    var isChecked: Boolean = false
)
