package com.example.umc.Survey

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.Spannable
import android.text.SpannableString
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.umc.R

class SurveyGoalWeightFragment : Fragment() {
    private lateinit var nextButton: Button
    private lateinit var previousButton: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var goalWeightEditText: EditText
    private var progressValue = 80  // 이전 단계에서 증가된 값 유지

    private val viewModel: SurveyViewModel by activityViewModels() // ✅ ViewModel 연동

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_survey_goal_weight, container, false)

        // "목표 체중" 부분을 주황색으로 변경
        val textView: TextView = view.findViewById(R.id.textView)
        val fullText = "목표 체중을 입력해 주세요!"
        val spannable = SpannableString(fullText)
        val startIndex = fullText.indexOf("목표 체중")
        val endIndex = startIndex + "목표 체중".length
        spannable.setSpan(
            ForegroundColorSpan(Color.parseColor("#FF7300")),
            startIndex, endIndex,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        textView.text = spannable

        // UI 요소 초기화
        progressBar = view.findViewById(R.id.progressBar)
        progressBar.progress = progressValue
        goalWeightEditText = view.findViewById(R.id.goal_weight_editText)
        nextButton = view.findViewById(R.id.next_button)
        previousButton = view.findViewById(R.id.previous_button)

        // 초기 상태에서 "다음" 버튼 비활성화
        nextButton.isEnabled = false
        nextButton.setBackgroundColor(Color.parseColor("#CDCDCD"))

        // EditText 입력 감지 → 글씨 색상 변경 & 다음 버튼 활성화
        goalWeightEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                validateInput() // 입력값이 변경될 때마다 검사
            }

            override fun afterTextChanged(s: Editable?) {
                s?.let {
                    goalWeightEditText.setTextColor(Color.parseColor("#A4A4A4")) // 입력 시 글씨 색 변경
                }
            }
        })

        // "다음 버튼" 클릭 시 ProgressBar 증가 및 SurveyBmiFragment로 이동
        nextButton.setOnClickListener {
            saveGoalWeightToViewModel() // ✅ 목표 체중을 ViewModel에 저장 (추가된 코드)
            updateProgressBar()
            goToSurveyBmiFragment()
        }

        // "이전 버튼" 클릭 시 SurveyHeightWeightFragment로 이동
        previousButton.setOnClickListener {
            goToSurveyHeightWeightFragment()
        }

        return view
    }

    // ✅ 목표 체중을 ViewModel에 저장하는 코드 (추가된 부분)
    private fun saveGoalWeightToViewModel() {
        val goalWeight = goalWeightEditText.text.toString().toIntOrNull() ?: return
        viewModel.updateSurveyData(viewModel.surveyData.value!!.copy(targetWeight = goalWeight))
    }

    private fun validateInput() {
        val goalWeightInput = goalWeightEditText.text.toString().trim()

        if (goalWeightInput.isNotEmpty()) {
            nextButton.isEnabled = true
            nextButton.setBackgroundColor(Color.parseColor("#FF7300"))
        } else {
            nextButton.isEnabled = false
            nextButton.setBackgroundColor(Color.parseColor("#CDCDCD"))
        }
    }

    private fun updateProgressBar() {
        if (progressValue < 100) {
            progressValue += 10
            progressBar.progress = progressValue
            setProgressWithAnimation(progressBar, progressValue)
        }
    }

    private fun setProgressWithAnimation(progressBar: ProgressBar, progress: Int) {
        val animator = ObjectAnimator.ofInt(progressBar, "progress", progressBar.progress, progress)
        animator.duration = 500
        animator.start()
    }

    // 다음 페이지
    private fun goToSurveyBmiFragment() {
        val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.survey_container, SurveyBmiFragment())
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }

    // 이전 페이지
    private fun goToSurveyHeightWeightFragment() {
        val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.survey_container, SurveyHeightWeightFragment())
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }
}
