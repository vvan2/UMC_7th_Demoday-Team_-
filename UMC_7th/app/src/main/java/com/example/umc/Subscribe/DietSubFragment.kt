

package com.example.umc.Subscribe

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.umc.Diet.DietItem
import com.example.umc.Main.MainActivity
import com.example.umc.R
import com.example.umc.UserApi.RetrofitClient
import com.example.umc.UserApi.UserRepository
import com.example.umc.cart.SubscribeCart
import com.example.umc.databinding.FragmentDietSubBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Locale

class DietSubFragment : Fragment(), OnDietCheckedChangeListener {

    private var _binding: FragmentDietSubBinding? = null
    private val binding get() = _binding!!

    private lateinit var dailyDietAdapter: SubscribeDietAdapter
    private lateinit var dietList: List<DietItem>
    private lateinit var viewModel: SubscribeViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDietSubBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(requireActivity()).get(SubscribeViewModel::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 데이터 로드
        loadDataFromServer()

        binding.btCart.setOnClickListener {
            Toast.makeText(requireContext(), "장바구니 담기 완료", Toast.LENGTH_SHORT).show()
            navigateToSubscribeCart()
        }
    }

    private fun loadDataFromServer() {
        CoroutineScope(Dispatchers.IO).launch {
            val token = UserRepository.getAuthToken(requireContext()) // SharedPreferences에서 토큰을 가져옴
            try {
                val response = RetrofitClient.subMealService.getSubMealsList("맛있는 일상 구독", "Bearer $token")
                if (response.isSuccessful) {
                    val data = response.body()?.success ?: emptyList()
                    withContext(Dispatchers.Main) {
                        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        val outputFormat = SimpleDateFormat("MM.dd", Locale.getDefault())
                        val weekFormat = SimpleDateFormat("E", Locale.KOREAN) // 요일 포맷

                        dietList = data.map {
                            val mealDate = it.mealSubs.firstOrNull()?.mealDate ?: ""
                            val parsedDate = dateFormat.parse(mealDate)
                            val formattedDate = if (parsedDate != null) outputFormat.format(parsedDate) else ""
                            val weekDay = if (parsedDate != null) weekFormat.format(parsedDate) else ""

                            DietItem(
                                mealDate = formattedDate,
                                week = weekDay,
                                time = it.mealSubs.firstOrNull()?.time ?: "",
                                food = it.food,
                                isChecked = false // 초기 체크 상태 설정
                            )
                        }
                        dailyDietAdapter = SubscribeDietAdapter(dietList, this@DietSubFragment)
                        binding.recyclerDailyDiet.layoutManager = LinearLayoutManager(requireContext())
                        binding.recyclerDailyDiet.adapter = dailyDietAdapter
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(requireContext(), "서버 연결 실패: ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "서버 연결 중 오류 발생: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }



    private fun navigateToSubscribeCart() {
        // 선택된 아이템만 필터링하여 ViewModel에 저장
        val selectedDiets = dietList.filter { it.isChecked }
        viewModel.setSelectedDiets(selectedDiets)

        val transaction: FragmentTransaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.main_container, SubscribeCart())
        transaction.addToBackStack(null)
        transaction.commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        val mainActivity = activity as? MainActivity
        mainActivity?.showTitle("맛있는 일상 구독", true)
        (activity as? MainActivity)?.showBottomBar()
    }

    override fun onDietCheckedChange(isAnyChecked: Boolean) {
        val color = if (isAnyChecked) R.color.Primary_Orange1 else R.color.Gray7
        binding.btCart.setBackgroundColor(ContextCompat.getColor(requireContext(), color))
    }
}
