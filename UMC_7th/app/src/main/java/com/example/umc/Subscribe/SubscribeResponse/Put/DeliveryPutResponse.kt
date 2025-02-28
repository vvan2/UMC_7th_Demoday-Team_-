package com.example.umc.Subscribe.SubscribeResponse.Put

data class DeliveryPutResponse(
    val addressId: Int,
    val userId: Int,
    val name: String,
    val address: String,
    val postNum: Int,
    val phoneNum: String,
    val memo: String,
    val isDefault: Boolean
)
