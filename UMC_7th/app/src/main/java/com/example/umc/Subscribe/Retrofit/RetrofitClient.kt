package com.example.umc.Subscribe.Retrofit

import android.content.Context
import com.example.umc.Subscribe.SubcribeApi.DeliveryAddressApi
import com.example.umc.Subscribe.SubscribeApi.MealApi
import com.example.umc.UserApi.AuthInterceptor
import com.example.umc.model.service.MealApiService
import com.example.umc.UserApi.UserRepository
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    // BASE_URL을 여기에 추가해줍니다
    private const val BASE_URL = "http://3.38.39.238:3000"

    // RetrofitClient 초기화 시 context를 받아오도록 수정
    fun getRetrofitInstance(context: Context): Retrofit {
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(context))  // AuthInterceptor에 context 전달
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit
    }

    // MealApiService는 context 없이 호출할 수 있도록 별도로 처리
    fun mealApiService(): MealApiService {
        // Context 없이 사용할 수 있도록 getRetrofitInstanceWithoutContext 사용
        return getRetrofitInstanceWithoutContext().create(MealApiService::class.java)
    }

    // DeliveryAddressApi는 context를 필요로 하므로 context 전달
    fun getDeliveryAddressApi(context: Context): DeliveryAddressApi {
        return getRetrofitInstance(context).create(DeliveryAddressApi::class.java)
    }

    // context 없이 Retrofit 인스턴스를 생성하는 메서드
    private fun getRetrofitInstanceWithoutContext(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    fun getMealApi(context: Context): MealApi {
        return getRetrofitInstance(context).create(MealApi::class.java)
    }
}
