package com.example.umc.Subscribe.SubscribeResponse.Put

import com.example.umc.Subscribe.SubscribeResponse.Get.DeliveryGetResponse

data class SuccessDelivertPutResponse(
    val result: List<DeliveryPutResponse>
)
