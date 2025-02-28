package com.example.umc.Subscribe.SubscribeResponse.Get

data class DeliveryAddressGetresponse(
    val resultType: String,
    val error: Any?, // error가 null일 수도 있으므로 Any?로 선언
    val success: SuccessDeliveryResponse?

)
