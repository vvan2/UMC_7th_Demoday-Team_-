package com.example.umc.Subscribe.SubscribeResponse.Patch

import com.example.umc.Subscribe.SubscribeResponse.Get.DeliveryGetResponse

data class DeliveryAddressPatchresponse(
    val resultType: String, // ✅ 응답 결과 (SUCCESS / FAILURE)
    val error: Any?, // ✅ 에러 메시지 (null 가능)
    val success: SuccessDeliveryPatchResponse? // ✅ 성공 응답
)

data class SuccessDeliveryPatchResponse(
    val deliveryAddress: DeliveryGetResponse // ✅ 단일 배송지 정보
)
