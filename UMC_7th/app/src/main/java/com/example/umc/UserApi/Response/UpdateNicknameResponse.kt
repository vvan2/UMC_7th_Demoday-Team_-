package com.example.umc.UserApi.Response

data class UpdateNicknameResponse(
    val user: UserNickname  // 수정된 유저 정보
)

data class UserNickname(
    val userId: Int,
    val email: String,
    val password: String,
    val birth: String,
    val name: String,  // 수정된 이름
    val phoneNum: String,
    val purpose: String
)
