package com.example.umc.Survey

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import androidx.fragment.app.activityViewModels
import com.example.umc.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton

class SurveyDiseaseBottomSheetFragment(private val onSelectionDone: (List<String>) -> Unit) : BottomSheetDialogFragment() {

    private val selectedDiseaseButtons = mutableSetOf<MaterialButton>()
    private lateinit var nextButton: Button
    private lateinit var previousButton: Button

    private val viewModel: SurveyViewModel by activityViewModels() // ✅ ViewModel 연동

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_survey_disease_bottom_sheet, container, false)

        // 지병 선택 버튼들
        val diseaseButtons = listOf(
            view.findViewById<MaterialButton>(R.id.disease_diabetes),
            view.findViewById<MaterialButton>(R.id.disease_high),
            view.findViewById<MaterialButton>(R.id.disease_cancer),
            view.findViewById<MaterialButton>(R.id.disease_digestive)
        )

        nextButton = view.findViewById(R.id.next_button)
        previousButton = view.findViewById(R.id.previous_button)

        val closeButton = view.findViewById<ImageButton>(R.id.survey_x_button) // 닫기 버튼

        // 초기 상태에서 "다음" 버튼 비활성화
        nextButton.isEnabled = false
        nextButton.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#CDCDCD"))

        // 지병 선택 버튼 이벤트
        for (button in diseaseButtons) {
            button.setOnClickListener {
                toggleDiseaseButton(button)
            }
        }

        // "이전" 버튼 클릭 시 닫기
        previousButton.setOnClickListener {
            dismiss()
            goToSurveyAllergyFragment()
        }

        // "다음" 버튼 클릭 시 선택한 지병을 리스트로 전달
        nextButton.setOnClickListener {
            val selectedDiseases = selectedDiseaseButtons.map { it.text.toString() }
            onSelectionDone(selectedDiseases) // ✅ 리스트 전달
            dismiss()
            goToSurveyPeopleFragment()
        }

        // "X(닫기)" 버튼 클릭 시 닫기
        closeButton.setOnClickListener {
            dismiss()
            goToSurveyDiseaseFragment()
        }

        return view
    }

    // 버튼 선택/해제 기능
    private fun toggleDiseaseButton(button: MaterialButton) {
        if (selectedDiseaseButtons.contains(button)) {
            button.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#F0F0F0"))
            button.setTextColor(Color.parseColor("#9A9A9A"))
            button.strokeColor = ColorStateList.valueOf(Color.parseColor("#F0F0F0"))
            selectedDiseaseButtons.remove(button)
        } else {
            button.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#FFEAD9"))
            button.setTextColor(Color.parseColor("#FF7300"))
            button.strokeColor = ColorStateList.valueOf(Color.parseColor("#FF7300"))
            selectedDiseaseButtons.add(button)
        }

        // "다음 버튼" 활성화/비활성화
        updateNextButtonState()
    }

    private fun updateNextButtonState() {
        nextButton.isEnabled = selectedDiseaseButtons.isNotEmpty()
        nextButton.backgroundTintList = if (selectedDiseaseButtons.isNotEmpty())
            ColorStateList.valueOf(Color.parseColor("#FF7300"))
        else
            ColorStateList.valueOf(Color.parseColor("#CDCDCD"))
    }

    // SurveyPeopleFragment로 이동
    private fun goToSurveyPeopleFragment() {
        val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.survey_container, SurveyPeopleFragment())
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }

    // SurveyAllergyFragment로 이동
    private fun goToSurveyAllergyFragment() {
        val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.survey_container, SurveyAllergyFragment())
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }

    // SurveyDiseaseFragment로 이동 (X 버튼 클릭 시)
    private fun goToSurveyDiseaseFragment() {
        val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.survey_container, SurveyDiseaseFragment())
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }
}
