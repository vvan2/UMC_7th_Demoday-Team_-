package com.example.umc.Subscribe.SubscribeResponse.Get

data class DeliveryGetResponse(
    val addressId: Int,
    val userId: Int,
    val name: String,
    val address: String,
    val postNum: Int,
    val phoneNum: String,
    val memo: String,
    val isDefault: Boolean
)
