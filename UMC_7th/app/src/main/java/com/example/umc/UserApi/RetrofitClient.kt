package com.example.umc.UserApi

import ImageProfileApi
import android.content.Context
import com.example.umc.Subscribe.SubcribeApi.SubMealService
import com.example.umc.UserApi.APi.DiagnosisApi
import com.example.umc.UserApi.APi.GetUserApi
import com.example.umc.UserApi.APi.HealthScoreApi

import com.example.umc.UserApi.APi.KakaoLoginApi
import com.example.umc.UserApi.APi.MypageGoalApi
import com.example.umc.UserApi.APi.NaverLoginApi
import com.example.umc.UserApi.APi.OtpApi
import com.example.umc.UserApi.APi.OtpValidationApi
import com.example.umc.UserApi.APi.UpdateUserApi
import com.example.umc.UserApi.APi.UserApi
import com.example.umc.model.service.ImageApiService
import com.example.umc.model.service.KamisApiService
import com.example.umc.model.service.MealApiService
import com.example.umc.model.service.MaterialApiService
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

import com.google.gson.GsonBuilder

// swagger 연결 http

//object RetrofitClient {
//    private const val BASE_URL = "http://3.38.39.238:3000/"  // 여기에 API 서버 주소 입력
//    private lateinit var okHttpClient: OkHttpClient
//
//    private val gson = GsonBuilder()
//        .setLenient()
//        .create()
//
//    // Retrofit 빌더 함수로 중복 제거
//    private fun createRetrofit(): Retrofit {
//        return Retrofit.Builder()
//            .baseUrl(BASE_URL)
//            .addConverterFactory(GsonConverterFactory.create(gson)) // 수정된 Gson 사용
//            .client(okHttpClient)
//            .build()
//    }
//
//    // ✅ SharedPreferences 초기화 (Application에서 호출 필요)
//    fun init(context: Context) {
//        okHttpClient = OkHttpClient.Builder()
//            .addInterceptor(AuthInterceptor(context)) // 인증 헤더 추가
//            .connectTimeout(30, TimeUnit.SECONDS) // 연결 타임아웃 설정
//            .readTimeout(30, TimeUnit.SECONDS) // 읽기 타임아웃 설정
//            .writeTimeout(30, TimeUnit.SECONDS) // 쓰기 타임아웃 설정
//            .build()
//    }
//
//
//    val instance: UserApi by lazy {
//        Retrofit.Builder()
//            .baseUrl(BASE_URL)
//            .addConverterFactory(GsonConverterFactory.create()) // JSON 변환
//            .client(okHttpClient)
//            .build()
//            .create(UserApi::class.java)
//    }
//
//    val mealApiService: MealApiService by lazy {
//        Retrofit.Builder()
//            .baseUrl(BASE_URL)
//            .addConverterFactory(GsonConverterFactory.create())
//            .client(okHttpClient)
//            .build()
//            .create(MealApiService::class.java)ㅋ
//    }
//
//    val getApiService: GetUserApi by lazy {
//        Retrofit.Builder()
//            .baseUrl(BASE_URL)
//            .addConverterFactory(GsonConverterFactory.create())
//            .client(okHttpClient)
//            .build()
//            .create(GetUserApi::class.java)
//    }
//
//    val updateUserApi: UpdateUserApi by lazy {
//        Retrofit.Builder()
//            .baseUrl(BASE_URL)
//            .addConverterFactory(GsonConverterFactory.create())
//            .client(okHttpClient)
//            .build()
//            .create(UpdateUserApi::class.java)
//    }
//
//    val imageApiService: ImageApiService by lazy {
//        Retrofit.Builder()
//            .baseUrl(BASE_URL)
//            .addConverterFactory(GsonConverterFactory.create())
//            .client(okHttpClient)
//            .build()
//            .create(ImageApiService::class.java)
//    }
//
//    val otpApi: OtpApi by lazy {
//        Retrofit.Builder()
//            .baseUrl(BASE_URL)
//            .addConverterFactory(GsonConverterFactory.create())
//            .client(okHttpClient)
//            .build()
//            .create(OtpApi::class.java)
//    }
//
//    val otpValidationApi: OtpValidationApi by lazy {
//        Retrofit.Builder()
//            .baseUrl(BASE_URL)
//            .addConverterFactory(GsonConverterFactory.create())
//            .client(okHttpClient)
//            .build()
//            .create(OtpValidationApi::class.java)
//    }
//
//    val kakaoLoginApi: KakaoLoginApi by lazy {
//        Retrofit.Builder()
//            .baseUrl(BASE_URL)
//            .addConverterFactory(GsonConverterFactory.create())
//            .client(okHttpClient)
//            .build()
//            .create(KakaoLoginApi::class.java)
//    }
//
//    // 건강 점수 확인 로직
//    val healthScoreApi: HealthScoreApi by lazy {
//        Retrofit.Builder()
//            .baseUrl(BASE_URL)
//            .addConverterFactory(GsonConverterFactory.create())
//            .client(okHttpClient)
//            .build()
//            .create(HealthScoreApi::class.java)
//    }
//    val diagnosisApi: DiagnosisApi by lazy {
//        Retrofit.Builder()
//            .baseUrl(BASE_URL)
//            .addConverterFactory(GsonConverterFactory.create())
//            .client(okHttpClient)
//            .build()
//            .create(DiagnosisApi::class.java)
//    }
//
//    val mypageGoalApi: MypageGoalApi by lazy {
//        Retrofit.Builder()
//            .baseUrl(BASE_URL)
//            .addConverterFactory(GsonConverterFactory.create())
//            .client(okHttpClient)
//            .build()
//            .create(MypageGoalApi::class.java)
//    }
//    val imageProfileApi: ImageProfileApi by lazy {
//        Retrofit.Builder()
//            .baseUrl(BASE_URL)
//            .addConverterFactory(GsonConverterFactory.create()) // JSON 변환
//            .client(okHttpClient)
//            .build()
//            .create(ImageProfileApi::class.java)
//    }
//
//    val subMealService: SubMealService by lazy {
//        Retrofit.Builder()
//            .baseUrl(BASE_URL)
//            .addConverterFactory(GsonConverterFactory.create())
//            .build()
//            .create(SubMealService::class.java)
//    }
//
//    val naverLoginApi: NaverLoginApi by lazy {
//        createRetrofit().create(NaverLoginApi::class.java)
//    }
//
//}

