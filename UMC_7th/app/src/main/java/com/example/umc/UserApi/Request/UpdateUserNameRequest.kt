package com.example.umc.UserApi.Request

// 이름 수정 요청을 위한 데이터 클래스
data class UpdateUserNameRequest(
    val userId: Int,
    val name: String
)
