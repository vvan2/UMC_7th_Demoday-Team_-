package com.example.umc.model.response

data class GetMaterialRankVarietyResponse(
    val error: Any,
    val resultType: String,
    val success: GetMaterialRankVarietySuccess
)

data class GetMaterialRankVarietyData(
    val itemId: Int,
    val materialId: Int,
    val name: String,
    val delta: Double,
    val unit: String,
    val variety: Variety
)

data class GetMaterialRankVarietySuccess(
    val `data`: List<GetMaterialRankVarietyData>,
    val isSucess: Boolean
)

data class Variety(
    val name: String
)