object RetrofitClient {

    private const val KAMIS_URL = "http://www.kamis.or.kr/"
    private const val BASE_URL = "http://3.38.39.238:3000/"
    private lateinit var okHttpClient: OkHttpClient

    // ✅ Gson 인스턴스 생성 - lenient 모드 활성화
    private val gson = GsonBuilder()
        .create()

    fun init(context: Context) {
        okHttpClient = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(context))
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    // Retrofit 빌더 함수로 중복 제거
    private fun createRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson)) // 수정된 Gson 사용
            .client(okHttpClient)
            .build()
    }

    val instance: UserApi by lazy {
        createRetrofit().create(UserApi::class.java)
    }

    val mealApiService: MealApiService by lazy {
        createRetrofit().create(MealApiService::class.java)
    }

    val getApiService: GetUserApi by lazy {
        createRetrofit().create(GetUserApi::class.java)
    }

    val updateUserApi: UpdateUserApi by lazy {
        createRetrofit().create(UpdateUserApi::class.java)
    }

    val imageApiService: ImageApiService by lazy {
        createRetrofit().create(ImageApiService::class.java)
    }

    val otpApi: OtpApi by lazy {
        createRetrofit().create(OtpApi::class.java)
    }

    val otpValidationApi: OtpValidationApi by lazy {
        createRetrofit().create(OtpValidationApi::class.java)
    }

    val kakaoLoginApi: KakaoLoginApi by lazy {
        createRetrofit().create(KakaoLoginApi::class.java)
    }

    val healthScoreApi: HealthScoreApi by lazy {
        createRetrofit().create(HealthScoreApi::class.java)
    }

    val diagnosisApi: DiagnosisApi by lazy {
        createRetrofit().create(DiagnosisApi::class.java)
    }

    val mypageGoalApi: MypageGoalApi by lazy {
        createRetrofit().create(MypageGoalApi::class.java)
    }

    val imageProfileApi: ImageProfileApi by lazy {
        createRetrofit().create(ImageProfileApi::class.java)
    }

    val subMealService: SubMealService by lazy {
        createRetrofit().create(SubMealService::class.java)
    }

    val naverLoginApi: NaverLoginApi by lazy {
        createRetrofit().create(NaverLoginApi::class.java)
    }

    val materialApiService: MaterialApiService by lazy {
        createRetrofit().create(MaterialApiService::class.java)
    }

    val kamisService: KamisApiService by lazy {
        Retrofit.Builder()
            .baseUrl(KAMIS_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(RetrofitClient.okHttpClient)
            .build()
            .create(KamisApiService::class.java)
    }
}