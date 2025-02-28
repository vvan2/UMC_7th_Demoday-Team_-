package com.example.umc.UserApi.Response

data class SignUpResponse(
    val resultType: String,  // "SUCCESS" 또는 "FAIL"
    val error: String?,  // 에러 메시지
    val success: SuccessSignUpResponse? // 회원가입 성공 시 반환 데이터
)

data class SuccessSignUpResponse(
    val user: UserSignUpData,  // ✅ user 객체 포함
    val accessToken: String,
    val refreshToken: String
)

data class UserSignUpData(
    val userId: Int,
    val email: String,
    val password: String,
    val birth: String,
    val name: String,
    val nickname: String?,
    val phoneNum: String,
    val purpose: String,
    val profileImage: String?,
    val loginMethod: String
)
