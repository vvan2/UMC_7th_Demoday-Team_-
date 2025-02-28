package com.example.umc.Signin
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.View
import android.content.Context
import android.net.Uri
import android.webkit.CookieManager
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import androidx.lifecycle.lifecycleScope
import com.example.umc.Main.MainActivity
import com.example.umc.R
import com.example.umc.SignUp.SignUpFragment
import com.example.umc.Survey.SurveyGoalFragment
import com.example.umc.UserApi.APi.KakaoAuthService
import com.example.umc.UserApi.Kakao.AuthResponse
import com.example.umc.UserApi.KakaoLoginManager
import com.example.umc.UserApi.NaverLoginManager
import com.example.umc.UserApi.Response.LoginResponse
import com.example.umc.UserApi.Response.LoginResult
import com.example.umc.UserApi.RetrofitClient
import com.example.umc.UserApi.RetrofitClient.naverLoginApi
import com.example.umc.UserApi.SharedPreferencesManager
import com.example.umc.UserApi.UserRepository
import com.example.umc.databinding.FragmentSigninBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Response
import retrofit2.awaitResponse




class LoginActivity : AppCompatActivity() {

    private lateinit var binding: FragmentSigninBinding
    private lateinit var naverLoginManager: NaverLoginManager
    private lateinit var kakaoLoginManager: KakaoLoginManager
    private var isPasswordVisible = false // 비밀번호 표시 상태
    private val userRepository = UserRepository() // UserRepository 인스턴스 생성

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        clearWebViewCache()

        // ViewBinding 설정
        binding = FragmentSigninBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // KakaoLoginManager 초기화
        kakaoLoginManager = KakaoLoginManager(
            authService = KakaoAuthService.create(),
            context = applicationContext
        )

        if (savedInstanceState == null) {
            clearBackStack()
        }


        val naverLoginApi = RetrofitClient.naverLoginApi  // Retrofit API 인스턴스
        naverLoginManager = NaverLoginManager(naverLoginApi, this, binding.naverWebView)

        // ✅ 네이버 로그인 버튼 클릭 이벤트
        binding.naverlogin.setOnClickListener {
            clearWebViewCache() // ✅ WebView 캐시와 쿠키 삭제
            binding.naverWebView.visibility = View.VISIBLE
            naverLoginManager.startNaverLogin()
        }


