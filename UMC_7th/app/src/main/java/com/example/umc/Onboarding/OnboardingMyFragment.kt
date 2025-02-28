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
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.umc.R
import com.example.umc.SignUp.SignUpFragment
import com.example.umc.Signin.LoginActivity

class OnboardingMyFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_onboarding_my, container, false)

        val nextButton: Button = view.findViewById(R.id.nextButton)
        val loginButton: Button = view.findViewById(R.id.loginButton)
        val backButton: ImageButton = view.findViewById(R.id.backButton)
        val myTextView: TextView = view.findViewById(R.id.textView3) // "이거먹자 마이"
        val dotsLayout: LinearLayout = requireActivity().findViewById(R.id.dotsLayout) // 점 레이아웃

        // "이거먹자 마이" 텍스트에서 "마이" 부분만 색상 변경
        val fullText = "이거먹자 마이"
        val spannableString = SpannableString(fullText)
        val colorSpan = ForegroundColorSpan(Color.parseColor("#FF7300")) // "마이" 색상 변경
        val startIndex = fullText.indexOf("마이") // "마이" 시작 위치 찾기

        if (startIndex != -1) {
            spannableString.setSpan(colorSpan, startIndex, startIndex + 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        myTextView.text = spannableString

        // "로그인" 버튼 텍스트에 굵은 밑줄과 색상 적용
        val loginText = "로그인"
        val loginSpannable = SpannableString(loginText)

        // 기본 밑줄 적용
        loginSpannable.setSpan(UnderlineSpan(), 0, loginText.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        // 추가적인 밑줄 적용 (겹쳐서 더 두껍게 보이도록 함)
        loginSpannable.setSpan(UnderlineSpan(), 0, loginText.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        // 텍스트 색상 적용
        loginSpannable.setSpan(ForegroundColorSpan(Color.parseColor("#FF7300")), 0, loginText.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        loginButton.text = loginSpannable

        // 현재 프래그먼트에 해당하는 점을 활성화
        updateDots(dotsLayout, 4)

        // "회원가입" 버튼 클릭 시 SignUpFragment로 이동
//        nextButton.setOnClickListener {
//            nextButton.setBackgroundColor(Color.parseColor("#FF7300")) // 배경색 변경
//            parentFragmentManager.beginTransaction()
//                .replace(R.id.fragment_container, SignUpFragment())
//                .addToBackStack(null)
//                .commit()
//        }
        // 확인 버튼 클릭 시
        nextButton.setOnClickListener {
            nextButton.setBackgroundColor(Color.parseColor("#FF7300")) // 배경색 변경
            val intent = Intent(requireContext(), LoginActivity::class.java)
            startActivity(intent)
        }


        // "로그인" 버튼 클릭 시 LoginActivity로 이동
        loginButton.setOnClickListener {
            val intent = Intent(requireContext(), LoginActivity::class.java)
            startActivity(intent)
        }

        // "뒤로 가기" 버튼 클릭 시 OnboardingSubscribeFragment로 이동
        backButton.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, OnboardingSubscribeFragment()) // 이전 프래그먼트로 이동
                .addToBackStack(null)
                .commit()
        }

        return view
    }

    // 점 업데이트 함수
    private fun updateDots(dotsLayout: LinearLayout, activeIndex: Int) {
        for (i in 0 until dotsLayout.childCount) {
            val dot = dotsLayout.getChildAt(i)
            if (i == activeIndex) {
                dot.setBackgroundResource(R.drawable.dot_active) // 활성화된 점
            } else {
                dot.setBackgroundResource(R.drawable.dot_inactive) // 비활성화된 점
            }
        }
    }
}
