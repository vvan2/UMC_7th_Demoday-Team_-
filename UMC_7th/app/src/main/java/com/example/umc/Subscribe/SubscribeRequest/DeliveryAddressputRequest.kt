package com.example.umc.Subscribe.SubscribeRequest

data class DeliveryAddressputRequest(
    val addressId: String?,
    val name: String,
    val address: String,
    val postNum: Int,
    val phoneNum: String,
    val memo: String
)
