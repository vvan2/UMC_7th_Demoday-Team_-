package com.example.umc.UserApi

import android.content.Context

object SharedPreferencesManager {

    private const val PREF_NAME = "UserPreferences"
    private const val KEY_USER_ID = "userId"
    private const val KEY_USER_NAME = "userName"
    private const val KEY_EMAIL = "userEmail"
    private const val KEY_BIRTH = "birth"
    private const val KEY_LOGIN_METHOD = "loginMethod"
    private const val KEY_PHONE_NUMBER = "userPhoneNumber"
    private const val KEY_PROFILE_IMAGE = "userProfileImage"  // ✅ 프로필 이미지 저장 추가
    private const val KEY_ACCESS_TOKEN = "accessToken"  // ✅ 액세스 토큰 저장 추가
    private const val KEY_REFRESH_TOKEN = "refreshToken"  // ✅ 리프레시 토큰 저장 추가

    fun saveUserData(
        context: Context,
        userId: Int,
        accessToken: String,
        refreshToken: String? = null,
        email: String? = null
    ) {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        sharedPreferences.edit().apply {
            putInt(KEY_USER_ID, userId)
            putString(KEY_ACCESS_TOKEN, accessToken)
            refreshToken?.let { putString(KEY_REFRESH_TOKEN, it) }
            email?.let { putString(KEY_EMAIL, it) }
            apply()
        }
    }

    // ✅ SharedPreferences에 userId 저장
    fun saveUserId(context: Context, userId: Int) {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        sharedPreferences.edit().putInt(KEY_USER_ID, userId).apply()
    }

    fun getUserId(context: Context): Int {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getInt(KEY_USER_ID, -1)
    }

    // ✅ SharedPreferences에 name 저장
    fun saveUserName(context: Context, userName: String) {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        sharedPreferences.edit().putString(KEY_USER_NAME, userName).apply()
    }

    fun getUserName(context: Context): String? {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getString(KEY_USER_NAME, null)
    }

    // ✅ SharedPreferences에 email 저장
    fun saveUserEmail(context: Context, email: String) {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        sharedPreferences.edit().putString(KEY_EMAIL, email).apply()
    }

    fun getUserEmail(context: Context): String? {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getString(KEY_EMAIL, null)
    }

    // ✅ SharedPreferences에 phoneNumber 저장
    fun saveUserPhoneNumber(context: Context, phoneNumber: String) {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        sharedPreferences.edit().putString(KEY_PHONE_NUMBER, phoneNumber).apply()
    }

    fun getUserPhoneNumber(context: Context): String? {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getString(KEY_PHONE_NUMBER, null)
    }

    // ✅ SharedPreferences에 프로필 이미지 저장
    fun saveUserProfileImage(context: Context, profileImage: String) {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        sharedPreferences.edit().putString(KEY_PROFILE_IMAGE, profileImage).apply()
    }

    fun getUserProfileImage(context: Context): String? {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getString(KEY_PROFILE_IMAGE, null)
    }

    // ✅ SharedPreferences에 액세스 토큰 저장
    fun saveAccessToken(context: Context, accessToken: String) {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        sharedPreferences.edit().putString(KEY_ACCESS_TOKEN, accessToken).apply()
    }

    fun getAccessToken(context: Context): String? {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getString(KEY_ACCESS_TOKEN, null)
    }

    // ✅ SharedPreferences에 리프레시 토큰 저장
    fun saveRefreshToken(context: Context, refreshToken: String) {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        sharedPreferences.edit().putString(KEY_REFRESH_TOKEN, refreshToken).apply()
    }

    fun getRefreshToken(context: Context): String? {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getString(KEY_REFRESH_TOKEN, null)
    }

    // ✅ SharedPreferences에서 모든 데이터 삭제 (로그아웃 시 사용 가능)
    fun clearUserData(context: Context) {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        sharedPreferences.edit().clear().apply()
    }

}


