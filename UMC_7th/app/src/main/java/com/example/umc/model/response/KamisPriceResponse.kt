package com.example.umc.model.response

import com.google.gson.annotations.SerializedName

data class KamisPriceResponse(
    val condition: List<ConditionItem>,  // ✅ List로 변경
    val error_code: String,
    val price: List<PriceItem>           // ✅ price 필드도 리스트
)

data class ConditionItem(
    val p_productno: String,
    val p_regday: String,
    val p_cert_key: String,
    val p_cert_id: String,
    val p_returntype: String
)

data class PriceItem(
    val yyyy: String,
    val d40: Any,  // Can be either a String or a List
    val d30: Any,
    val d20: Any,
    val d10: Any,
    val d0: Any,
    val mx: String,
    val mn: String
)