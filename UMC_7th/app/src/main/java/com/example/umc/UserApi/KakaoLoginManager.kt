package com.example.umc.UserApi

import android.content.Context
import android.provider.Settings
import android.util.Log
import com.example.umc.UserApi.APi.KakaoAuthService
import com.example.umc.UserApi.Kakao.AuthResponse
import com.example.umc.UserApi.Response.LoginResult

class KakaoLoginManager(
    private val authService: KakaoAuthService,
    private val context: Context
) {
    companion object {
        private const val TAG = "KakaoLoginManager"  // 명시적 TAG 선언
        private const val KAKAO_NATIVE_APP_KEY = "3467c5d19149e86652d623f529cc95c1"
        private const val REDIRECT_URI = "http://3.38.39.238:3000/auth/kakao/callback"
    }

    // 카카오 로그인 인증 페이지 URL 생성
    fun getKakaoAuthUrl(): String {
        return "https://kauth.kakao.com/oauth/authorize" +
                "?client_id=$KAKAO_NATIVE_APP_KEY" +
                "&redirect_uri=$REDIRECT_URI" +
                "&response_type=code"
    }

    // 디바이스 ID를 안전하게 가져오는 메서드
    private fun getDeviceId(): String {
        return Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ANDROID_ID
        ) ?: "unknown_device"  // 널 대비 기본값 제공
    }

    // 카카오 로그인 프로세스 처리
// 이렇게 수정해야 합니다
    suspend fun handleKakaoLogin(authCode: String): LoginResult<AuthResponse> {
        return try {
            // 1. 인증 코드 유효성 검사
            require(authCode.isNotBlank()) { "유효하지 않은 인증 코드" }

            // 2. 디버깅을 위한 상세 로깅
            Log.d(TAG, "Kakao Login Request - Code Length: ${authCode.length}")

            // 3. 네트워크 요청 처리
            val deviceId = getDeviceId()
            val result = authService.loginWithKakao(
                code = authCode,
                device = deviceId
            )

            // 4. 응답 상세 분석
            when {
                result.isSuccessful -> {
                    result.body()?.let { authResponse ->
                        val userId = authResponse.success?.user?.userId
                            ?: authResponse.success?.user?.id

                        Log.d(TAG, "Login Success - User ID: $userId")

                        // 토큰 저장
                        saveAuthTokens(authResponse)

                        LoginResult.Success(authResponse)
                    } ?: LoginResult.Error(Exception("Empty response body"))
                }
                else -> {
                    val errorBody = result.errorBody()?.string() ?: "Unknown Error"
                    Log.e(TAG, "Login Error - Status: ${result.code()}, Body: $errorBody")
                    LoginResult.Error(Exception("로그인 실패: ${result.code()}"))
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected Kakao Login Error", e)
            LoginResult.Error(e)
        }
    }

    // 인증 토큰 안전하게 저장
    private fun saveAuthTokens(authResponse: AuthResponse) {
        val prefs = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
        with(prefs.edit()) {
            putString("access_token", authResponse.accessToken)
            putString("refresh_token", authResponse.refreshToken)
            apply()
        }
    }
}