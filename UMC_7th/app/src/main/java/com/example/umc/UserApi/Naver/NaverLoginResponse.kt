package com.example.umc.UserApi.Response

data class NaverLoginResponse(
    val resultType: String,
    val error: String?,
    val success: SuccessNaverData
)

data class SuccessNaverData(
    val user: UserNaverData,
    val accessToken: String,
    val refreshToken: String
)

data class UserNaverData(
    val userId: Int,
    val email: String,
    val name: String,
    val nickname: String?,
    val birth: String?,
    val phoneNum: String?,
    val purpose: String?,
    val profileImage: String?,
    val loginMethod: String
)
