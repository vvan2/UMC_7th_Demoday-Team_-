package com.example.umc.Subscribe

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.umc.Main.MainActivity
import com.example.umc.R
import com.example.umc.Subscribe.Repository.MealRepository
import com.example.umc.databinding.FragmentSubscriptionHistoryBinding
import kotlinx.coroutines.launch

class SubscriptionHistoryFragment : Fragment() {
    private var _binding: FragmentSubscriptionHistoryBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: OrderHistoryAdapter

    private val orderGroups = mutableListOf<OrderGroup>()
    private val mealRepository by lazy { MealRepository(requireContext()) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSubscriptionHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupSearchBar()
        loadOrderHistory()

    }

    private fun setupRecyclerView() {
        adapter = OrderHistoryAdapter { orderGroup ->
            try {
                // 상세보기 클릭 시 OrderDetailFragment로 이동
                val fragment = OrderDetailFragment().apply {
                    arguments = Bundle().apply {
                        putString("ORDER_DATE", orderGroup.orderDate)
                        // 필요한 추가 정보들 전달 가능
                    }
                }

                // 프래그먼트 트랜잭션으로 교체
                parentFragmentManager.beginTransaction()
                    .replace(R.id.main_container, fragment)  // main_container로 변경
                    .addToBackStack(null)
                    .commit()
            } catch (e: Exception) {
                // 예외 발생 시 로그 출력
                Log.e("SubscriptionHistoryFragment", "Fragment transaction error", e)
                // 필요하다면 사용자에게 오류 메시지 표시
                Toast.makeText(requireContext(), "화면 전환 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
            }
        }
        binding.subscriptionHistoryRecyclerView.apply {
            adapter = this@SubscriptionHistoryFragment.adapter
            layoutManager = LinearLayoutManager(context)
        }
    }
    private fun setupSearchBar() {
        binding.searchBar.setOnSearchClickListener { searchText ->
            if (searchText.isNotEmpty()) {
                filterOrders(searchText)   // searchText를 loadOrderHistory에 전달
            } else {
                loadOrderHistory()  // 검색 텍스트가 비어 있으면 전체 주문 내역을 다시 로드
            }
        }
    }

    private fun filterOrders(searchText: String) {
        if (searchText.isEmpty()) {
            loadOrderHistory()
            return
        }

        // 날짜로 필터링된 주문 목록 표시
        val filteredList = orderGroups.filter { group ->
            group.orderItems.any { item ->
                item.deliveryDate.contains(searchText, ignoreCase = true)
            }
        }
        adapter.submitList(filteredList)
    }

    private fun loadOrderHistory(searchDate: String? = null) {
        if (searchDate != null && searchDate.isNotEmpty()) {
            // 날짜를 기반으로 API 호출
            fetchOrderHistoryFromApi(searchDate)
        } else {
            // 검색 없이 전체 주문 내역 표시
            // 샘플 데이터 로드 (예시 데이터 추가)
            val sampleData = listOf(
                OrderGroup(
                    orderDate = "2025.01.01 주문내역",
                    orderItems = listOf(
                        OrderItem(
                            deliveryDate = "1/22 (수)",
                            deliveryStatus = "배송 완료",
                            deliveryLocation = "배송장소 (아침, 점심)",
                            menuName = "다이어트 구독 식단",
                            breakfastMenu = "아침 - 하루 시작 포케 (420kcal)",
                            lunchMenu = "점심 - 고등어 조림 한상/비조림 (830kcal)",
                            imageUrl = "sample_image_url",
                            isReviewEnabled = true
                        ),
                        OrderItem(
                            deliveryDate = "1/24 (금)",
                            deliveryStatus = "도착 예정",
                            deliveryLocation = "배송장소 (아침, 점심)",
                            menuName = "다이어트 구독 식단",
                            breakfastMenu = "아침 - 하루 시작 포케 (420kcal)",
                            lunchMenu = "점심 - 고등어 조림 한상/비조림 (830kcal)",
                            imageUrl = "sample_image_url",
                            isReviewEnabled = false
                        )
                    )
                )
            )
            orderGroups.clear()
            orderGroups.addAll(sampleData)  // 샘플 데이터를 orderGroups에 추가
            adapter.submitList(sampleData)  // 샘플 데이터를 RecyclerView에 제출
        }
    }
    private fun fetchOrderHistoryFromApi(date: String) {
        lifecycleScope.launch {
            try {
                val result = mealRepository.getMealsByDate(date)
                result.onSuccess { mealResponse ->
                    // API에서 받은 데이터를 RecyclerView에 업데이트
                    val orderGroups = mealResponse.success.map { mealData ->
                        OrderGroup(
                            orderDate = mealData.orderAt,
                            orderItems = listOf(
                                OrderItem(
                                    deliveryDate = mealData.mealSub.mealDate,
                                    deliveryStatus = "상태",  // 실제 상태로 변경 필요
                                    deliveryLocation = "위치",  // 실제 위치로 변경 필요
                                    menuName = mealData.mealSub.category.name,
                                    breakfastMenu = mealData.mealSub.meal.food,
                                    lunchMenu = "",  // 점심 메뉴가 필요하다면 추가
                                    imageUrl = "",  // 실제 이미지 URL을 사용
                                    isReviewEnabled = false // 리뷰 가능 여부 추가
                                )
                            )
                        )
                    }
                    adapter.submitList(orderGroups)
                }.onFailure {
                    Toast.makeText(requireContext(), "식단 정보를 불러오는 데 실패했습니다.", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "네트워크 오류: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        (activity as? MainActivity)?.showTitle("구독 내역", true)
    }

    companion object {
        private val orderGroups = mutableListOf<OrderGroup>()
    }
}