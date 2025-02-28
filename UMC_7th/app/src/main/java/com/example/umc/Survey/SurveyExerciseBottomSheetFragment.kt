package com.example.umc.Survey

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.example.umc.AnimationFragment
import com.example.umc.Main.MainActivity
import com.example.umc.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton

class SurveyExerciseBottomSheetFragment(private val onSelectionDone: (String?) -> Unit) : BottomSheetDialogFragment() {

    private var selectedExerciseButton: MaterialButton? = null
    private lateinit var nextButton: Button
    private lateinit var previousButton: Button

    private val viewModel: SurveyViewModel by activityViewModels() // ✅ ViewModel 연동


    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_survey_exercise_bottom_sheet, container, false)

        // 운동 횟수 선택 버튼들
        val exerciseButtons = listOf(
            view.findViewById<MaterialButton>(R.id.exercise_none),
            view.findViewById<MaterialButton>(R.id.exercise_1),
            view.findViewById<MaterialButton>(R.id.exercise_2),
            view.findViewById<MaterialButton>(R.id.exercise_3),
            view.findViewById<MaterialButton>(R.id.exercise_4),
            view.findViewById<MaterialButton>(R.id.exercise_5),
            view.findViewById<MaterialButton>(R.id.exercise_6),
            view.findViewById<MaterialButton>(R.id.exercise_7)
        )

        nextButton = view.findViewById(R.id.next_button)
        previousButton = view.findViewById(R.id.previous_button)

        val closeButton = view.findViewById<ImageButton>(R.id.survey_x_button)

        // 초기 상태에서 "다음" 버튼 비활성화
        nextButton.isEnabled = false
        nextButton.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#CDCDCD"))

        // 운동 횟수 선택 버튼 이벤트
        for (button in exerciseButtons) {
            button.setOnClickListener {
                selectSingleExerciseButton(button)
            }
        }

        // "이전" 버튼 클릭 시 BottomSheet 닫기
        previousButton.setOnClickListener {
            dismiss()  // SurveyWorkFragment에서 처리
            goToSurveyBmiFragment()
        }

        // "다음" 버튼 클릭 시 운동 횟수 저장 후 서버 전송
        nextButton.setOnClickListener {
            if (selectedExerciseButton != null) {
                val exerciseCount = selectedExerciseButton!!.text.toString().toIntOrNull() ?: 0

                // ✅ 최신 데이터 저장 후 서버 전송
                viewModel.updateSurveyData(viewModel.surveyData.value!!.copy(
                    exerciseFrequency = exerciseCount
                ))

                // ✅ 최신 ViewModel 데이터를 반영한 후 서버에 전송
                sendSurveyDataToServer()
            }
        }

        // "X(닫기)" 버튼 클릭 시 SurveyWorkFragment로 이동
        closeButton.setOnClickListener {
            dismiss() // 현재 BottomSheet 닫기
            goToSurveyWorkFragment() // SurveyWorkFragment로 이동
        }

        return view
    }

    // **하나의 버튼만 선택 가능하도록 설정**
    private fun selectSingleExerciseButton(button: MaterialButton) {
        selectedExerciseButton?.let {
            it.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#F0F0F0")) // 기본 배경
            it.setTextColor(Color.parseColor("#9A9A9A")) // 기본 글씨 색
            it.strokeColor = ColorStateList.valueOf(Color.parseColor("#F0F0F0")) // 기본 테두리 색
        }

        if (selectedExerciseButton == button) {
            selectedExerciseButton = null  // 동일한 버튼을 다시 클릭하면 해제
        } else {
            selectedExerciseButton = button
            button.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#FFEAD9")) // 선택된 배경
            button.setTextColor(Color.parseColor("#FF7300")) // 선택된 글씨 색
            button.strokeColor = ColorStateList.valueOf(Color.parseColor("#FF7300")) // 선택된 테두리

            // ✅ 선택한 운동 횟수를 즉시 ViewModel에 반영
            val selectedExercise = button.text.toString().toIntOrNull() ?: 0
            viewModel.updateSurveyData(viewModel.surveyData.value!!.copy(
                exerciseFrequency = selectedExercise
            ))

            // ✅ SurveyWorkFragment에도 선택된 값 반영
            onSelectionDone(selectedExercise.toString())
        }

        // "다음 버튼" 활성화/비활성화 업데이트
        updateNextButtonState()
    }

    private fun updateNextButtonState() {
        nextButton.isEnabled = selectedExerciseButton != null
        nextButton.backgroundTintList = if (selectedExerciseButton != null)
            ColorStateList.valueOf(Color.parseColor("#FF7300"))
        else
            ColorStateList.valueOf(Color.parseColor("#CDCDCD"))
    }

    private fun sendSurveyDataToServer() {
        viewModel.submitSurveyData(
            isUpdate = false,
            onSuccess = {
                dismiss() // ✅ BottomSheet 먼저 닫기
//                showAnimationAndNavigateToMain()
                showAnimationAndNavigateToMain()
            },
            onError = { errorMessage ->
                Toast.makeText(requireContext(), "서버 전송 실패: $errorMessage", Toast.LENGTH_SHORT).show()
            }
        )
    }

   //  ✅ 애니메이션 완료 후 MainActivity로 이동
   private fun showAnimationAndNavigateToMain() {
       val animationFragment = AnimationFragment()
       animationFragment.setAnimationCompleteListener(object : AnimationFragment.AnimationCompleteListener {
           override fun onAnimationComplete() {
               // Fragment가 Activity에 연결되어 있는지 확인
               if (isAdded) {
                   val intent = Intent(requireContext(), MainActivity::class.java)
                   intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                   startActivity(intent)
               }
           }
       })

       requireActivity().supportFragmentManager.beginTransaction()
           .replace(R.id.survey_container, animationFragment)
           .addToBackStack(null)
           .commit()
   }



    // SurveyGoalFragment로 이동 대신 MainActivity로 이동하도록 변경
    private fun goToMainActivity() {
        val intent = Intent(requireContext(), MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }


    // 이전 페이지
    // SurveyBmiFragment로 이동
    private fun goToSurveyBmiFragment() {
        val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.survey_container, SurveyBmiFragment())
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }

    // SurveyWorkFragment로 이동 (X 버튼 클릭 시)
    private fun goToSurveyWorkFragment() {
        val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.survey_container, SurveyWorkFragment())
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }
}
