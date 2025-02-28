package com.example.umc.Onboarding

import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.umc.R

class OnboardingDietFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_onboarding_diet, container, false)

        val nextButton: Button = view.findViewById(R.id.nextButton)
        val backButton: ImageButton = view.findViewById(R.id.backButton)
        val dietTextView: TextView = view.findViewById(R.id.textView3)
        val dotsLayout: LinearLayout? = view.findViewById(R.id.dotsLayout)

        // "이거먹자 식단" 텍스트에서 "식단" 부분만 색상 변경
        val fullText = "이거먹자 식단"
        val spannableString = SpannableString(fullText)
        val colorSpan = ForegroundColorSpan(Color.parseColor("#FF7300"))
        val startIndex = fullText.indexOf("식단")

        if (startIndex != -1) {
            spannableString.setSpan(colorSpan, startIndex, startIndex + 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        dietTextView.text = spannableString

        // 현재 프래그먼트에 해당하는 점을 활성화
        dotsLayout?.let { updateDots(it, 0) }

        // "다음" 버튼 클릭 시 배경색 변경 후 OnboardingSubscribeFragment로 이동
        nextButton.setOnClickListener {
            nextButton.setBackgroundColor(Color.parseColor("#9A9A9A")) // 배경색 변경
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, OnboardingPriceFragment())
                .addToBackStack(null)
                .commit()
        }

        // "뒤로 가기" 버튼 클릭 시 OnboardingMainFragment로 이동 (Activity 다시 실행 X)
        backButton.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, OnboardingMainFragment()) // 기존 Fragment로 돌아감
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
                dot.setBackgroundResource(R.drawable.dot_active)
            } else {
                dot.setBackgroundResource(R.drawable.dot_inactive)
            }
        }
    }
}
