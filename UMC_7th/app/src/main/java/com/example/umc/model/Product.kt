package com.example.umc.model

// 상품 데이터 클래스
data class Product(
    val id: Int,
    val name: String,
    val price: String,
    val unit: String,
    val imageUrl: String,
    val itemId: String,
    val delta: Double
)