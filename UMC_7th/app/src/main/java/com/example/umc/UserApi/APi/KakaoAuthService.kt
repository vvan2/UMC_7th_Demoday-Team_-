package com.example.umc.UserApi.APi

import android.util.Log
import com.example.umc.UserApi.Kakao.AuthResponse
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

interface KakaoAuthService {
    @GET("auth/kakao/callback")
    suspend fun loginWithKakao(
        @Query("code") code: String,
        @Query("device") device: String
    ): Response<AuthResponse>

    companion object {
        // 네트워크 통신 설정을 강화하는 create 메서드
        fun create(): KakaoAuthService {
            // 로깅 인터셉터 생성 (네트워크 요청/응답 디버깅)
            val loggingInterceptor = HttpLoggingInterceptor { message ->
                Log.d("KakaoAuthNetwork", message)
            }.apply {
                level = HttpLoggingInterceptor.Level.BODY
            }

            // OkHttpClient 설정 - 타임아웃 및 로깅 인터셉터 추가
            val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)  // 네트워크 로깅
                .connectTimeout(30, TimeUnit.SECONDS)  // 연결 타임아웃 설정
                .readTimeout(30, TimeUnit.SECONDS)     // 읽기 타임아웃 설정
                .writeTimeout(30, TimeUnit.SECONDS)    // 쓰기 타임아웃 설정
                .build()

            // Retrofit 빌더 - 강화된 네트워크 클라이언트와 함께 생성
            return Retrofit.Builder()
                .baseUrl("http://3.38.39.238:3000/")  // 서버 베이스 URL
                .client(okHttpClient)                 // 강화된 OkHttpClient 적용
                .addConverterFactory(GsonConverterFactory.create())  // JSON 변환기
                .build()
                .create(KakaoAuthService::class.java)
        }
    }
}