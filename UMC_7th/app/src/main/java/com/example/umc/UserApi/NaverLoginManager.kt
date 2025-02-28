package com.example.umc.UserApi

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.example.umc.Main.MainActivity
import com.example.umc.R
import com.example.umc.UserApi.APi.NaverLoginApi
import com.example.umc.UserApi.Response.NaverLoginResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.awaitResponse


class NaverLoginManager(
    private val naverLoginApi: NaverLoginApi,
    private val context: Context,
    private val webView: WebView  // ✅ WebView를 전달받아 사용
) {

    private val CLIENT_ID: String by lazy {
        context.getString(R.string.naver_client_id)  // ✅ `secrets.xml`에서 값 가져오기
    }
    private val REDIRECT_URI: String by lazy {
        context.getString(R.string.naver_redirect_uri)
    }

    // 네이버 로그인 URL 생성
    fun getNaverAuthUrl(): String {
        val url = "https://nid.naver.com/oauth2.0/authorize" +
                "?client_id=$CLIENT_ID" +
                "&redirect_uri=$REDIRECT_URI" +
                "&response_type=code" +
                "&state=random_state"

        Log.d("NAVER_LOGIN", "생성된 네이버 로그인 URL: $url")
        return url
    }

    // 네이버 로그인 WebView 실행
    fun startNaverLogin() {
        val loginUrl = getNaverAuthUrl()

        webView.apply {
            visibility = View.VISIBLE
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            settings.useWideViewPort = true
            settings.loadWithOverviewMode = true

            webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                    Log.d("NAVER_LOGIN", "URL 로딩: $url")
                    if (url != null && url.startsWith(REDIRECT_URI)) {
                        handleRedirectUrl(url)
                        return true
                    }
                    return false
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    Log.d("NAVER_LOGIN", "페이지 로드 완료: $url")
                }
            }

            loadUrl(loginUrl)
        }
    }

    // ✅ 네이버 로그인 리디렉트 URL 처리
    private fun handleRedirectUrl(url: String) {
        val uri = Uri.parse(url)
        val authCode = uri.getQueryParameter("code")
        val state = uri.getQueryParameter("state")

        if (authCode != null) {
            webView.visibility = View.GONE
            Log.d("NAVER_LOGIN", "✅ 인증 코드 받음: $authCode")

            // ✅ `lifecycleScope`를 올바르게 가져오기
            val lifecycleOwner = (context as? LifecycleOwner) ?: return
            lifecycleOwner.lifecycleScope.launch {
                requestNaverAccessToken(authCode, state)
            }
        } else {
            Log.e("NAVER_LOGIN", "🚨 인증 코드 없음!")
            Toast.makeText(context, "네이버 로그인 실패!", Toast.LENGTH_SHORT).show()
        }
    }



    // 네이버 로그인 처리
    private suspend fun requestNaverAccessToken(authCode: String, state: String?) {
        withContext(Dispatchers.IO) {
            try {
                val response = naverLoginApi.getNaverAccessToken(authCode, state ?: "").awaitResponse()

                if (response.isSuccessful) {
                    response.body()?.let { responseBody ->
                        saveAuthTokens(responseBody)

                        // 🚀 네이버 로그인 성공 후 자동으로 MainActivity 이동
                        val intent = Intent(context, MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        context.startActivity(intent)

                        Log.d("NAVER_LOGIN", "로그인 성공: ${responseBody.success.accessToken}")
                    } ?: run {
                        Log.e("NAVER_LOGIN", "응답 본문이 비어 있음")
                    }
                } else {
                    Log.e("NAVER_LOGIN", "로그인 실패: ${response.code()} - ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("NAVER_LOGIN", "네트워크 오류 발생: ${e.message}", e)
            }
        }
    }


    // 네이버 로그인 토큰 저장
    private fun saveAuthTokens(response: NaverLoginResponse) {
        val prefs = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
        with(prefs.edit()) {
            putString("access_token", response.success.accessToken)
            apply()
        }
    }
}
