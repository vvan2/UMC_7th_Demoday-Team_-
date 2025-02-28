package com.example.umc.model.response

data class DeleteMaterialMarkResponse(
    val error: Any,
    val resultType: String,
    val success: DeleteMaterialMarkSuccess
)

data class DeleteMarkMaterial(
    val markId: Int,
    val materialId: Int,
    val userId: Int
)

data class DeleteMaterialMarkSuccess(
    val isSucess: Boolean,
    val markMaterial: DeleteMarkMaterial
)