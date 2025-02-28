package com.example.umc.Survey

import android.animation.ObjectAnimator
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
import com.example.umc.AnimationFragment
import com.example.umc.Main.MainActivity
import com.example.umc.R
import com.google.android.material.button.MaterialButton

class SurveyWorkFragment : Fragment() {
    private lateinit var nextButton: Button
    private lateinit var previousButton: Button
    private lateinit var progressBar: ProgressBar
    private var progressValue = 100
    private var selectedWorkButton: MaterialButton? = null
    private var selectedExercise: String? = null
    private var handler: Handler? = null

    private val viewModel: SurveyViewModel by activityViewModels() // ✅ ViewModel 연동 추가

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handler = Handler(Looper.getMainLooper())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_survey_work, container, false)

        setupTextView(view)
        setupProgressBar(view)
        setupButtons(view)

        return view
    }

    private fun setupTextView(view: View) {
        val textView: TextView = view.findViewById(R.id.textView)
        val fullText = "현재 하시는 일과 운동 횟수를 알려주세요!"
        val spannable = SpannableString(fullText)

        fullText.indexOf("일").let { startIndex ->
            if (startIndex >= 0) {
                spannable.setSpan(
                    ForegroundColorSpan(Color.parseColor("#FF7300")),
                    startIndex,
                    startIndex + 1,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
        }

        fullText.indexOf("운동 횟수").let { startIndex ->
            if (startIndex >= 0) {
                spannable.setSpan(
                    ForegroundColorSpan(Color.parseColor("#FF7300")),
                    startIndex,
                    startIndex + "운동 횟수".length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
        }

        textView.text = spannable
    }

    private fun setupProgressBar(view: View) {
        progressBar = view.findViewById(R.id.progressBar)
        progressBar.progress = progressValue
    }

    private fun setupButtons(view: View) {
        val workButtons = listOf(
            view.findViewById<MaterialButton>(R.id.no_work_button),
            view.findViewById<MaterialButton>(R.id.house_work_button),
            view.findViewById<MaterialButton>(R.id.sit_down_button),
            view.findViewById<MaterialButton>(R.id.student_button),
            view.findViewById<MaterialButton>(R.id.sit_up_button),
            view.findViewById<MaterialButton>(R.id.activity_work_button)
        )

        nextButton = view.findViewById(R.id.next_button)
        previousButton = view.findViewById(R.id.previous_button)

        initializeButtonStates(workButtons)
    }

    private fun initializeButtonStates(workButtons: List<MaterialButton>) {
        nextButton.isEnabled = false
        nextButton.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#CDCDCD"))

        workButtons.forEach { button ->
            button.setOnClickListener {
                selectSingleWorkButton(button)
                showExerciseBottomSheet()
            }
        }

        nextButton.setOnClickListener {
            if (selectedWorkButton != null && selectedExercise != null) {
                saveWorkDataToViewModel()
            } else {
                Toast.makeText(requireContext(), "하나의 항목을 선택해주세요.", Toast.LENGTH_SHORT).show()
            }
        }

        previousButton.setOnClickListener {
            goToSurveyBmiFragment()
        }
    }

    private fun showExerciseBottomSheet() {
        val exerciseBottomSheet = SurveyExerciseBottomSheetFragment { selected ->
            selectedExercise = selected
            updateNextButtonState()
        }
        exerciseBottomSheet.show(parentFragmentManager, "exercise_bottom_sheet")

        nextButton.isEnabled = false
        nextButton.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#CDCDCD"))
    }

    private fun selectSingleWorkButton(button: MaterialButton) {
        selectedWorkButton?.apply {
            backgroundTintList = ColorStateList.valueOf(Color.parseColor("#F0F0F0"))
            setTextColor(Color.parseColor("#9A9A9A"))
            strokeColor = ColorStateList.valueOf(Color.parseColor("#F0F0F0"))
        }

        if (selectedWorkButton == button) {
            selectedWorkButton = null
        } else {
            selectedWorkButton = button
            button.apply {
                backgroundTintList = ColorStateList.valueOf(Color.parseColor("#FFEAD9"))
                setTextColor(Color.parseColor("#FF7300"))
                strokeColor = ColorStateList.valueOf(Color.parseColor("#FF7300"))
            }

            // ✅ 일을 선택하면 즉시 ViewModel에 저장
            val selectedJob = button.text.toString()
            viewModel.updateSurveyData(viewModel.surveyData.value!!.copy(
                job = selectedJob
            ))
        }

        updateNextButtonState()
    }

    private fun updateNextButtonState() {
        val isEnabled = selectedWorkButton != null && selectedExercise != null
        nextButton.isEnabled = isEnabled
        nextButton.backgroundTintList = ColorStateList.valueOf(
            if (isEnabled) Color.parseColor("#FF7300") else Color.parseColor("#CDCDCD")
        )
    }

    private fun saveWorkDataToViewModel() {
        val selectedJob = selectedWorkButton?.text.toString()
        val exerciseFrequency = selectedExercise?.toIntOrNull() ?: 0

        // ✅ "다음" 버튼을 누를 때 최신 데이터 저장
//        viewModel.updateSurveyData(viewModel.surveyData.value!!.copy(
//            job = selectedJob,
//            //exerciseFrequency = exerciseFrequency
//        ))
    }

    private fun goToSurveyBmiFragment() {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.survey_container, SurveyBmiFragment())
            .addToBackStack(null)
            .commit()
    }

    override fun onDestroy() {
        super.onDestroy()
        handler?.removeCallbacksAndMessages(null)
        handler = null
    }
}
