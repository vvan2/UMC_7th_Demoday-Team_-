package com.example.umc.Subscribe.SubscribeRequest

data class DeliveryAddressRequest(
    val name: String,
    val address: String,
    val postNum: Int,
    val phoneNum : String,
    val memo: String
)
