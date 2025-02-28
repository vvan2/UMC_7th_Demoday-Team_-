package com.example.umc.UserApi.Viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.umc.UserApi.Response.LoginResponse
import com.example.umc.UserApi.UserRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

//requestbody+viewmodel을 통해 일괄관리

class LoginViewModel(private val repository: UserRepository) : ViewModel() {

    private val _loginResult = MutableLiveData<LoginResponse>()
    val loginResult: LiveData<LoginResponse> get() = _loginResult

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    fun login(email: String, password: String) {
        repository.login(email, password).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    _loginResult.value = response.body()
                } else {
                    _error.value = "로그인 실패: ${response.errorBody()?.string()}"
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                _error.value = "네트워크 오류: ${t.message}"
            }
        })
    }
}
