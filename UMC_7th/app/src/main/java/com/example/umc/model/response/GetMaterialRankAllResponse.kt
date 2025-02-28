package com.example.umc.model.response

data class GetMaterialRankAllResponse(
    val error: Any,
    val resultType: String,
    val success: GetMaterialRankAllSuccess
)

data class GetMaterialRankAllData(
    val itemId: Int,
    val materialId: Int,
    val name: String,
    val unit: String,
    val delta: Double,
    val variety: GetMaterialRankAllVariety
)


data class GetMaterialRankAllSuccess(
    val `data`: List<GetMaterialRankAllData>,
    val isSuccess: Boolean
)

data class GetMaterialRankAllVariety(
    val name: String
)