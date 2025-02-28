package com.example.umc.Subscribe.SubscribeResponse.Post

data class DeliveryAddressresponse(
    val resultType: String,
    val error: String?,
    val success: SuccessAddressResponse
)
