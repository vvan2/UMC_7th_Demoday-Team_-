package com.example.umc.model

import android.app.Application
import android.util.Log
import com.example.umc.UserApi.RetrofitClient

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        RetrofitClient.init(this)
        Log.d("RetrofitClient", "RetrofitClient 초기화 완료")
    }
}

