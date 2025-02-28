package com.example.umc.UserApi.Response

data class LoginResponse(
    val success: SuccessResponse?,   // 로그인 성공 시의 데이터
    val error: ErrorResponse?        // 로그인 실패 시의 에러
)

data class SuccessResponse(
    val user: User,                // 로그인한 사용자 정보
    val accessToken: String?,      // accessToken
    val refreshToken: String?      // refreshToken
)

data class User(
    val userId: Int,               // 사용자 ID
    val email: String,             // 사용자 이메일
    val password: String,          // 사용자 비밀번호
    val birth: String,             // 생일
    val name: String,              // 사용자 이름
    val nickname: String?,         // 사용자 닉네임
    val phoneNum: String,          // 전화번호
    val purpose: String            // 사용 목적
)

data class ErrorResponse(
    val errorCode: String,         // 에러 코드
    val reason: String,            // 에러 발생 이유
    val data: String,              // 추가 데이터
    val fileName: String,          // 파일명
    val lineNumber: Int            // 오류 발생 위치
)