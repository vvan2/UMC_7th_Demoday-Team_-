package com.example.umc.model.response

data class GetMaterialMarkResponse(
    val error: Any,
    val resultType: String,
    val success: GetMaterialMarkSuccess
)

data class GetMaterialMark(
    val markId: Int,
    val material: GetMaterialMarkMaterial
)

data class GetMaterialMarkMaterial(
    val itemId: Int,
    val materialId: Int,
    val name: String,
    val unit: String,
    val variety: GetMaterialMarkVariety
)

data class GetMaterialMarkSuccess(
    val markList: List<GetMaterialMark>
)

data class GetMaterialMarkVariety(
    val name: String
)