        setupUI()
    }

    private fun clearWebViewCache() {
        WebView(this).apply {
            clearCache(true)
            clearHistory()
        }

        // ✅ 쿠키 삭제 (비동기 방식 적용)
        runOnUiThread {
            val cookieManager = CookieManager.getInstance()
            cookieManager.removeAllCookies(null)
            cookieManager.flush()
        }

        // ✅ WebView 관련 데이터 삭제 (WebStorage 대체)
        applicationContext.cacheDir.deleteRecursively()
        applicationContext.getDir("webview", Context.MODE_PRIVATE).deleteRecursively()
    }


    private fun clearBackStack() {
        // 백스택에 있는 모든 프래그먼트 제거
        for (i in 0 until supportFragmentManager.backStackEntryCount) {
            supportFragmentManager.popBackStack()
        }
    }

    private fun setupUI() {
        // 이메일과 비밀번호 입력 감지
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updateLoginButtonState()
            }
            override fun afterTextChanged(s: Editable?) {}
        }

        binding.emailLoginEditText.addTextChangedListener(textWatcher)
        binding.passwordLoginEditText.addTextChangedListener(textWatcher)

        // 비밀번호 표시/숨기기 기능
        binding.signinvisible.setOnClickListener {
            togglePasswordVisibility()
        }

        // 로그인 버튼 클릭 리스너
        binding.loginButton.setOnClickListener {
            val email = binding.emailLoginEditText.text.toString().trim()
            val password = binding.passwordLoginEditText.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                performLogin(email, password)
            } else {
                Toast.makeText(this, "이메일과 비밀번호를 입력하세요.", Toast.LENGTH_SHORT).show()
            }
        }

        // 회원가입 버튼 클릭 리스너
        binding.loginButton2.setOnClickListener {
            navigateToSignUpFragment()
        }
        // 카카오 로그인 버튼
        binding.kakaologin.setOnClickListener {
            startKakaoLogin()
        }

    }

    private fun showError(message: String) {
        runOnUiThread {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateLoginButtonState() {
        val email = binding.emailLoginEditText.text.toString().trim()
        val password = binding.passwordLoginEditText.text.toString().trim()

        val isInputValid = email.isNotEmpty() && password.isNotEmpty()
        binding.loginButton.isEnabled = isInputValid
        binding.loginButton.setBackgroundColor(
            if (isInputValid) {
                getColor(R.color.Primary_Orange1)
            } else {
                getColor(R.color.Gray8)
            }
        )
    }

//    private fun updateLoginButtonState() {
//        val email = binding.emailLoginEditText.text.toString().trim()
//        val password = binding.passwordLoginEditText.text.toString().trim()
//
//        val isInputValid = email.isNotEmpty() && password.isNotEmpty()
//        binding.loginButton.apply {
//            isEnabled = isInputValid
//            setBackgroundColor(
//                getColor(if (isInputValid) R.color.Primary_Orange1 else R.color.Gray8)
//            )
//        }
//    }
        private fun navigateToSignUpFragment() {
        val fragment = SignUpFragment()
        supportFragmentManager.commit {
            setReorderingAllowed(true)
            replace(R.id.fragmentContainer, fragment)
            addToBackStack(null) // 뒤로가기 시 이전 화면으로 돌아가게 설정
        }
    }

    private fun togglePasswordVisibility() {
        if (isPasswordVisible) {
            // 비밀번호 숨기기
            binding.passwordLoginEditText.transformationMethod = PasswordTransformationMethod.getInstance()
            binding.signinvisible.setImageResource(R.drawable.ic_eye_visible) // 숨기기 아이콘 설정
        } else {
            // 비밀번호 표시
            binding.passwordLoginEditText.transformationMethod = HideReturnsTransformationMethod.getInstance()
            binding.signinvisible.setImageResource(R.drawable.ic_eye_invisible) // 보이기 아이콘 설정
        }
        isPasswordVisible = !isPasswordVisible
        binding.passwordLoginEditText.text?.let { binding.passwordLoginEditText.setSelection(it.length) } // 커서를 끝으로 이동
    }
//    private fun togglePasswordVisibility() {
//        binding.passwordLoginEditText.apply {
//            transformationMethod = if (isPasswordVisible) {
//                PasswordTransformationMethod.getInstance()
//            } else {
//                HideReturnsTransformationMethod.getInstance()
//            }
//            text?.let { setSelection(it.length) }
//        }
//
//        binding.signinvisible.setImageResource(
//            if (isPasswordVisible) R.drawable.ic_eye_visible
//            else R.drawable.ic_eye_invisible
//        )
//        isPasswordVisible = !isPasswordVisible
//    }



    fun performNaverLogin(authCode: String, state: String?) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = naverLoginApi.getNaverAccessToken(authCode, state ?: "").awaitResponse()

                if (response.isSuccessful) {
                    response.body()?.let { responseBody ->
                        val successData = responseBody.success
                        val user = successData.user

                        val accessToken = successData.accessToken
                        val refreshToken = successData.refreshToken
                        val userId = user.userId
                        val userName = user.name
                        val userEmail = user.email
                        val userPhoneNumber = user.phoneNum ?: ""  // 값이 없으면 빈 문자열 저장
                        val profileImage = user.profileImage ?: ""

                        if (accessToken != null && userId != null && userName != null && userEmail != null) {
                            // ✅ SharedPreferences에 데이터 저장
                            UserRepository.saveAuthToken(this@LoginActivity, accessToken)
                            SharedPreferencesManager.saveUserId(this@LoginActivity, userId)
                            SharedPreferencesManager.saveUserName(this@LoginActivity, userName)
                            SharedPreferencesManager.saveUserEmail(this@LoginActivity, userEmail)
                            SharedPreferencesManager.saveUserPhoneNumber(this@LoginActivity, userPhoneNumber)
                            SharedPreferencesManager.saveUserProfileImage(this@LoginActivity, profileImage)
                            SharedPreferencesManager.saveAccessToken(this@LoginActivity, accessToken)
                            SharedPreferencesManager.saveRefreshToken(this@LoginActivity, refreshToken)

                            // ✅ 메인 화면으로 이동
                            withContext(Dispatchers.Main) {
                                navigateToMain(accessToken)
                            }
                        } else {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(this@LoginActivity, "네이버 로그인 정보가 부족합니다.", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } ?: run {
                        withContext(Dispatchers.Main) {
                            Log.e("NAVER_LOGIN", "응답 본문이 비어 있음")
                        }
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Log.e("NAVER_LOGIN", "로그인 실패: ${response.code()} - ${response.errorBody()?.string()}")
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Log.e("NAVER_LOGIN", "네트워크 오류 발생: ${e.message}", e)
                    Toast.makeText(this@LoginActivity, "네트워크 오류 발생: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    // 설문조사 임시코드
    private fun performLogin(email: String, password: String) {
        userRepository.login(email, password).enqueue(object : retrofit2.Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                Log.d("Login", "Response Code: ${response.code()}")

                if (response.isSuccessful) {
                    val accessToken = response.body()?.success?.accessToken

                    if (accessToken != null) {
                        UserRepository.saveAuthToken(this@LoginActivity, accessToken)

                        // 사용자가 설문조사를 완료했는지 확인하는 로직
                        checkSurveyStatus(accessToken)
                    } else {
                        Toast.makeText(this@LoginActivity, "토큰이 없습니다.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@LoginActivity, "로그인 실패: ${response.errorBody()?.string()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Log.e("Login", "Network Error", t)
                Toast.makeText(this@LoginActivity, "네트워크 오류: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }


    //원래 코드
//    private fun performLogin(email: String, password: String) {
//        userRepository.login(email, password).enqueue(object : retrofit2.Callback<LoginResponse> {
//            override fun onResponse(call: retrofit2.Call<LoginResponse>, response: Response<LoginResponse>) {
//                Log.d("Login", "Response Code: ${response.code()}")
//
//                if (response.isSuccessful) {
//                    val loginSuccess = response.body()?.success
//                    val accessToken = loginSuccess?.accessToken
//                    val userId = loginSuccess?.user?.userId
//                    val userName = loginSuccess?.user?.name
//                    val userEmail = loginSuccess?.user?.email  // ✅ 이메일 추가
//                    val userPhoneNumber = loginSuccess?.user?.phoneNum  // ✅ 전화번호 추가
//
//                    if (accessToken != null && userId != null && userName != null && userEmail != null && userPhoneNumber != null) {
//                        // ✅ 로그인 정보 저장
//                        UserRepository.saveAuthToken(this@LoginActivity, accessToken)
//                        SharedPreferencesManager.saveUserId(this@LoginActivity, userId)
//                        SharedPreferencesManager.saveUserName(this@LoginActivity, userName)
//                        SharedPreferencesManager.saveUserEmail(this@LoginActivity, userEmail) // ✅ 이메일 저장
//                        SharedPreferencesManager.saveUserPhoneNumber(this@LoginActivity, userPhoneNumber) // ✅ 전화번호 저장
//
////                        navigateToMain(accessToken)
//                          navigateToSurvey()
//                    } else {
//                        Toast.makeText(this@LoginActivity, "로그인 정보가 부족합니다.", Toast.LENGTH_SHORT).show()
//                    }
//                } else {
//                    Toast.makeText(this@LoginActivity, "로그인 실패: ${response.errorBody()?.string()}", Toast.LENGTH_SHORT).show()
//                }
//            }
//
//            override fun onFailure(call: retrofit2.Call<LoginResponse>, t: Throwable) {
//                Log.e("Login", "Network Error", t)
//                Toast.makeText(this@LoginActivity, "네트워크 오류: ${t.message}", Toast.LENGTH_SHORT).show()
//            }
//        })
//    }



    private fun checkSurveyStatus(accessToken: String) {
        // TODO: API를 통해 사용자의 설문조사 완료 여부를 확인
        // 임시로 설문조사를 하지 않았다고 가정
        val hasSurveyCompleted = false

        if (!hasSurveyCompleted) {
            navigateToSurvey()
        } else {
            navigateToMain(accessToken)
        }
    }
    // 카카오 로그인 메서드
    private fun startKakaoLogin() {
        binding.apply {
            loginConstraintLayout.visibility = View.GONE
            kakaoWebView.apply {
                visibility = View.VISIBLE
                settings.apply {
                    javaScriptEnabled = true
                    domStorageEnabled = true
                }
                webViewClient = createKakaoWebViewClient()
                loadUrl(kakaoLoginManager.getKakaoAuthUrl())
            }
        }
    }

    private fun createKakaoWebViewClient(): WebViewClient {
        return object : WebViewClient() {
            override fun onReceivedError(
                view: WebView?,
                errorCode: Int,
                description: String?,
                failingUrl: String?
            ) {
                Log.e(TAG, "WebView Error: $errorCode - $description")
                view?.let { webView ->
                    loadErrorPage(
                        webView,
                        errorCode,
                        description ?: "알 수 없는 오류",
                        failingUrl ?: "알 수 없는 URL"
                    )
                }
                restoreLoginLayout()
            }

            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                url?.let {
                    // 리다이렉트 URI를 정확히 일치시킴
                    val redirectUri = "http://3.38.39.238:3000/auth/kakao/callback"

                    if (url.startsWith(redirectUri)) {
                        val uri = Uri.parse(url)
                        val authCode = uri.getQueryParameter("code")

                        authCode?.let { code ->
                            lifecycleScope.launch {
                                handleKakaoAuthCode(code)
                            }
                        }
                        hideWebView()
                        return true
                    }
                }
                return false
            }

            private fun loadErrorPage(
                view: WebView,
                errorCode: Int,
                description: String,
                failingUrl: String
            ) {
                val htmlData = """
                    <html>
                    <body style="text-align: center; font-family: Arial, sans-serif; padding-top: 50px;">
                        <h1>네트워크 오류</h1>
                        <p>페이지를 로드할 수 없습니다.</p>
                        <p>오류 코드: $errorCode</p>
                        <p>설명: $description</p>
                        <p>URL: $failingUrl</p>
                        <button onclick="window.location.reload()">다시 시도</button>
                    </body>
                    </html>
                """.trimIndent()

                view.loadDataWithBaseURL(
                    null,
                    htmlData,
                    "text/html",
                    "UTF-8",
                    null
                )
            }

            private fun restoreLoginLayout() {
                binding.loginConstraintLayout.visibility = View.VISIBLE
            }

            private fun hideWebView() {
                binding.apply {
                    kakaoWebView.visibility = View.GONE
                    loginConstraintLayout.visibility = View.VISIBLE
                }
            }
        }
    }


    private suspend fun handleKakaoAuthCode(authCode: String) {
        try {
            // API 호출 결과를 받아옵니다
            val result = withContext(Dispatchers.IO) {
                kakaoLoginManager.handleKakaoLogin(authCode)
            }

            // when 표현식으로 결과를 처리합니다
            when (result) {
                is LoginResult.Success -> {
                    processSuccessfulKakaoLogin(result.data)
                }
                is LoginResult.Error -> {
                    handleKakaoLoginError(result)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "예상치 못한 카카오 로그인 오류", e)
            showError("로그인 중 오류 발생")
        }
    }

    private fun processSuccessfulKakaoLogin(authResponse: AuthResponse) {
        // 로그를 남깁니다
        Log.d(TAG, "Full Auth Response: $authResponse")

        // null 체크를 통해 안전하게 액세스 토큰을 추출합니다
        val accessToken = authResponse.accessToken
        if (accessToken == null) {
            Log.e(TAG, "Access token is missing")
            showError("로그인 토큰을 받지 못했습니다.")
            return
        }

        // refreshToken은 null일 수 있으므로 orEmpty()를 사용합니다
        val refreshToken = authResponse.refreshToken.orEmpty()

        // 사용자 ID를 안전하게 추출합니다
        val userId = authResponse.user?.let { user ->
            user.userId ?: user.id
        }

        if (userId == null) {
            Log.e(TAG, "No user ID found in auth response")
            showError("사용자 정보를 찾을 수 없습니다.")
            return
        }

        try {
            // 사용자 데이터를 저장합니다
            SharedPreferencesManager.saveUserData(
                context = this,
                userId = userId.toInt(),
                accessToken = accessToken,
                refreshToken = refreshToken
            )

            // 성공 로그를 남깁니다
            Log.d(TAG, "Login Success - User ID: $userId")

            // 메인 화면으로 이동합니다
            navigateToMain(accessToken)
        } catch (e: Exception) {
            Log.e(TAG, "Error saving user data", e)
            showError("사용자 데이터 저장 중 오류가 발생했습니다.")
        }
    }


    private fun handleKakaoLoginError(error: LoginResult.Error) {
        // 에러 로그를 남깁니다
        Log.e(TAG, "Login Error: ${error.exception.message}", error.exception)
        showError("로그인 실패: ${error.exception.message}")

        // 에러 코드가 있다면 로그에 남깁니다
        error.code?.let { errorCode ->
            Log.e(TAG, "Error Code: $errorCode")
        }
    }


    private fun navigateToSurvey() {
        // 기존 프래그먼트들을 모두 제거
        clearBackStack()

        // 새로운 컨테이너 레이아웃으로 전환
        setContentView(R.layout.activity_survey_container)

        // SurveyGoalFragment 추가
        supportFragmentManager.commit {
            setReorderingAllowed(true)
            replace(R.id.survey_container, SurveyGoalFragment())
            // 설문 진행 중에는 백스택에 추가하지 않음
        }
    }
//
    private fun navigateToMain(accessToken: String) {
        val intent = Intent(this@LoginActivity, MainActivity::class.java)
        intent.putExtra("ACCESS_TOKEN", accessToken)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

}




// 시발 2
//package com.example.umc.Signin
//
//import android.content.Intent
//import android.net.Uri
//import android.os.Bundle
//import android.text.Editable
//import android.text.TextWatcher
//import android.text.method.HideReturnsTransformationMethod
//import android.text.method.PasswordTransformationMethod
//import android.util.Log
//import android.view.View
//import android.webkit.WebView
//import android.webkit.WebViewClient
//import android.widget.Toast
//import androidx.appcompat.app.AppCompatActivity
//import androidx.fragment.app.commit
//import androidx.lifecycle.lifecycleScope
//import com.example.umc.Main.MainActivity
//import com.example.umc.R
//import com.example.umc.SignUp.SignUpFragment
//import com.example.umc.Survey.SurveyGoalFragment
//import com.example.umc.UserApi.Response.LoginResponse
//import com.example.umc.UserApi.Response.NaverLoginResponse
//import com.example.umc.UserApi.RetrofitClient
//import com.example.umc.UserApi.SharedPreferencesManager
//import com.example.umc.UserApi.UserRepository
//import com.example.umc.databinding.FragmentSigninBinding
//import com.google.gson.Gson
//import com.google.gson.JsonSyntaxException
//import com.navercorp.nid.NaverIdLoginSDK
//import com.navercorp.nid.oauth.OAuthLoginCallback
//import com.navercorp.nid.profile.api.NidProfileApi
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.launch
//import kotlinx.coroutines.withContext
//import retrofit2.Call
//import retrofit2.Callback
//import retrofit2.Response
//import retrofit2.awaitResponse
//
////login 로직 처리
//
//class LoginActivity : AppCompatActivity() {
//
//    private lateinit var binding: FragmentSigninBinding
//    private var isPasswordVisible = false // 비밀번호 표시 상태
//    private val userRepository = UserRepository() // UserRepository 인스턴스 생성
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        // ViewBinding 설정
//        binding = FragmentSigninBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//        if (savedInstanceState == null) {
//            clearBackStack()
//        }
//
//        setupUI()
//    }
//    private fun clearBackStack() {
//        // 백스택에 있는 모든 프래그먼트 제거
//        for (i in 0 until supportFragmentManager.backStackEntryCount) {
//            supportFragmentManager.popBackStack()
//        }
//    }
//
//    private fun setupUI() {
//        // 이메일과 비밀번호 입력 감지
//        val textWatcher = object : TextWatcher {
//            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
//
//            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//                updateLoginButtonState()
//            }
//
//            override fun afterTextChanged(s: Editable?) {}
//        }
//
//        binding.emailLoginEditText.addTextChangedListener(textWatcher)
//        binding.passwordLoginEditText.addTextChangedListener(textWatcher)
//
//        // 비밀번호 표시/숨기기 기능
//        binding.signinvisible.setOnClickListener {
//            togglePasswordVisibility()
//        }
//
//        // 로그인 버튼 클릭 리스너
//        binding.loginButton.setOnClickListener {
//            val email = binding.emailLoginEditText.text.toString().trim()
//            val password = binding.passwordLoginEditText.text.toString().trim()
//
//            if (email.isNotEmpty() && password.isNotEmpty()) {
//                performLogin(email, password)
//            } else {
//                Toast.makeText(this, "이메일과 비밀번호를 입력하세요.", Toast.LENGTH_SHORT).show()
//            }
//        }
//
//        // 회원가입 버튼 클릭 리스너
//        binding.loginButton2.setOnClickListener {
//            navigateToSignUpFragment()
//        }
//
//        binding.naverlogin.setOnClickListener {
//            loginWithNaver()
//            binding.naverWebView.visibility = View.VISIBLE  // ✅ WebView 강제 표시
//        }
//
//
//
////        // 카카오 로그인 버튼 클릭 리스너
////        binding.kakaologin.setOnClickListener {
////            // 카카오 로그인 호출
////            loginWithKakao()
////        }
//    }
//
//
//    //    private fun loginWithKakao() {
////        // 카카오 로그인 후 받은 액세스 토큰
////        val accessToken = "여기_카카오_액세스_토큰_입력"
////
////        RetrofitClient.kakaoLoginApi.loginWithKakao(accessToken).enqueue(object : Callback<LoginResponse> {
////            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
////                if (response.isSuccessful) {
////                    val loginResponse = response.body()
////                    if (loginResponse?.success == true) {
////                        val token = loginResponse.accessToken
////                        // 로그인 성공 후 처리
////                        UserRepository.saveAuthToken(this@LoginActivity, token)
////                        navigateToMain(token)
////                    } else {
////                        Toast.makeText(this@LoginActivity, "로그인 실패: ${loginResponse?.message}", Toast.LENGTH_SHORT).show()
////                    }
////                } else {
////                    Toast.makeText(this@LoginActivity, "서버 오류", Toast.LENGTH_SHORT).show()
////                }
////            }
////
////            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
////                Toast.makeText(this@LoginActivity, "네트워크 오류", Toast.LENGTH_SHORT).show()
////            }
////        })
////    }
////
////
////    private fun performKakaoLogin(kakaoToken: String) {
////        userRepository.kakaoLogin(kakaoToken).enqueue(object : Callback<LoginResponse> {
////            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
////                if (response.isSuccessful) {
////                    val accessToken = response.body()?.success?.accessToken
////                    if (accessToken != null) {
////                        UserRepository.saveAuthToken(this@LoginActivity, accessToken)
////                        checkSurveyStatus(accessToken)
////                    }
////                } else {
////                    Toast.makeText(this@LoginActivity, "카카오 로그인 실패", Toast.LENGTH_SHORT).show()
////                }
////            }
////
////            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
////                Toast.makeText(this@LoginActivity, "네트워크 오류", Toast.LENGTH_SHORT).show()
////            }
////        })
////    }
//
// //이건 이제 api
// private fun loginWithNaver() {
//     lifecycleScope.launch(Dispatchers.IO) {
//         try {
//             val response = RetrofitClient.naverLoginApi.loginWithNaver().execute()
//             if (response.isSuccessful) {
//                 val loginUrl = response.body()?.string()
//                 Log.d("NAVER_LOGIN", "네이버 로그인 URL: $loginUrl")
//
//                 withContext(Dispatchers.Main) {
//                     loginUrl?.let {
//                         openNaverLoginWebView(it)
//                     } ?: run {
//                         Toast.makeText(this@LoginActivity, "네이버 로그인 URL을 불러오지 못했습니다.", Toast.LENGTH_SHORT).show()
//                     }
//                 }
//             } else {
//                 Log.e("NAVER_LOGIN", "네이버 로그인 실패: ${response.code()}")
//             }
//         } catch (e: Exception) {
//             Log.e("NAVER_LOGIN", "네트워크 오류 발생: ${e.message}", e)
//         }
//     }
// }
//    private fun openNaverLoginWebView(loginUrl: String) {
//        binding.naverWebView.apply {
//            visibility = View.VISIBLE
//            settings.apply {
//                javaScriptEnabled = true  // ✅ JavaScript 활성화
//                domStorageEnabled = true  // ✅ 웹 저장소 활성화
//                useWideViewPort = true  // ✅ 웹페이지가 화면 크기에 맞게 조정되도록 설정
//                loadWithOverviewMode = true  // ✅ 초기 로딩 시 전체 화면 맞춤
//                mixedContentMode = android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW  // ✅ HTTP & HTTPS 동시 허용
//                allowContentAccess = true  // ✅ 컨텐츠 접근 허용
//                allowFileAccess = true  // ✅ 파일 접근 허용
//                databaseEnabled = true  // ✅ 데이터 저장 허용
//            }
//
//            webViewClient = object : WebViewClient() {
//                override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
//                    url?.let {
//                        Log.d("NAVER_LOGIN", "Redirect URL: $url") // ✅ 리디렉트 URL 확인용 로그
//                        if (url.startsWith("http://3.38.39.238:3000/auth/naver/callback")) {
//                            val uri = Uri.parse(url)
//                            val authCode = uri.getQueryParameter("code")
//                            val state = uri.getQueryParameter("state")
//
//                            authCode?.let { code ->
//                                lifecycleScope.launch {
//                                    requestNaverAccessToken(code, state)
//                                }
//                            }
//
//                            visibility = View.GONE // ✅ WebView 숨기기
//                            binding.loginConstraintLayout.visibility = View.VISIBLE
//                            return true
//                        }
//                    }
//                    return false
//                }
//
//                override fun onPageFinished(view: WebView?, url: String?) {
//                    super.onPageFinished(view, url)
//                    Log.d("NAVER_LOGIN", "페이지 로드 완료: $url")
//                }
//            }
//            loadUrl(loginUrl)
//        }
//    }
//
//
//    private suspend fun requestNaverAccessToken(authCode: String, state: String?) {
//        withContext(Dispatchers.IO) {
//            try {
//                Log.d("NAVER_LOGIN", "네이버 액세스 토큰 요청 중...")
//
//                val response = RetrofitClient.naverLoginApi.getNaverAccessToken(authCode, state ?: "").execute()
//
//                if (response.isSuccessful) {
//                    val loginResponse = response.body()
//
//                    if (loginResponse != null) {
//                        Log.d("NAVER_LOGIN", "네이버 로그인 성공! 액세스 토큰: ${loginResponse.success.accessToken}")
//
//                        loginResponse.success?.accessToken?.let { accessToken ->
//                            withContext(Dispatchers.Main) {
//                                navigateToMain(accessToken)
//                            }
//                        } ?: run {
//                            Log.e("NAVER_LOGIN", "토큰이 없습니다.")
//                        }
//                    } else {
//                        Log.e("NAVER_LOGIN", "응답 본문이 비어 있습니다.")
//                    }
//                } else {
//                    val errorMessage = response.errorBody()?.string() ?: "Unknown error"
//                    Log.e("NAVER_LOGIN", "토큰 요청 실패: ${response.code()} - $errorMessage")
//                }
//            } catch (e: Exception) {
//                Log.e("NAVER_LOGIN", "네트워크 오류 발생: ${e.message}", e)
//            }
//        }
//    }
//
//
//
//
//
//
//
//    private fun updateLoginButtonState() {
//        val email = binding.emailLoginEditText.text.toString().trim()
//        val password = binding.passwordLoginEditText.text.toString().trim()
//
//        val isInputValid = email.isNotEmpty() && password.isNotEmpty()
//        binding.loginButton.isEnabled = isInputValid
//        binding.loginButton.setBackgroundColor(
//            if (isInputValid) {
//                getColor(R.color.Primary_Orange1)
//            } else {
//                getColor(R.color.Gray8)
//            }
//        )
//    }
//
//    private fun navigateToSignUpFragment() {
//        val fragment = SignUpFragment()
//        supportFragmentManager.commit {
//            setReorderingAllowed(true)
//            replace(R.id.fragmentContainer, fragment)
//            addToBackStack(null) // 뒤로가기 시 이전 화면으로 돌아가게 설정
//        }
//    }
//
//    private fun togglePasswordVisibility() {
//        if (isPasswordVisible) {
//            // 비밀번호 숨기기
//            binding.passwordLoginEditText.transformationMethod = PasswordTransformationMethod.getInstance()
//            binding.signinvisible.setImageResource(R.drawable.ic_eye_visible) // 숨기기 아이콘 설정
//        } else {
//            // 비밀번호 표시
//            binding.passwordLoginEditText.transformationMethod = HideReturnsTransformationMethod.getInstance()
//            binding.signinvisible.setImageResource(R.drawable.ic_eye_invisible) // 보이기 아이콘 설정
//        }
//        isPasswordVisible = !isPasswordVisible
//        binding.passwordLoginEditText.text?.let { binding.passwordLoginEditText.setSelection(it.length) } // 커서를 끝으로 이동
//    }
//
//    // 설문조사 임시코드
////    private fun performLogin(email: String, password: String) {
////        userRepository.login(email, password).enqueue(object : retrofit2.Callback<LoginResponse> {
////            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
////                Log.d("Login", "Response Code: ${response.code()}")
////
////                if (response.isSuccessful) {
////                    val accessToken = response.body()?.success?.accessToken
////                    val userId = response.body()?.success?.user?.userId  // userId 받아오기
////
////                    if (accessToken != null) {
////                        UserRepository.saveAuthToken(this@LoginActivity, accessToken)
////
////                        // 사용자가 설문조사를 완료했는지 확인하는 로직
////                        checkSurveyStatus(accessToken)
////                    } else {
////                        Toast.makeText(this@LoginActivity, "토큰이 없습니다.", Toast.LENGTH_SHORT).show()
////                    }
////                } else {
////                    Toast.makeText(this@LoginActivity, "로그인 실패: ${response.errorBody()?.string()}", Toast.LENGTH_SHORT).show()
////                }
////            }
////
////            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
////                Log.e("Login", "Network Error", t)
////                Toast.makeText(this@LoginActivity, "네트워크 오류: ${t.message}", Toast.LENGTH_SHORT).show()
////            }
////        })
////    }
//
//
//    //원래 코드
//    private fun performLogin(email: String, password: String) {
//        userRepository.login(email, password).enqueue(object : Callback<LoginResponse> {
//            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
//                Log.d("Login", "Response Code: ${response.code()}")
//
//                if (response.isSuccessful) {
//                    val accessToken = response.body()?.success?.accessToken
//                    val userId = response.body()?.success?.user?.userId  // userId 받아오기
//
//                    if (accessToken != null) {
//                        UserRepository.saveAuthToken(this@LoginActivity, accessToken)
//                        if (userId != null) {
//                            SharedPreferencesManager.saveUserId(this@LoginActivity, userId)
//                        }  // userId 저장
//                        Log.d("LoginAuthToken", "토큰과 userId가 저장되었습니다: $accessToken, $userId")
//
//                        navigateToMain(accessToken) //코드가 중복된 같아서 이렇게 바꿨습니다.
//                    } else {
//                        Toast.makeText(this@LoginActivity, "토큰이 없습니다.", Toast.LENGTH_SHORT).show()
//                    }
//                } else {
//                    Toast.makeText(this@LoginActivity, "로그인 실패: ${response.errorBody()?.string()}", Toast.LENGTH_SHORT).show()
//                }
//            }
//
//            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
//                Log.e("Login", "Network Error", t)
//                Toast.makeText(this@LoginActivity, "네트워크 오류: ${t.message}", Toast.LENGTH_SHORT).show()
//            }
//        })
//    }
//
//
//    private fun checkSurveyStatus(accessToken: String) {
//        // TODO: API를 통해 사용자의 설문조사 완료 여부를 확인
//        // 임시로 설문조사를 하지 않았다고 가정
//        val hasSurveyCompleted = false
//
//        if (!hasSurveyCompleted) {
//            navigateToSurvey()
//        } else {
//            navigateToMain(accessToken)
//        }
//    }
//
//    private fun navigateToSurvey() {
//        // 기존 프래그먼트들을 모두 제거
//        clearBackStack()
//
//        // 새로운 컨테이너 레이아웃으로 전환
//        setContentView(R.layout.activity_survey_container)
//
//        // SurveyGoalFragment 추가
//        supportFragmentManager.commit {
//            setReorderingAllowed(true)
//            replace(R.id.survey_container, SurveyGoalFragment())
//            // 설문 진행 중에는 백스택에 추가하지 않음
//        }
//    }
//
//    private fun navigateToMain(accessToken: String) {
//        val intent = Intent(this, MainActivity::class.java).apply {
//            putExtra("ACCESS_TOKEN", accessToken)
//            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//        }
//        startActivity(intent)
//        finish()
//    }
//
//    private fun NavernavigateToMain() {
//        val intent = Intent(this@LoginActivity, MainActivity::class.java)
//        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//        startActivity(intent)
//        finish()
//    }
//
//}
//허허

// 일단 저장까지 다 되는거
//package com.example.umc.Signin
//
//import android.content.Intent
//import android.net.Uri
//import android.os.Bundle
//import android.text.Editable
//import android.text.TextWatcher
//import android.text.method.HideReturnsTransformationMethod
//import android.text.method.PasswordTransformationMethod
//import android.util.Log
//import android.view.View
//import android.widget.Toast
//import androidx.appcompat.app.AppCompatActivity
//import androidx.browser.customtabs.CustomTabsIntent
//
//import androidx.fragment.app.commit
//import androidx.lifecycle.lifecycleScope
//import com.example.umc.Main.MainActivity
//import com.example.umc.R
//import com.example.umc.SignUp.SignUpFragment
//import com.example.umc.Survey.SurveyGoalFragment
//import com.example.umc.UserApi.Response.LoginResponse
//import com.example.umc.UserApi.Response.NaverLoginResponse
//import com.example.umc.UserApi.RetrofitClient
//import com.example.umc.UserApi.SharedPreferencesManager
//import com.example.umc.UserApi.UserRepository
//import com.example.umc.databinding.FragmentSigninBinding
//import com.google.gson.Gson
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.launch
//import kotlinx.coroutines.withContext
//import retrofit2.Response
//
//class LoginActivity : AppCompatActivity() {
//
//    private lateinit var binding: FragmentSigninBinding
//    private var isPasswordVisible = false
//    private val userRepository = UserRepository()
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        // ViewBinding 설정
//        binding = FragmentSigninBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//        if (savedInstanceState == null) {
//            clearBackStack()
//        }
//
//        setupUI()
//    }
//
//    private fun clearBackStack() {
//        // 백스택 초기화
//        repeat(supportFragmentManager.backStackEntryCount) {
//            supportFragmentManager.popBackStack()
//        }
//    }
//
//    private fun setupUI() {
//        val textWatcher = object : TextWatcher {
//            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
//            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//                updateLoginButtonState()
//            }
//            override fun afterTextChanged(s: Editable?) {}
//        }
//
//        binding.emailLoginEditText.addTextChangedListener(textWatcher)
//        binding.passwordLoginEditText.addTextChangedListener(textWatcher)
//
//        binding.signinvisible.setOnClickListener {
//            togglePasswordVisibility()
//        }
//
//        binding.loginButton.setOnClickListener {
//            val email = binding.emailLoginEditText.text.toString().trim()
//            val password = binding.passwordLoginEditText.text.toString().trim()
//
//            if (email.isNotEmpty() && password.isNotEmpty()) {
//                performLogin(email, password)
//            } else {
//                Toast.makeText(this, "이메일과 비밀번호를 입력하세요.", Toast.LENGTH_SHORT).show()
//            }
//        }
//
//        binding.loginButton2.setOnClickListener {
//            navigateToSignUpFragment()
//        }
//
//        binding.naverlogin.setOnClickListener {
//            openNaverLoginInBrowser()
//        }
//    }
//
//    /** ✅ Chrome Custom Tabs 사용하여 네이버 로그인 화면 열기 */
//    private fun openNaverLoginInBrowser() {
//        lifecycleScope.launch(Dispatchers.IO) {
//            try {
//                val response = RetrofitClient.naverLoginApi.loginWithNaver().execute()
//                if (response.isSuccessful) {
//                    val loginUrl = response.body()?.string()
//                    Log.d("NAVER_LOGIN", "네이버 로그인 URL: $loginUrl")
//
//                    withContext(Dispatchers.Main) {
//                        loginUrl?.let {
//                            val customTabsIntent = CustomTabsIntent.Builder().setShowTitle(true).build()
//                            customTabsIntent.launchUrl(this@LoginActivity, Uri.parse(it))
//                        } ?: run {
//                            Toast.makeText(this@LoginActivity, "네이버 로그인 URL을 불러오지 못했습니다.", Toast.LENGTH_SHORT).show()
//                        }
//                    }
//                } else {
//                    Log.e("NAVER_LOGIN", "네이버 로그인 실패: ${response.code()}")
//                }
//            } catch (e: Exception) {
//                Log.e("NAVER_LOGIN", "네트워크 오류 발생: ${e.message}", e)
//            }
//        }
//    }
//
//    /** ✅ 네이버 로그인 후 콜백을 받아 토큰 요청 */
//    private suspend fun requestNaverAccessToken(authCode: String, state: String?) {
//        withContext(Dispatchers.IO) {
//            try {
//                val response: Response<NaverLoginResponse> =
//                    RetrofitClient.naverLoginApi.getNaverAccessToken(authCode, state ?: "").execute()
//
//                if (response.isSuccessful) {
//                    val loginResponse = response.body()
//                    loginResponse?.success?.accessToken?.let { accessToken ->
//                        withContext(Dispatchers.Main) {
//                            navigateToMain(accessToken)
//                        }
//                    } ?: run {
//                        Log.e("NAVER_LOGIN", "토큰이 없습니다.")
//                    }
//                } else {
//                    Log.e("NAVER_LOGIN", "토큰 요청 실패: ${response.code()} - ${response.errorBody()?.string()}")
//                }
//            } catch (e: Exception) {
//                Log.e("NAVER_LOGIN", "네트워크 오류 발생: ${e.message}", e)
//            }
//        }
//    }
//
//    private fun updateLoginButtonState() {
//        val email = binding.emailLoginEditText.text.toString().trim()
//        val password = binding.passwordLoginEditText.text.toString().trim()
//
//        val isInputValid = email.isNotEmpty() && password.isNotEmpty()
//        binding.loginButton.isEnabled = isInputValid
//        binding.loginButton.setBackgroundColor(
//            if (isInputValid) getColor(R.color.Primary_Orange1) else getColor(R.color.Gray8)
//        )
//    }
//
//    private fun navigateToSignUpFragment() {
//        val fragment = SignUpFragment()
//        supportFragmentManager.commit {
//            setReorderingAllowed(true)
//            replace(R.id.fragmentContainer, fragment)
//            addToBackStack(null)
//        }
//    }
//
//    private fun togglePasswordVisibility() {
//        if (isPasswordVisible) {
//            binding.passwordLoginEditText.transformationMethod = PasswordTransformationMethod.getInstance()
//            binding.signinvisible.setImageResource(R.drawable.ic_eye_visible)
//        } else {
//            binding.passwordLoginEditText.transformationMethod = HideReturnsTransformationMethod.getInstance()
//            binding.signinvisible.setImageResource(R.drawable.ic_eye_invisible)
//        }
//        isPasswordVisible = !isPasswordVisible
//        binding.passwordLoginEditText.setSelection(binding.passwordLoginEditText.length())
//    }
//
//    private fun performLogin(email: String, password: String) {
//        userRepository.login(email, password).enqueue(object : retrofit2.Callback<LoginResponse> {
//            override fun onResponse(call: retrofit2.Call<LoginResponse>, response: Response<LoginResponse>) {
//                Log.d("Login", "Response Code: ${response.code()}")
//
//                if (response.isSuccessful) {
//                    val loginSuccess = response.body()?.success
//                    val accessToken = loginSuccess?.accessToken
//                    val userId = loginSuccess?.user?.userId
//                    val userName = loginSuccess?.user?.name  // ✅ 사용자 이름
//                    val userEmail = loginSuccess?.user?.email  // ✅ 이메일 추가
//                    val userPhoneNumber = loginSuccess?.user?.phoneNum  // ✅ 전화번호 추가
//
//                    if (accessToken != null && userId != null && userName != null && userEmail != null && userPhoneNumber != null) {
//                        // ✅ 토큰, userId, name, email, phoneNum 저장
//                        UserRepository.saveAuthToken(this@LoginActivity, accessToken)
//                        SharedPreferencesManager.saveUserId(this@LoginActivity, userId)
//                        SharedPreferencesManager.saveUserName(this@LoginActivity, userName)
//                        SharedPreferencesManager.saveUserEmail(this@LoginActivity, userEmail) // ✅ 이메일 저장
//                        SharedPreferencesManager.saveUserPhoneNumber(this@LoginActivity, userPhoneNumber) // ✅ 전화번호 저장
//
//                        navigateToMain(accessToken)
//                    } else {
//                        Toast.makeText(this@LoginActivity, "로그인 정보가 부족합니다.", Toast.LENGTH_SHORT).show()
//                    }
//                } else {
//                    Toast.makeText(this@LoginActivity, "로그인 실패: ${response.errorBody()?.string()}", Toast.LENGTH_SHORT).show()
//                }
//            }
//
//            override fun onFailure(call: retrofit2.Call<LoginResponse>, t: Throwable) {
//                Log.e("Login", "Network Error", t)
//                Toast.makeText(this@LoginActivity, "네트워크 오류: ${t.message}", Toast.LENGTH_SHORT).show()
//            }
//        })
//    }
//
//
//
//    private fun navigateToMain(accessToken: String) {
//        val intent = Intent(this, MainActivity::class.java).apply {
//            putExtra("ACCESS_TOKEN", accessToken)
//            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//        }
//        startActivity(intent)
//        finish()
//    }
//}
//





//package com.example.umc.Signin
//
//import android.content.Intent
//import android.net.Uri
//import android.os.Bundle
//import android.util.Log
//import android.view.View
//import android.webkit.WebView
//import android.webkit.WebViewClient
//import android.widget.Toast
//import androidx.appcompat.app.AppCompatActivity
//import androidx.lifecycle.lifecycleScope
//import com.example.umc.Main.MainActivity
//import com.example.umc.UserApi.RetrofitClient
//import com.example.umc.UserApi.Response.NaverLoginResponse
//import com.example.umc.databinding.FragmentSigninBinding
//import com.google.gson.Gson
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.launch
//import kotlinx.coroutines.withContext
//import retrofit2.awaitResponse
//
//class LoginActivity : AppCompatActivity() {
//    private lateinit var binding: FragmentSigninBinding
//    private val NAVER_CLIENT_ID = "YOUR_NAVER_CLIENT_ID"
//    private val NAVER_REDIRECT_URI = "YOUR_NAVER_REDIRECT_URI"
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = FragmentSigninBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        setupNaverLoginButton()
//    }
//
//    private fun setupNaverLoginButton() {
//        binding.naverlogin.setOnClickListener {
//            startNaverLogin()
//        }
//    }
//
//    private fun startNaverLogin() {
//        // 로그인 화면 숨기기
//        binding.loginConstraintLayout.visibility = View.GONE
//
//        // WebView 설정
//        val webView = binding.naverWebView
//        webView.apply {
//            visibility = View.VISIBLE
//            settings.apply {
//                javaScriptEnabled = true
//                domStorageEnabled = true
//            }
//
//            webViewClient = object : WebViewClient() {
//                override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
//                    url?.let {
//                        // 네이버 로그인 콜백 URL 확인
//                        if (url.startsWith(NAVER_REDIRECT_URI)) {
//                            val uri = Uri.parse(url)
//                            val authCode = uri.getQueryParameter("code")
//
//                            authCode?.let { code ->
//                                lifecycleScope.launch {
//                                    handleNaverAuthCode(code)
//                                }
//                            }
//
//                            // WebView와 로그인 화면 상태 복원
//                            webView.visibility = View.GONE
//                            binding.loginConstraintLayout.visibility = View.VISIBLE
//                            return true
//                        }
//                    }
//                    return false
//                }
//            }
//
//            // 네이버 로그인 URL 로드
//            loadUrl(getNaverAuthUrl())
//        }
//    }
//
//    private fun getNaverAuthUrl(): String {
//        return "https://nid.naver.com/oauth2.0/authorize?response_type=code" +
//                "&client_id=$NAVER_CLIENT_ID" +
//                "&redirect_uri=$NAVER_REDIRECT_URI" +
//                "&state=random_state"
//    }
//
//    private suspend fun handleNaverAuthCode(authCode: String) {
//        try {
//            val response = withContext(Dispatchers.IO) {
//                RetrofitClient.naverLoginApi.getNaverAccessToken(
//                    clientId = NAVER_CLIENT_ID,
//                    clientSecret = "YOUR_NAVER_CLIENT_SECRET",
//                    code = authCode,
//                    grantType = "authorization_code",
//                    state = "random_state"
//                ).awaitResponse()
//            }
//
//            if (response.isSuccessful) {
//                val responseBody = response.body()?.string()
//                Log.d("NAVER_LOGIN", "네이버 로그인 성공: $responseBody")
//
//                responseBody?.let {
//                    val gson = Gson()
//                    val loginResponse = gson.fromJson(it, NaverLoginResponse::class.java)
//
//                    loginResponse.success?.accessToken?.let { accessToken ->
//                        navigateToMain(accessToken)
//                    } ?: run {
//                        Toast.makeText(this, "네이버 로그인 실패: 토큰 없음", Toast.LENGTH_SHORT).show()
//                    }
//                }
//            } else {
//                Toast.makeText(this, "네이버 로그인 실패: ${response.errorBody()?.string()}", Toast.LENGTH_SHORT).show()
//            }
//        } catch (e: Exception) {
//            Log.e("NAVER_LOGIN", "네트워크 오류 발생: ${e.message}", e)
//            Toast.makeText(this, "네트워크 오류 발생", Toast.LENGTH_SHORT).show()
//        }
//    }
//
//    private fun navigateToMain(accessToken: String) {
//        val intent = Intent(this, MainActivity::class.java).apply {
//            putExtra("ACCESS_TOKEN", accessToken)
//            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//        }
//        startActivity(intent)
//        finish()
//    }
//}
//
