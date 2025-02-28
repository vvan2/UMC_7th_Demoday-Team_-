package com.example.umc.Mypage

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.umc.Main.MainActivity
import com.example.umc.R
import com.example.umc.Survey.SurveyGoalFragment
import com.example.umc.Survey.SurveyMealFragment
import com.example.umc.UserApi.Response.HealthScoreData
import com.example.umc.UserApi.Response.SuccessData
import com.example.umc.UserApi.SharedPreferencesManager
import com.example.umc.UserApi.UserRepository
import com.example.umc.databinding.FragmentMyBinding
import kotlinx.coroutines.async
import kotlinx.coroutines.launch


class MyFragment : Fragment() {
    private var _binding: FragmentMyBinding? = null
    private val binding get() = _binding!!
    private val userRepository = UserRepository()

    private lateinit var diagnosis1: TextView
    private lateinit var diagnosis2: TextView
    private lateinit var advice1: TextView
    private lateinit var advice2: TextView


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        diagnosis1 = binding.diagnosis1
        diagnosis2 = binding.diagnosis2
        advice1 = binding.advice1
        advice2 = binding.advice2

        initializeViews()
        setupListeners()

            // 개별 호출
//        fetchHealthScore()
//        fetchAiDiagnosis() // AI 진단 데이터 조회
//        fetchMypageGoal() // 목표 정보 조회


    }
//    private fun fetchAiDiagnosis() {
//        lifecycleScope.launch {
//            try {
//                val response = userRepository.getDiagnosisResult(requireContext())
//
//                if (response == null) {
//                    Log.e("MyFragment", "서버 응답이 null입니다.")
//                    binding.tvAiDiagnosisDiet.text = "서버 응답이 없습니다."
//                    binding.tvAiDiagnosisHealth.text = "서버 응답이 없습니다."
//                    return@launch
//                }
//
//                Log.d("MyFragment", "AI 진단 응답 데이터: $response")
//
//                response.success?.let {
//                    Log.d("MyFragment", "Diagnosis: ${it.diagnosis}, Advice: ${it.advice}")
//                    updateAiDiagnosisInfo(it)
//                } ?: run {
//                    Log.e("MyFragment", "success 필드가 null입니다.")
//                }
//            } catch (e: Exception) {
//                Log.e("MyFragment", "AI 진단 조회 실패: ${e.message}", e)
//            }
//        }
//    }




//    private fun fetchMypageGoal() {
//        lifecycleScope.launch {
//            try {
//                val response = userRepository.getMypageGoal(requireContext())
//
//                if (response == null) {
//                    Log.e("MyFragment", "[마이페이지 목표] 서버 응답이 null입니다.")
//                    return@launch
//                }
//
//                Log.d("MyFragment", "[마이페이지 목표] 응답 데이터: $response")
//
//                response.user?.let {
//                    Log.d("MyFragment", "[마이페이지 목표] 유저 목표: ${it.goal}")
//                    updateGoalInfo(it.goal)
//                } ?: Log.e("MyFragment", "[마이페이지 목표] 데이터 없음")
//            } catch (e: Exception) {
//                Log.e("MyFragment", "[마이페이지 목표] 조회 실패: ${e.message}", e)
//            }
//        }
//    }

//    private fun fetchHealthScore() {
//        lifecycleScope.launch {
//            try {
//                val healthScoreData = userRepository.getHealthScore(requireContext())
//
//                if (healthScoreData != null) {
//                    updateHealthInfo(healthScoreData)
//                } else {
//                    Log.e("MyFragment", "[건강 점수] 데이터 없음")
//                }
//            } catch (e: Exception) {
//                Log.e("MyFragment", "건강 점수 조회 실패: ${e.message}", e)
//            }
//        }
//    }

    //병렬 적으로 불러오는 로직

//    private fun fetchAllData() {
//        lifecycleScope.launch {
//            try {
//                // ✅ 병렬로 API 요청 실행
//                val healthScoreDeferred = async { userRepository.getHealthScore(requireContext()) }
//                val aiDiagnosisDeferred = async { userRepository.getDiagnosisResult(requireContext()) }
//                val mypageGoalDeferred = async { userRepository.getMypageGoal(requireContext()) }
//
//                // ✅ API 응답 대기
//                val healthScoreResponse = healthScoreDeferred.await()
//                val aiDiagnosisResponse = aiDiagnosisDeferred.await()
//                val mypageGoalResponse = mypageGoalDeferred.await()
//
//                // ✅ 건강 점수 업데이트
//                healthScoreResponse?.success?.let { updateHealthInfo(it) } ?: Log.e("MyFragment", "[건강 점수] 데이터 없음")
//
//                // ✅ AI 진단 업데이트
//                aiDiagnosisResponse?.success?.let { updateAiDiagnosisInfo(it) } ?: Log.e("MyFragment", "[AI 진단] 데이터 없음")
//
//                // ✅ 마이페이지 목표 업데이트
//                mypageGoalResponse?.user?.let { updateGoalInfo(it.goal) } ?: Log.e("MyFragment", "[마이페이지 목표] 데이터 없음")
//
//            } catch (e: Exception) {
//                Log.e("MyFragment", "데이터 가져오기 실패: ${e.message}", e)
//            }
//        }
//    } 잘가라 내 코드들아

