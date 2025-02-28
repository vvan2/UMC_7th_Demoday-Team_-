package com.example.umc.Survey

import android.content.Context
import android.util.Log

object UserRepository {
    private const val PREF_NAME = "user_prefs"
    private const val KEY_AUTH_TOKEN = "auth_token"

    fun saveAuthToken(context: Context, token: String) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_AUTH_TOKEN, token).apply()
        Log.d("UserRepository", "✅ 토큰 저장 완료: $token")  // 🔥 토큰 저장 확인 로그 추가
    }

    fun getAuthToken(context: Context): String? {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val token = prefs.getString(KEY_AUTH_TOKEN, null)
        Log.d("UserRepository", "📢 저장된 토큰 가져오기: $token")  // 🔥 토큰 가져오기 확인 로그 추가
        return token
    }
}

