package com.example.umc.Subscribe.Repository

import android.content.Context
import android.util.Log
import com.example.umc.Subscribe.Retrofit.RetrofitClient
import com.example.umc.Subscribe.SubscribeRequest.DefaultDeliveryAddressRequest
import com.example.umc.Subscribe.SubscribeRequest.DeliveryAddressRequest
import com.example.umc.Subscribe.SubscribeRequest.DeliveryAddressputRequest
import com.example.umc.Subscribe.SubscribeResponse.Get.CalendarResponse
import com.example.umc.Subscribe.SubscribeResponse.Get.DeliveryAddressGetresponse
import com.example.umc.Subscribe.SubscribeResponse.Get.DeliveryGetResponse
import com.example.umc.Subscribe.SubscribeResponse.Patch.DeliveryAddressPatchresponse
import com.example.umc.Subscribe.SubscribeResponse.Post.DeliveryAddressresponse
import com.example.umc.Subscribe.SubscribeResponse.Post.SuccessAddressResponse

import com.example.umc.UserApi.UserRepository
import retrofit2.Response
import com.example.umc.Subscribe.SubscribeResponse.Get.SuccessDeliveryResponse as SuccessDeliveryResponse1

class DeliveryAddressRepository(private val context: Context) {
    // api를 생성자에서 전달된 context를 통해 초기화
    private val api = RetrofitClient.getDeliveryAddressApi(context)

    // GET 관련 기능
    object Get {
        suspend fun getCalendarDates(context: Context): Result<CalendarResponse> {
            val token = UserRepository.getAuthToken(context) ?: return Result.failure(Exception("인증 토큰이 없습니다."))

            return try {
                val response = RetrofitClient.getDeliveryAddressApi(context)
                    .getCalendar("Bearer $token") // API 요청: 캘린더 날짜
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!) // 성공적으로 응답 받음
                } else {
                    Result.failure(Exception("캘린더 정보 조회 실패: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
        suspend fun getDeliveryAddresses(context: Context): Result<DeliveryAddressGetresponse> {
            val token = UserRepository.getAuthToken(context)
            if (token.isNullOrEmpty()) {
                return Result.failure(Exception("인증 토큰이 없습니다."))
            }

            return try {
                val response = RetrofitClient.getDeliveryAddressApi(context)
                    .getDeliveryAddresses("Bearer $token")
                if (response.isSuccessful && response.body() != null) {
                    val responseBody = response.body()!! // 원본 응답 (List<DeliveryGetResponse>)

                    // 변환해서 DeliveryAddressGetresponse 형식으로 맞추기
                    val convertedResponse = DeliveryAddressGetresponse(
                        resultType = "SUCCESS",  // 성공 여부 지정
                        error = null,  // 실패가 없으면 null
                        success = SuccessDeliveryResponse1(result = responseBody) // 직접 responseBody를 전달
                    )

                    Result.success(convertedResponse)  // 🔹 DeliveryAddressGetresponse 객체 반환
                } else {
                    Result.failure(Exception("배송지 목록 조회 실패: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
        suspend fun getDefaultDeliveryAddress(context: Context): Result<DeliveryGetResponse> {
            val token = UserRepository.getAuthToken(context) ?: return Result.failure(Exception("인증 토큰이 없습니다."))

            return try {
                val response: Response<DeliveryAddressPatchresponse> =
                    RetrofitClient.getDeliveryAddressApi(context)
                        .getDefaultDeliveryAddress("Bearer $token")

                if (response.isSuccessful) {
                    val responseBody = response.body()

                    if (responseBody != null) {
                        val defaultAddress = responseBody.success?.deliveryAddress // ✅ 단일 객체로 접근

                        if (defaultAddress != null) {
                            AddressRepository.saveDefaultAddress(context, defaultAddress) // ✅ 기본 배송지 저장
                            Log.d("GET_ADDRESS_SUCCESS", "기본 배송지 정보 불러오기 성공: $defaultAddress")

                            return Result.success(defaultAddress)
                        }
                    }

                    Log.e("GET_ADDRESS_ERROR", "기본 배송지가 없습니다.")
                    return Result.failure(Exception("기본 배송지가 없습니다."))
                } else {
                    val errorBody = response.errorBody()?.string() ?: "No error body"
                    Log.e("GET_ADDRESS_ERROR", "기본 배송지 불러오기 실패: HTTP ${response.code()} - $errorBody")
                    return Result.failure(Exception("기본 배송지 불러오기 실패: ${response.code()}"))
                }
            } catch (e: Exception) {
                Log.e("GET_ADDRESS_ERROR", "네트워크 오류: ${e.message}")
                return Result.failure(e)
            }
        }



    }







    // POST 관련 기능
    object Post {
        suspend fun addDeliveryAddress(
            context: Context,
            request: DeliveryAddressRequest
        ): Result<DeliveryAddressresponse> {
            val token = UserRepository.getAuthToken(context)
            if (token.isNullOrEmpty()) {
                return Result.failure(Exception("인증 토큰이 없습니다."))
            }

            return try {
                val response = RetrofitClient.getDeliveryAddressApi(context).addDeliveryAddress("Bearer $token", request)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("배송지 추가 실패: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    object Put {
        suspend fun updateDeliveryAddress(
            context: Context,
            addressId: String?,
            request: DeliveryAddressputRequest
        ): Result<DeliveryAddressresponse> {
            val token = UserRepository.getAuthToken(context)
            if (token.isNullOrEmpty()) {
                return Result.failure(Exception("인증 토큰이 없습니다."))
            }

            return try {
                // addressId를 URL 경로에 포함시켜 PUT 요청을 보냄
                val response = RetrofitClient.getDeliveryAddressApi(context).updateDeliveryAddress(
                    "Bearer $token", addressId, request
                )
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("배송지 수정 실패: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    // ✅ 기본 배송지 설정 (PATCH)
    object Patch {
        suspend fun setDefaultDeliveryAddress(
            context: Context,
            addressId: Int
        ): Result<DeliveryAddressresponse> {
            val token = UserRepository.getAuthToken(context)

            if (token.isNullOrEmpty()) {
                Log.e("PATCH_ERROR", "인증 토큰이 없습니다.")
                return Result.failure(Exception("인증 토큰이 없습니다."))
            }

            val requestBody = DefaultDeliveryAddressRequest(addressId) // ✅ Body 객체 생성

            return try {
                val response: Response<DeliveryAddressresponse> =
                    RetrofitClient.getDeliveryAddressApi(context)
                        .setDefaultDeliveryAddress("Bearer $token", requestBody) // ✅ Body로 요청

                if (response.isSuccessful && response.body() != null) {
                    // ✅ 성공 시 SharedPreferences에 기본 배송지 저장
                    AddressRepository.saveAddressId(context, addressId.toString())
                    Log.d("PATCH_SUCCESS", "기본 배송지 설정 성공: ${response.body()}")
                    Result.success(response.body()!!)
                } else {
                    val errorBody = response.errorBody()?.string() ?: "No error body"
                    Log.e("PATCH_ERROR", "기본 배송지 설정 실패: HTTP ${response.code()} - $errorBody")
                    Result.failure(Exception("기본 배송지 설정 실패: ${response.code()}"))
                }
            } catch (e: Exception) {
                Log.e("PATCH_ERROR", "네트워크 오류: ${e.message}")
                Result.failure(e)
            }
        }
    }




}