//    private fun updateGoalInfo(goal: String) {
//        binding.apply {
//            // goal 값이 업데이트되면 goalmeal TextView에 값 설정
//            goalmeal.text = goal
//        }
//    }
//    private fun updateAiDiagnosisInfo(data: SuccessData?) {
//        data?.let {
//            Log.d("MyFragment", "Diagnosis: ${it.diagnosis}")  // diagnosis 리스트의 내용을 확인
//            Log.d("MyFragment", "Advice: ${it.advice}")  // advice 리스트의 내용을 확인
//
//            binding.apply {
//                // 진단 내용 출력: diagnosis1, diagnosis2에 각각 두 항목 표시
//                it.diagnosis?.let { diagnosis ->
//                    if (diagnosis.size >= 2) {
//                        diagnosis1.text = diagnosis[0]  // 첫 번째 진단 항목
//                        diagnosis2.text = diagnosis[1]  // 두 번째 진단 항목
//                    } else {
//                        tvAiDiagnosisDiet.text = "진단 정보 없음"
//                    }
//                } ?: run {
//                    tvAiDiagnosisDiet.text = "진단 정보 없음"  // diagnosis가 null인 경우 처리
//                }
//
//                // 조언 내용 출력: advice1, advice2에 각각 두 항목 표시
//                it.advice?.let { advice ->
//                    if (advice.size >= 2) {
//                        advice1.text = advice[0]  // 첫 번째 조언 항목
//                        advice2.text = advice[1]  // 두 번째 조언 항목
//                    } else {
//                        tvAiDiagnosisHealth.text = "조언 정보 없음"
//                    }
//                } ?: run {
//                    tvAiDiagnosisHealth.text = "조언 정보 없음"  // advice가 null인 경우 처리
//                }
//            }
//        } ?: run {
//            // SuccessData가 null일 경우 처리
//            binding.tvAiDiagnosisDiet.text = "진단 데이터 불러오기 실패"
//            binding.tvAiDiagnosisHealth.text = "조언 데이터 불러오기 실패"
//        }
//    }


//    private fun updateHealthInfo(data: HealthScoreData) {
//        binding.apply {
//            // 건강 점수 업데이트
//            healthscore.text = "${data.healthScore}점"
//
//            // 비교값 업데이트 (comparison이 String으로 받아지므로 그대로 표시)
//            comparsion.text = data.comparison
//
//            // 업데이트 날짜 표시
//            textView52.text = "${data.updateAt} 기준"
//        }
//    }


    private fun initializeViews() {
        binding.apply {
            // 기존 코드
            val userName = SharedPreferencesManager.getUserName(requireContext())
            tvName.text = userName ?: "이름 없음"
            tvProfileManage.text = "내 정보 관리"

            // AI 텍스트 색상 변경을 위한 SpannableString 설정
            val texts = listOf(
                binding.tvAiDiagnosisDiet,
                binding.tvAiDiagnosisHealth,
                binding.tvAiDiagnosisSuggestion
            )

            texts.forEach { textView ->
                val fullText = textView.text.toString()
                val spannableString = SpannableString(fullText)

                // "AI" 텍스트의 위치 찾기
                val startIndex = fullText.indexOf("AI")
                if (startIndex != -1) {
                    spannableString.setSpan(
                        ForegroundColorSpan(resources.getColor(R.color.Primary_Orange1, null)),
                        startIndex,
                        startIndex + 5,  // "AI"는 2글자
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }

                textView.text = spannableString
            }
        }
    }

    private fun setupListeners() {
        binding.apply {
            // 프로필 관리 클릭 리스너
            tvProfileManage.setOnClickListener {
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_my_container, MyFragmentInfo()) // fragment_container는 MainActivity의 Fragment 배치 영역 ID
                    .addToBackStack(null) // 뒤로 가기 가능하도록 추가
                    .commit()
            }

            // 식단 카드 클릭 리스너
//            cvDiet.setOnClickListener {
//                // TODO: 식단 상세 화면으로 이동
//            }

            // 건강 점수 카드 클릭 리스너
//            cvHealthScore.setOnClickListener {
//                // TODO: 건강 점수 상세 화면으로 이동
//            }
            // 변경 버튼 클릭 리스너 추가
            btnChange.setOnClickListener {
                showChangeDialog()
            }
        }
    }
    private fun showChangeDialog() {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.fragment_my_dialog)

        // 다이얼로그 배경을 투명하게 설정
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        // 다이얼로그 외부 터치시 종료되지 않도록 설정
        dialog.setCanceledOnTouchOutside(false)

        // 취소 버튼 클릭 리스너
        dialog.findViewById<Button>(R.id.btnCancel).setOnClickListener {
            dialog.dismiss()
        }

        // 변경 버튼 클릭 리스너
        dialog.findViewById<Button>(R.id.btnConfirm).setOnClickListener {
            // 여기에 변경 버튼 클릭시 수행할 로직 추가
            dialog.dismiss()
            // SurveyGoalFragment로 전환
            val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.fragment_my_container, SurveyMealFragment())
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }

        // 다이얼로그 크기 설정
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        dialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }



    override fun onResume() {
        super.onResume()
        (activity as? MainActivity)?.hideTitle()

        // 여기나 oncreateView에 추가
    }
}