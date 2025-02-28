package com.example.umc.Survey

import android.animation.ObjectAnimator
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.umc.R
import com.google.android.material.button.MaterialButton

class SurveyPeopleFragment : Fragment() {
    private lateinit var nextButton: Button
    private lateinit var previousButton: Button
    private var selectedButton: MaterialButton? = null  // 단 하나의 버튼만 선택 가능
    private lateinit var progressBar: ProgressBar
    private var progressValue = 50  // SurveyDiseaseFragment에서 증가된 값 유지
    private val viewModel: SurveyViewModel by activityViewModels() // ✅ ViewModel 연동

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_survey_people, container, false)

        // "성별" 부분만 주황색으로 변경
        val textView: TextView = view.findViewById(R.id.textView)
        val fullText = "성별을 선택해 주세요!"
        val spannable = SpannableString(fullText)
        val startIndex = fullText.indexOf("성별")
        if (startIndex >= 0) { // 오류 방지
            val endIndex = startIndex + "성별".length
            spannable.setSpan(
                ForegroundColorSpan(Color.parseColor("#FF7300")),
                startIndex, endIndex,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        textView.text = spannable

        // ProgressBar 설정
        progressBar = view.findViewById(R.id.progressBar)
        progressBar.progress = progressValue

        // 버튼 설정 (남성, 여성 중 하나만 선택 가능)
        val manButton = view.findViewById<MaterialButton>(R.id.man_button)
        val womanButton = view.findViewById<MaterialButton>(R.id.woman_button)

        nextButton = view.findViewById(R.id.next_button)
        previousButton = view.findViewById(R.id.previous_button)

        // 버튼 클릭 이벤트 추가 (하나만 선택 가능)
        manButton.setOnClickListener { toggleSingleSelection(manButton) }
        womanButton.setOnClickListener { toggleSingleSelection(womanButton) }

        // "다음 버튼" 클릭 시 선택 여부 확인 후 이동
        nextButton.setOnClickListener {
            if (selectedButton != null) {
                saveGenderToViewModel() // ✅ 선택한 성별을 ViewModel에 저장
                updateProgressBar()
                goToSurveyYearFragment()
            } else {
                Toast.makeText(requireContext(), "성별을 선택해 주세요.", Toast.LENGTH_SHORT).show()
            }
        }

        // "이전 버튼" 클릭 시 SurveyDiseaseFragment로 이동
        previousButton.setOnClickListener {
            goToSurveyDiseaseFragment()
        }

        return view
    }

    // ✅ 선택한 성별을 ViewModel에 저장하는 메서드
    private fun saveGenderToViewModel() {
        val selectedGender = selectedButton?.text.toString()
        viewModel.updateSurveyData(viewModel.surveyData.value!!.copy(gender = selectedGender))
    }

    // 버튼 하나만 선택되도록 설정
    private fun toggleSingleSelection(button: MaterialButton) {
        // 기존에 선택된 버튼이 있다면 원래 상태로 되돌림
        selectedButton?.let {
            it.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#F0F0F0")) // 기본 배경
            it.setTextColor(Color.parseColor("#9A9A9A")) // 기본 글씨 색
            it.strokeColor = ColorStateList.valueOf(Color.parseColor("#F0F0F0")) // 기본 테두리
        }

        // 현재 클릭된 버튼 선택
        if (selectedButton == button) {
            // 동일한 버튼 클릭 시 선택 해제
            selectedButton = null
        } else {
            selectedButton = button
            button.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#FFEAD9")) // 선택된 배경
            button.setTextColor(Color.parseColor("#FF7300")) // 선택된 글씨 색
            button.strokeColor = ColorStateList.valueOf(Color.parseColor("#FF7300")) // 선택된 테두리
        }

        // "다음 버튼" 활성화/비활성화
        updateNextButtonState()
    }

    // "다음 버튼" 상태 업데이트 (하나 이상 선택 시 활성화)
    private fun updateNextButtonState() {
        nextButton.isEnabled = selectedButton != null
        nextButton.backgroundTintList = ColorStateList.valueOf(
            if (selectedButton != null) Color.parseColor("#FF7300") else Color.parseColor("#CDCDCD")
        )
    }

    // ProgressBar 증가 애니메이션 적용
    private fun updateProgressBar() {
        if (progressValue < 100) {
            progressValue += 10  // 진행도 10 증가
            setProgressWithAnimation(progressBar, progressValue)
        }
    }

    private fun setProgressWithAnimation(progressBar: ProgressBar, progress: Int) {
        val animator = ObjectAnimator.ofInt(progressBar, "progress", progressBar.progress, progress)
        animator.duration = 500 // 0.5초 동안 애니메이션 적용
        animator.start()
    }

    // SurveyYearFragment로 이동
    private fun goToSurveyYearFragment() {
        val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.survey_container, SurveyYearFragment())
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }

    // SurveyDiseaseFragment로 이동
    private fun goToSurveyDiseaseFragment() {
        val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.survey_container, SurveyDiseaseFragment())
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }
}
