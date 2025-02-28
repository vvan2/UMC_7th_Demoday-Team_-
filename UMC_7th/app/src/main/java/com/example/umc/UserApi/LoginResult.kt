package com.example.umc.UserApi

sealed interface LoginResult<out T> {
    // 성공했을 때의 데이터를 담는 클래스
    data class Success<T>(val data: T) : LoginResult<T>
    // 실패했을 때의 에러 정보를 담는 클래스
    data class Error(val exception: Exception, val code: Int? = null) : LoginResult<Nothing>
}