package com.example.umc.Splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.umc.Main.MainActivity
import com.example.umc.Onboarding.OnboardingMainActivity
import com.example.umc.Signin.LoginActivity

class SplashActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // 스플래시 스크린 설치는 반드시 super.onCreate 전에
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        // 스플래시 화면 유지를 위한 변수
        var keepSplashOnScreen = true

        // 스플래시 화면 유지 조건 설정
        splashScreen.setKeepOnScreenCondition { keepSplashOnScreen }

        // 로딩 작업을 시뮬레이션
        Handler(Looper.getMainLooper()).postDelayed({
            keepSplashOnScreen = false
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }, 3000) // 3초 딜레이
    }
}

// 여기 위에 코드 원본 코드라 절대로 건들지 말아주세요 !!

//
//package com.example.umc.Splash
//
//import android.content.Intent
//import android.os.Bundle
//import android.os.Handler
//import android.os.Looper
//import androidx.activity.ComponentActivity
//import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
//import com.example.umc.Main.MainActivity
//// MainActivity import 추가
//
//class SplashActivity : ComponentActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        val splashScreen = installSplashScreen()
//        super.onCreate(savedInstanceState)
//
//        var keepSplashOnScreen = true
//        splashScreen.setKeepOnScreenCondition { keepSplashOnScreen }
//
//        Handler(Looper.getMainLooper()).postDelayed({
//            keepSplashOnScreen = false
//            // OnboardingMainActivity 대신 MainActivity로 변경
//            startActivity(Intent(this, MainActivity::class.java))
//            finish()
//        }, 3000)
//    }
//}
