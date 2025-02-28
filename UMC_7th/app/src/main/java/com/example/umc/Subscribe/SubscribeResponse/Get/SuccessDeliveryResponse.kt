package com.example.umc.Subscribe.SubscribeResponse.Get

import com.example.umc.Subscribe.SubscribeResponse.Post.DeliveryAddressresponse

data class SuccessDeliveryResponse(
    val result: List<DeliveryAddressresponse>
)
