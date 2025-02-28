package com.example.umc.Survey

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
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

class SurveyMealFragment : Fragment() {
    private lateinit var nextButton: Button
    private lateinit var previousButton: Button
    private val selectedButtons = mutableSetOf<MaterialButton>()
    private lateinit var progressBar: ProgressBar
    private var progressValue = 10  // SurveyGoalFragment에서 증가된 값 유지

    private val viewModel: SurveyViewModel by activityViewModels() // ✅ ViewModel 연결

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_survey_meal, container, false)

        // "식사" 부분만 주황색으로 변경
        val textView: TextView = view.findViewById(R.id.textView)
        val fullText = "제공받고자 하는 식사를 선택해주세요!"
        val spannable = SpannableString(fullText)
        val startIndex = fullText.indexOf("식사")
        val endIndex = startIndex + "식사".length
        spannable.setSpan(
            ForegroundColorSpan(Color.parseColor("#FF7300")),
            startIndex, endIndex,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        textView.text = spannable

        // ProgressBar 가져오기
        progressBar = view.findViewById(R.id.progressBar)
        progressBar.progress = progressValue

        // 버튼 목록
        val goalButtons = listOf(
            view.findViewById<MaterialButton>(R.id.morning_button),
            view.findViewById<MaterialButton>(R.id.lunch_button),
            view.findViewById<MaterialButton>(R.id.dinner_button)
        )

        nextButton = view.findViewById(R.id.next_button)
        previousButton = view.findViewById(R.id.previous_button)

        // 모든 버튼에 클릭 이벤트 추가
        for (button in goalButtons) {
            button.setOnClickListener {
                toggleButtonState(button)
            }
        }

        // "다음 버튼" 클릭 시 ProgressBar 증가 및 SurveyAllergyFragment로 이동
        nextButton.setOnClickListener {
            if (selectedButtons.isNotEmpty()) {
                saveMealsToViewModel() // ✅ ViewModel에 데이터 저장
                updateProgressBar()
                goToSurveyAllergyFragment()
            } else {
                Toast.makeText(requireContext(), "하나 이상의 식사를 선택해주세요.", Toast.LENGTH_SHORT).show()
            }
        }

        // "이전 버튼" 클릭 시 SurveyGoalFragment로 이동
        previousButton.setOnClickListener {
            goToSurveyGoalFragment()
        }

        return view
    }

    // ✅ 선택한 식사 정보를 ViewModel에 저장
    private fun saveMealsToViewModel() {
        val selectedMeals = selectedButtons.map { it.text.toString() } // 선택한 버튼의 텍스트 리스트
        viewModel.updateSurveyData(viewModel.surveyData.value!!.copy(meals = selectedMeals)) // ✅ ViewModel에 저장
    }

    private fun updateProgressBar() {
        if (progressValue < 100) {
            progressValue += 10  // 진행도를 10씩 증가
            progressBar.progress = progressValue  // ProgressBar 즉시 업데이트
            setProgressWithAnimation(progressBar, progressValue)
        }
    }

    private fun setProgressWithAnimation(progressBar: ProgressBar, progress: Int) {
        val animator = ObjectAnimator.ofInt(progressBar, "progress", progressBar.progress, progress)
        animator.duration = 500 // 0.5초 동안 애니메이션
        animator.start()
    }

    // 버튼 클릭 시 토글 (선택/해제)
    private fun toggleButtonState(button: MaterialButton) {
        if (selectedButtons.contains(button)) {
            // 선택 해제 시
            button.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#F0F0F0")) // 기본 배경
            button.setTextColor(Color.parseColor("#9A9A9A")) // 기본 글씨 색
            button.strokeColor = ColorStateList.valueOf(Color.parseColor("#F0F0F0")) // 기본 테두리
            selectedButtons.remove(button)
        } else {
            // 버튼 선택 시
            button.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#FFEAD9")) // 선택된 배경
            button.setTextColor(Color.parseColor("#FF7300")) // 선택된 글씨 색
            button.strokeColor = ColorStateList.valueOf(Color.parseColor("#FF7300")) // 선택된 테두리
            selectedButtons.add(button)
        }

        // 다음 버튼 상태 업데이트
        updateNextButtonState()
    }

    // "다음 버튼" 활성화 여부 설정
    private fun updateNextButtonState() {
        nextButton.isEnabled = selectedButtons.isNotEmpty()
        nextButton.backgroundTintList = ColorStateList.valueOf(
            if (selectedButtons.isNotEmpty()) Color.parseColor("#FF7300") else Color.parseColor("#CDCDCD")
        )
    }

    // 이전 버튼 클릭 시
    private fun goToSurveyGoalFragment() {
        val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.survey_container, SurveyGoalFragment())
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }

    // 다음 버튼 클릭 시
    private fun goToSurveyAllergyFragment() {
        val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.survey_container, SurveyAllergyFragment())
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }
}
