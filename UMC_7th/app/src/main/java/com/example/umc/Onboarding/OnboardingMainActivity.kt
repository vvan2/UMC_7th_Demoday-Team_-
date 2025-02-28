package com.example.umc.Onboarding

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.umc.R

class OnboardingMainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboardingmain)

        // 앱 실행 시 첫 번째 프래그먼트를 로드 (중복 방지)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, OnboardingMainFragment()) // OnboardingMainFragment 표시
                .commit()
        }
    }
}
