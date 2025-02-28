package com.example.umc.Survey

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

class SurveyAllergyBottomSheetFragment(private val onSelectionDone: (List<String>) -> Unit) : BottomSheetDialogFragment() {

    private val selectedAllergyButtons = mutableSetOf<MaterialButton>()
    private lateinit var nextButton: Button
    private lateinit var previousButton: Button

    private val viewModel: SurveyViewModel by activityViewModels() // ✅ ViewModel 연동

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_survey_allergy_bottom_sheet, container, false)

        // 알레르기 선택 버튼들
        val allergyButtons = listOf(
            view.findViewById<MaterialButton>(R.id.allergy_grain),
            view.findViewById<MaterialButton>(R.id.allergy_meat),
            view.findViewById<MaterialButton>(R.id.allergy_dairy),
            view.findViewById<MaterialButton>(R.id.allergy_seafood),
            view.findViewById<MaterialButton>(R.id.allergy_nuts),
            view.findViewById<MaterialButton>(R.id.allergy_fruit),
            view.findViewById<MaterialButton>(R.id.allergy_vegetable),
            view.findViewById<MaterialButton>(R.id.allergy_herbs)
        )

        nextButton = view.findViewById(R.id.next_button)
        previousButton = view.findViewById(R.id.previous_button)

        val closeButton = view.findViewById<ImageButton>(R.id.survey_x_button)

        // 초기 상태에서 "다음" 버튼 비활성화
        nextButton.isEnabled = false
        nextButton.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#CDCDCD"))

        // 알레르기 선택 버튼 이벤트
        for (button in allergyButtons) {
            button.setOnClickListener {
                toggleAllergyButton(button)
            }
        }

        // "이전" 버튼 클릭 시 SurveyMealFragment로 이동
        previousButton.setOnClickListener {
            dismiss()  // BottomSheet 닫기
            goToSurveyMealFragment()
        }

        // "다음" 버튼 클릭 시 ViewModel에 저장 후 SurveyDiseaseFragment로 이동
        nextButton.setOnClickListener {
            val selectedAllergies = selectedAllergyButtons.map { it.text.toString() }

            // ✅ 선택된 알레르기 정보를 ViewModel에 저장
            viewModel.updateSurveyData(
                viewModel.surveyData.value!!.copy(
                    allergyDetails = selectedAllergies.joinToString(", ") // 쉼표로 구분
                )
            )

            onSelectionDone(selectedAllergies) // 선택된 목록 전달
            dismiss()  // BottomSheet 닫기
            goToSurveyDiseaseFragment()
        }

        // "X(닫기)" 버튼 클릭 시 SurveyAllergyFragment로 이동
        closeButton.setOnClickListener {
            dismiss() // 현재 BottomSheet 닫기
            goToSurveyAllergyFragment() // SurveyAllergyFragment로 이동
        }

        return view
    }

    // 버튼 선택/해제 기능
    private fun toggleAllergyButton(button: MaterialButton) {
        if (selectedAllergyButtons.contains(button)) {
            // 선택 해제
            button.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#F0F0F0"))
            button.setTextColor(Color.parseColor("#9A9A9A"))
            button.strokeColor = ColorStateList.valueOf(Color.parseColor("#F0F0F0"))
            selectedAllergyButtons.remove(button)
        } else {
            // 선택됨
            button.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#FFEAD9"))
            button.setTextColor(Color.parseColor("#FF7300"))
            button.strokeColor = ColorStateList.valueOf(Color.parseColor("#FF7300"))
            selectedAllergyButtons.add(button)
        }

        // "다음 버튼" 활성화/비활성화
        updateNextButtonState()
    }

    private fun updateNextButtonState() {
        nextButton.isEnabled = selectedAllergyButtons.isNotEmpty()
        nextButton.backgroundTintList = if (selectedAllergyButtons.isNotEmpty())
            ColorStateList.valueOf(Color.parseColor("#FF7300"))
        else
            ColorStateList.valueOf(Color.parseColor("#CDCDCD"))
    }

    // SurveyDiseaseFragment로 이동
    private fun goToSurveyDiseaseFragment() {
        val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.survey_container, SurveyDiseaseFragment())
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }

    // 이전 버튼 클릭 시
    private fun goToSurveyMealFragment() {
        val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.survey_container, SurveyMealFragment())
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }

    // SurveyAllergyFragment로 이동 (X 버튼 클릭 시)
    private fun goToSurveyAllergyFragment() {
        val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.survey_container, SurveyAllergyFragment())
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }
}
