package com.example.umc.model

import java.io.Serializable

data class RankingItem(
    val variety: String,
    val name: String,
    val rank: String,  // 이건 필요 없을 수도 있음
    val itemId: String, // itemId 추가
    val unit: String,  // 단위 추가
    val imgUrl: String,
    val price: String,
    val delta: Double
) : Serializable
