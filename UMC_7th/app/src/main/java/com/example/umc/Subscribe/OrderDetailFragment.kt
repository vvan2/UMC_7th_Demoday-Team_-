package com.example.umc.Subscribe

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.umc.Main.MainActivity
import com.example.umc.databinding.FragmentOrderDetailBinding

class OrderDetailFragment : Fragment() {
    private var _binding: FragmentOrderDetailBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: SubscribeViewModel
    private val orderMenuAdapter = OrderMenuAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOrderDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(requireActivity()).get(SubscribeViewModel::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        loadOrderData()
    }

    private fun setupRecyclerView() {
        binding.orderItemsRv.apply {
            adapter = orderMenuAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun loadOrderData() {
        // ViewModel의 선택된 식단 데이터를 기반으로 OrderDetails 생성
        val selectedDiets = viewModel.selectedDiets.value ?: emptyList()

        // 선택된 식단 정보로 OrderMenuItem 생성
        val orderItems = if (selectedDiets.isEmpty()) {
            listOf(
                OrderMenuItem(
                    menuDate = "1/22 (수) 식단 - 배송 완료",
                    breakfastName = "하루 시작 포케 - 2인분",
                    breakfastKcal = 420,
                    breakfastPrice = 20000,
                    lunchName = "고등어 조림 한상 - 1인분",
                    lunchKcal = 840,
                    lunchPrice = 12000
                )
            )
        } else {
            selectedDiets.map { diet ->
                OrderMenuItem(
                    menuDate = "${diet.mealDate} (${diet.week}) 식단 - 배송 예정",
                    breakfastName = diet.food,
                    breakfastKcal = 420,
                    breakfastPrice = 12000,
                    lunchName = "추가 메뉴",
                    lunchKcal = 840,
                    lunchPrice = 12000
                )
            }
        }

        // 주문 상세 정보 설정
        val orderDetails = OrderDetails(
            orderDate = "2025. 1. 1 결제건",
            recipientName = "김태현",
            address = "서울시 송파구 송파동 송파아파트 101동 101호",
            phoneNumber = "010-1234-5678",
            deliveryMemo = "문 앞 (1234)",
            orderItems = orderItems
        )

        // ViewModel에 주문 상세 정보 저장
        viewModel.setOrderDetails(orderDetails)

        // UI 업데이트
        orderMenuAdapter.submitList(orderItems)

        binding.apply {
            orderDateTv.text = orderDetails.orderDate
            recipientNameTv.text = orderDetails.recipientName
            addressDetailTv.text = orderDetails.address
            phoneTv.text = orderDetails.phoneNumber
            deliveryMemoTv.text = orderDetails.deliveryMemo

            // 총 가격 계산 로직 추가
            val totalPrice = orderItems.sumOf { it.breakfastPrice + it.lunchPrice }
            productPriceTv.text = "${totalPrice}원"
            deliveryFeeTv.text = "0원"
            kakaoPayTv.text = "${totalPrice}원"
            totalPriceTv.text = "${totalPrice}원"
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as? MainActivity)?.showTitle("주문 상세보기", true)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}