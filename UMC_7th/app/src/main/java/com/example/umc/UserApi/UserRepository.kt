package com.example.umc.UserApi

import android.content.Context
import android.net.http.HttpException
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresExtension
import com.example.umc.UserApi.APi.OtpApi
import com.example.umc.UserApi.Request.LoginRequest
import com.example.umc.UserApi.Request.OtpRequest
import com.example.umc.UserApi.Request.OtpValidationRequest
import com.example.umc.UserApi.Request.SignUpRequest
import com.example.umc.UserApi.Request.UpdateUserNameRequest
import com.example.umc.UserApi.Request.UpdateUserRequest
import com.example.umc.UserApi.Response.DiagnosisResponse
import com.example.umc.UserApi.Response.HealthScoreData
import com.example.umc.UserApi.Response.HealthScoreResponse
import com.example.umc.UserApi.Response.LoginResponse
import com.example.umc.UserApi.Response.MypageGoalResponse
import com.example.umc.UserApi.Response.NaverLoginResponse
import com.example.umc.UserApi.Response.OtpResponse
import com.example.umc.UserApi.Response.OtpValidationResponse
import com.example.umc.UserApi.Response.SignUpResponse
import com.example.umc.UserApi.Response.UpdateNicknameResponse
import com.example.umc.UserApi.Response.UserProfileResponse
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.awaitResponse
import java.io.File

class UserRepository {
    private val api = RetrofitClient.instance
    private val getUserApi = RetrofitClient.getApiService
    private val otpApi = RetrofitClient.otpApi
    private val mypageGoalApi = RetrofitClient.mypageGoalApi
    private val imageProfileApi = RetrofitClient.imageProfileApi

