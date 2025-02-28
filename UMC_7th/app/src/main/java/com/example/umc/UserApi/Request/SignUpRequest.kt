package com.example.umc.UserApi.Request

//회원가입 requset body
data class SignUpRequest(
    val email: String,
    val password: String,
    val birth: String,
    val name: String,
    val phoneNum: String,
    val purpose: String
)