package com.example.umc.model.response

data class GetMaterialAllResponse(
    val error: Any,
    val resultType: String,
    val success: GetMaterialAllSuccess
)

data class GetMaterialAllData(
    val itemId: Int,
    val materialId: Int,
    val name: String,
    val unit: String,
    val delta: Double,
    val variety: GetMaterialAllVariety
)

data class GetMaterialAllVariety(
    val name: String
)

data class GetMaterialAllSuccess(
    val `data`: List<GetMaterialAllData>,
    val isSuccess: Boolean
)