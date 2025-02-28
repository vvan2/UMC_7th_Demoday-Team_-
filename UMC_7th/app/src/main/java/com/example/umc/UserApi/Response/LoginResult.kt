package com.example.umc.UserApi.Response  // ✔ 패키지 이름 확인

sealed class LoginResult<out T> {
    data class Success<T>(val data: T) : LoginResult<T>()
    data class Error(
        val exception: Exception,
        val code: Int? = null  // code 프로퍼티 추가
    ) : LoginResult<Nothing>()
}
