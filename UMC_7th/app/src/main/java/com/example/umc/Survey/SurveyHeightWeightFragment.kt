package com.example.umc.Survey

import android.animation.ObjectAnimator
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

class SurveyHeightWeightFragment : Fragment() {
    private lateinit var nextButton: Button
    private lateinit var previousButton: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var heightEditText: EditText
    private lateinit var weightEditText: EditText
    private var progressValue = 70  // SurveyYearFragment에서 증가된 값 유지
    private val viewModel: SurveyViewModel by activityViewModels() // ✅ ViewModel 연동

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_survey_height_weight, container, false)

        // "키, 체중" 부분을 주황색으로 변경
        val textView: TextView = view.findViewById(R.id.textView)
        val fullText = "키와 체중을 입력해 주세요!"
        val spannable = SpannableString(fullText)
        val startIndex = fullText.indexOf("키")
        val endIndex = startIndex + "키".length
        spannable.setSpan(
            ForegroundColorSpan(Color.parseColor("#FF7300")),
            startIndex, endIndex,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        val startIndexWeight = fullText.indexOf("체중")
        val endIndexWeight = startIndexWeight + "체중".length
        spannable.setSpan(
            ForegroundColorSpan(Color.parseColor("#FF7300")),
            startIndexWeight, endIndexWeight,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        textView.text = spannable

        // UI 요소 초기화
        progressBar = view.findViewById(R.id.progressBar)
        progressBar.progress = progressValue
        heightEditText = view.findViewById(R.id.height_editText)
        weightEditText = view.findViewById(R.id.weight_editText)
        nextButton = view.findViewById(R.id.next_button)
        previousButton = view.findViewById(R.id.previous_button)

        // 초기 상태에서 "다음" 버튼 비활성화
        nextButton.isEnabled = false
        nextButton.setBackgroundColor(Color.parseColor("#CDCDCD"))

        // 두 개의 EditText 입력 감지 → 다음 버튼 활성화
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                validateInputs() // 입력값이 변경될 때마다 검사
            }

            override fun afterTextChanged(s: Editable?) {
                s?.let {
                    // 입력할 때 글씨 색을 #A4A4A4(회색)으로 변경
                    heightEditText.setTextColor(Color.parseColor("#A4A4A4"))
                    weightEditText.setTextColor(Color.parseColor("#A4A4A4"))
                }
            }
        }

        heightEditText.addTextChangedListener(textWatcher)
        weightEditText.addTextChangedListener(textWatcher)

        // "다음 버튼" 클릭 시 ProgressBar 증가 및 SurveyGoalWeightFragment로 이동
        nextButton.setOnClickListener {
            saveHeightWeightToViewModel() // ✅ 키와 체중을 ViewModel에 저장
            updateProgressBar()
            goToSurveyGoalWeightFragment()
        }

        // "이전 버튼" 클릭 시 SurveyYearFragment로 이동
        previousButton.setOnClickListener {
            goToSurveyYearFragment()
        }

        return view
    }

    // ✅ 키와 체중을 ViewModel에 저장하는 메서드
    private fun saveHeightWeightToViewModel() {
        val height = heightEditText.text.toString().toIntOrNull() ?: return
        val weight = weightEditText.text.toString().toIntOrNull() ?: return

        viewModel.updateSurveyData(viewModel.surveyData.value!!.copy(height = height, currentWeight = weight))
    }

    // 입력값 확인 함수
    private fun validateInputs() {
        val heightInput = heightEditText.text.toString().trim()
        val weightInput = weightEditText.text.toString().trim()

        // 두 개의 EditText가 모두 입력되었는지 확인
        if (heightInput.isNotEmpty() && weightInput.isNotEmpty()) {
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
    private fun goToSurveyGoalWeightFragment() {
        val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.survey_container, SurveyGoalWeightFragment())
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }

    // 이전 페이지
    private fun goToSurveyYearFragment() {
        val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.survey_container, SurveyYearFragment())
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }
}
