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

class SurveyYearFragment : Fragment() {
    private lateinit var nextButton: Button
    private lateinit var previousButton: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var yearEditText: EditText
    private var progressValue = 60  // 이전 단계에서 증가된 값 유지
    private val viewModel: SurveyViewModel by activityViewModels() // ✅ ViewModel 연동

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_survey_year, container, false)

        // "출생 연도" 부분을 주황색으로 변경
        val textView: TextView = view.findViewById(R.id.textView)
        val fullText = "출생 연도를 입력해 주세요!"
        val spannable = SpannableString(fullText)
        val startIndex = fullText.indexOf("출생 연도")
        val endIndex = startIndex + "출생 연도".length
        spannable.setSpan(
            ForegroundColorSpan(Color.parseColor("#FF7300")),
            startIndex, endIndex,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        textView.text = spannable

        // UI 요소 초기화
        progressBar = view.findViewById(R.id.progressBar)
        progressBar.progress = progressValue
        yearEditText = view.findViewById(R.id.year_editText)
        nextButton = view.findViewById(R.id.next_button)
        previousButton = view.findViewById(R.id.previous_button)

        // 초기 상태에서 "다음" 버튼 비활성화
        nextButton.isEnabled = false
        nextButton.setBackgroundColor(Color.parseColor("#CDCDCD"))

        // EditText 입력 감지 → 글씨 색상 변경 & 다음 버튼 활성화
        yearEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s?.isNotEmpty() == true) {
                    yearEditText.setTextColor(Color.parseColor("#A4A4A4")) // 글씨 색상 변경 (#A4A4A4)
                } else {
                    yearEditText.setTextColor(Color.parseColor("#CDCDCD")) // 기본 색상 (#CDCDCD)
                }

                // 출생 연도가 4자리일 때만 "다음" 버튼 활성화
                if (s != null) {
                    if (s.length == 4) {
                        nextButton.isEnabled = true
                        nextButton.setBackgroundColor(Color.parseColor("#FF7300"))
                    } else {
                        nextButton.isEnabled = false
                        nextButton.setBackgroundColor(Color.parseColor("#CDCDCD"))
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        // "다음 버튼" 클릭 시 ProgressBar 증가 및 SurveyHeightWeightFragment로 이동
        nextButton.setOnClickListener {
            saveBirthYearToViewModel() // ✅ 출생 연도를 ViewModel에 저장
            updateProgressBar()
            goToSurveyHeightWeightFragment()
        }

        // "이전 버튼" 클릭 시 SurveyPeopleFragment로 이동
        previousButton.setOnClickListener {
            goToSurveyPeopleFragment()
        }

        return view
    }

    // ✅ 출생 연도를 ViewModel에 저장하는 메서드
    private fun saveBirthYearToViewModel() {
        val birthYear = yearEditText.text.toString().toIntOrNull() ?: return
        viewModel.updateSurveyData(viewModel.surveyData.value!!.copy(birthYear = birthYear))
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
    private fun goToSurveyHeightWeightFragment() {
        val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.survey_container, SurveyHeightWeightFragment())
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }

    // 이전 페이지
    private fun goToSurveyPeopleFragment() {
        val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.survey_container, SurveyPeopleFragment())
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }
}
