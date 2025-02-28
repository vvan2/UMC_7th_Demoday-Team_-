package com.example.umc.Onboarding

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.UnderlineSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.umc.R
import com.example.umc.Signin.LoginActivity

class OnboardingMainFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_onboardingmain, container, false)

        // 시작 버튼 설정
        val startButton: Button = view.findViewById(R.id.startButton)
        startButton.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, OnboardingDietFragment())
                .addToBackStack(null)
                .commit()
        }

        // 로그인 버튼 설정 (굵은 밑줄 적용)
        val loginButton: Button = view.findViewById(R.id.loginButton)
        val loginText = "로그인"
        val spannableString = SpannableString(loginText)

        // 기본 밑줄 적용
        spannableString.setSpan(UnderlineSpan(), 0, loginText.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        // 추가적인 밑줄 적용 (겹쳐서 더 두껍게 보이도록 함)
        spannableString.setSpan(UnderlineSpan(), 0, loginText.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        // 텍스트 색상 적용
        spannableString.setSpan(ForegroundColorSpan(Color.parseColor("#FF7300")), 0, loginText.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        loginButton.text = spannableString

        // 로그인 버튼 클릭 시 LoginActivity로 이동
        loginButton.setOnClickListener {
            val intent = Intent(requireContext(), LoginActivity::class.java)
            startActivity(intent)
        }

        return view
    }
}