    // SharedPreferences에 accessToken 저장
    companion object {
        private const val PREF_NAME = "UserPreferences"
        private const val KEY_ACCESS_TOKEN = "ACCESS_TOKEN"

        fun saveAuthToken(context: Context, token: String) {
            val sharedPreferences = context.getSharedPreferences("UserPreferences", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putString("auth_token", token)
            editor.apply()
        }

        fun getAuthToken(context: Context): String {
            val sharedPreferences = context.getSharedPreferences("UserPreferences", Context.MODE_PRIVATE)
            val token = sharedPreferences.getString("auth_token", "") ?: ""
            return token
        }
    }


    // 회원가입 처리 로직 수정
    // 회원가입 처리 로직
    fun signUp(request: SignUpRequest, callback: (Boolean, String) -> Unit) {
        Log.d("UserRepository", "회원가입 요청 보냄: $request")  // ✅ 요청 로그 추가

        api.signUp(request).enqueue(object : Callback<SignUpResponse> {
            override fun onResponse(call: Call<SignUpResponse>, response: Response<SignUpResponse>) {
                if (response.isSuccessful) {
                    val result = response.body()
                    Log.d("UserRepository", "회원가입 성공 응답: ${response.body()}")  // ✅ 응답 로그 추가

                    // ✅ success 내부에서 user 정보 확인
                    if (result?.success?.user != null) {
                        callback(true, "회원가입 성공")
                    } else {
                        Log.e("UserRepository", "회원가입 실패: user 필드가 null")
                        callback(false, "회원가입 실패: user 필드가 null")
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("UserRepository", "회원가입 실패: HTTP ${response.code()}, 오류: $errorBody")
                    callback(false, "서버 오류: $errorBody")
                }
            }

            override fun onFailure(call: Call<SignUpResponse>, t: Throwable) {
                Log.e("UserRepository", "네트워크 오류 발생: ${t.message}", t)  // ✅ 네트워크 오류 로그 추가
                callback(false, "네트워크 오류: ${t.message}")
            }
        })
    }



    // 로그인 요청을 Call<LoginResponse>로 반환 (callback 제거)
    fun login(email: String, password: String): Call<LoginResponse> {
        return api.login(LoginRequest(email, password))
    }


    suspend fun getUserProfile(context: Context): UserProfileResponse? {
        val accessToken = getAuthToken(context)
        if (accessToken.isNullOrEmpty()) {
            Log.e("UserRepository", "액세스 토큰이 없습니다.")
            return null
        }

        Log.d("UserRepository", "현재 전달된 토큰: $accessToken")

        return try {
            Log.d("UserRepository", "요청 헤더: Authorization = Bearer $accessToken")
            val response = getUserApi.getUserProfile("Bearer $accessToken")
            if (response.isSuccessful && response.body() != null) {
                response.body()
            } else {
                Log.e("UserRepository", "서버 응답 실패: ${response.code()}")
                Log.e("UserRepository", "에러 메시지: ${response.errorBody()?.string()}")
                null
            }
        } catch (e: Exception) {
            Log.e("UserRepository", "네트워크 오류 발생: ${e.message}")
            null
        }
    }

    suspend fun updateUserProfile(context: Context, updatedProfile: UpdateUserRequest): Boolean {
        val token = getAuthToken(context)
        if (token.isNullOrEmpty()) {
            Log.e("UserRepository", "액세스 토큰이 없습니다.")
            return false
        }

        return try {
            val response = RetrofitClient.updateUserApi.updateUserProfile("Bearer $token", updatedProfile)
            if (response.isSuccessful && response.body()?.user != null) {
                Log.d("UserRepository", "프로필 업데이트 성공: ${response.body()?.user}")
                true
            } else {
                Log.e("UserRepository", "프로필 업데이트 실패: ${response.errorBody()?.string()}")
                false
            }
        } catch (e: Exception) {
            Log.e("UserRepository", "네트워크 오류: ${e.message}")
            false
        }
    }

    suspend fun requestOtp(phoneNumber: String): Result<OtpResponse> {
        return try {
            Log.d("UserRepository", "OTP 요청 시작: $phoneNumber") // 요청 시작 로그

            val request = OtpRequest(phoneNumber)
            val response = otpApi.requestOtp(request)

            Log.d("UserRepository", "서버 응답 코드: ${response.code()}") // 응답 코드 로그

            if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it)
                } ?: run {
                    Log.e("UserRepository", "응답이 비어 있습니다.") // 응답 바디 없음 로그
                    Result.failure(Exception("응답이 비어있습니다."))
                }
            } else {
                val errorMessage = response.errorBody()?.string()
                Log.e("UserRepository", "서버 오류: ${response.code()} - $errorMessage") // 서버 오류 로그
                Result.failure(Exception("서버 오류: ${response.code()} - $errorMessage"))
            }
        } catch (e: Exception) {
            Log.e("UserRepository", "OTP 요청 실패: ${e.message}") // 예외 발생 로그
            Result.failure(Exception("네트워크 오류: ${e.message}"))
        }
    }


    suspend fun validateOtp(phoneNumber: String, code: String): Result<OtpValidationResponse> {
        return try {
            val request = OtpValidationRequest(phoneNumber, code)
            val response: OtpValidationResponse = RetrofitClient.otpValidationApi.validateOtp(request) // 반환 타입 확인
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 건강 점수 확인 로직
    suspend fun getHealthScore(context: Context): HealthScoreData? {
        val token = getAuthToken(context)
        if (token.isNullOrEmpty()) {
            Log.e("UserRepository", "🚨 액세스 토큰이 없습니다.")
            return null
        }

        return try {
            Log.d("UserRepository", "📢 건강 점수 API 요청: Bearer $token")

            val response = RetrofitClient.healthScoreApi.getHealthScore("Bearer $token")

            if (response.isSuccessful) {
                val body = response.body()
                Log.d("UserRepository", "✅ 서버 응답 성공: $body")

                body?.success?.let {
                    Log.d("UserRepository", "📊 건강 점수 데이터: healthScore=${it.healthScore}, comparison=${it.comparison}, updatedAt=${it.updateAt}")
                    return it
                } ?: run {
                    Log.e("UserRepository", "❌ success 필드가 null입니다.")
                    return null
                }
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e("UserRepository", "❌ 서버 응답 실패: HTTP ${response.code()}")
                Log.e("UserRepository", "🚨 에러 메시지: $errorBody")
                return null
            }
        } catch (e: Exception) {
            Log.e("UserRepository", "⚠️ 네트워크 오류 발생: ${e.localizedMessage}", e)
            return null
        }
    }






    suspend fun getDiagnosisResult(context: Context): DiagnosisResponse? {
        val token = getAuthToken(context)
        if (token.isNullOrEmpty()) {
            Log.e("UserRepository", "액세스 토큰이 없습니다.")
            return null
        }

        Log.d("UserRepository", "토큰 전달 확인: Bearer $token")

        return try {
            Log.d("UserRepository", "진단 API 호출 시작")
            val response = RetrofitClient.diagnosisApi.getDiagnosisResult("Bearer $token")

            if (response.isSuccessful) {
                Log.d("UserRepository", "서버 응답 성공: ${response.body()}")
                response.body()
            } else {
                Log.e("UserRepository", "서버 응답 실패: ${response.code()}")
                Log.e("UserRepository", "에러 메시지: ${response.errorBody()?.string()}")
                null
            }
        } catch (e: Exception) {
            Log.e("UserRepository", "네트워크 오류 발생: ${e.message}", e)
            null
        }
    }


    suspend fun getMypageGoal(context: Context): MypageGoalResponse? {
        val token = getAuthToken(context)
        if (token.isNullOrEmpty()) {
            Log.e("UserRepository", "액세스 토큰이 없습니다.")
            return null
        }

        return try {
            // API 요청
            val response = mypageGoalApi.getMypageGoal("Bearer $token").execute() // 동기 호출로 변경

            // 응답 처리
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    body // 응답 본문 반환
                } else {
                    // 응답 본문이 null일 경우 처리
                    Log.e("UserRepository", "응답 본문이 null입니다.")
                    null
                }
            } else {
                // 실패시 로깅
                Log.e("UserRepository", "서버 응답 실패: ${response.code()}")
                Log.e("UserRepository", "에러 메시지: ${response.errorBody()?.string()}")
                null
            }
        } catch (e: Exception) {
            // 네트워크 오류 처리
            Log.e("UserRepository", "네트워크 오류 발생: ${e.message}")
            null
        }
    }

    // 이미지 업로드 메서드
    fun updateProfileImage(context: Context, imagePart: MultipartBody.Part, callback: (Boolean, String?) -> Unit) {
        val token = getAuthToken(context)
        if (token.isNullOrEmpty()) {
            Log.e("UserRepository", "액세스 토큰이 없습니다.")
            callback(false, "액세스 토큰이 없습니다.")
            return
        }

        Log.d("UserRepository", "토큰 전달 확인: Bearer $token") // ✅ 디버깅 로그 추가

        imageProfileApi.updateProfileImage("Bearer $token", imagePart).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    callback(true, "프로필 이미지 업데이트 성공")
                } else {
                    Log.e("UserRepository", "프로필 이미지 업데이트 실패: ${response.errorBody()?.string()}")
                    callback(false, "서버 오류: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("UserRepository", "프로필 이미지 업로드 네트워크 오류: ${t.message}")
                callback(false, "네트워크 오류: ${t.message}")
            }
        })
    }


    // UserRepository
    fun updateUserName(userId: Int, newName: String, callback: (Boolean, String) -> Unit) {
        val request = UpdateUserNameRequest(userId, newName)

        api.updateUserName(request).enqueue(object : Callback<UpdateNicknameResponse> {
            override fun onResponse(call: Call<UpdateNicknameResponse>, response: Response<UpdateNicknameResponse>) {
                if (response.isSuccessful) {
                    val result = response.body()
                    if (result != null) {
                        callback(true, "이름 수정 성공")
                    } else {
                        callback(false, "응답 없음")
                    }
                } else {
                    callback(false, "서버 오류: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<UpdateNicknameResponse>, t: Throwable) {
                callback(false, "네트워크 오류: ${t.message}")
            }
        })
    }

    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
    suspend fun loginWithNaver(): NaverLoginResponse? {
        return withContext(Dispatchers.IO) { // 네트워크 호출은 IO 스레드에서 실행
            try {
                val response = RetrofitClient.naverLoginApi.loginWithNaver().execute() // 동기 호출
                if (response.isSuccessful) {
                    val responseBody = response.body()?.string()
                    Log.d("NAVER_LOGIN", "네이버 로그인 응답: $responseBody")

                    if (!responseBody.isNullOrBlank() && responseBody.startsWith("{")) {
                        val gson = Gson()
                        try {
                            val loginResponse = gson.fromJson(responseBody, NaverLoginResponse::class.java)
                            Log.d("NAVER_LOGIN", "네이버 로그인 성공: ${loginResponse.success.accessToken}")
                            loginResponse
                        } catch (e: JsonSyntaxException) {
                            Log.e("NAVER_LOGIN", "JSON 파싱 오류: ${e.message}", e)
                            null
                        }
                    } else {
                        Log.e("NAVER_LOGIN", "서버에서 JSON이 아닌 응답을 반환했습니다: $responseBody")
                        null
                    }
                } else {
                    Log.e("NAVER_LOGIN", "네이버 로그인 실패: HTTP ${response.code()}, 오류: ${response.errorBody()?.string()}")
                    null
                }
            } catch (e: HttpException) {
                Log.e("NAVER_LOGIN", "HTTP 예외 발생: ${e.message}", e)
                null
            } catch (e: Exception) {
                Log.e("NAVER_LOGIN", "네트워크 오류 발생: ${e.message}", e)
                null
            }
        }
    }









}