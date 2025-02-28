package com.example.umc.model.response

data class GetMaterialVarietyResponse(
    val error: Any,
    val resultType: String,
    val success: GetMaterialVarietySuccess
)

data class GetMaterialVarietyData(
    val itemId: Int,
    val materialId: Int,
    val name: String,
    val delta: Double,
    val unit: String,
    val variety: GetMaterialVariety
)

data class GetMaterialVarietySuccess(
    val `data`: List<GetMaterialVarietyData>,
    val isSuccess: Boolean
)

data class GetMaterialVariety(
    val name: String
)