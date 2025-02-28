package com.example.umc.model.repository

sealed class Result<out T> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val exception: Exception? = null, val errorMessage: String? = exception?.localizedMessage) : Result<Nothing>()
}
