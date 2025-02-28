package com.example.umc.model.response

data class PostMaterialMarkResponse(
    val error: Any,
    val resultType: String,
    val success: PostMaterialMarkSuccess
)

data class MarkMaterial(
    val markId: Int,
    val materialId: Int,
    val userId: Int
)

data class PostMaterialMarkSuccess(
    val isSucess: Boolean,
    val markMaterial: MarkMaterial
)