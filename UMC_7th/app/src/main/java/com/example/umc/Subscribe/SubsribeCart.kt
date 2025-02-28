package com.example.umc.cart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.umc.Main.MainActivity
import com.example.umc.R
import com.example.umc.Subscribe.Retrofit.RetrofitClient
import com.example.umc.Subscribe.SubscribeCartAdapter
import com.example.umc.Subscribe.SubscribeViewModel
import com.example.umc.Subscribe.Subscribecredit
import com.example.umc.databinding.FragmentSubscribeCartBinding
import com.example.umc.model.CartItem
import java.text.NumberFormat
import java.util.Locale

class SubscribeCart : Fragment() {
    private var _binding: FragmentSubscribeCartBinding? = null
    private val binding get() = _binding!!

    private var isAllSelected = false
    private val itemChecked = mutableListOf(false, false, false, false)
    private val itemCounts = mutableListOf(1, 1, 1, 1)
    private lateinit var viewModel: SubscribeViewModel

    private val cartItems = mutableListOf(
        CartItem("01.01", "아침", "콩나물 김치찌개 외 3개", 1),
        CartItem("01.02", "점심", "돈까스 냉모밀 외 3개", 1),
        CartItem("01.03", "저녁", "제육볶음 외 3개", 1)
    )

    private lateinit var adapter: SubscribeCartAdapter
    private var popupWindow: PopupWindow? = null
    private var isTooltipVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(requireActivity()).get(SubscribeViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSubscribeCartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // ViewModel에서 선택된 식단 아이템 가져오기
        viewModel.selectedDiets.observe(viewLifecycleOwner) { selectedDiets ->
            if (selectedDiets.isNotEmpty()) {
                // 선택된 식단을 CartItem으로 변환
                cartItems.clear()
                cartItems.addAll(viewModel.convertToCartItems(selectedDiets))
                adapter.notifyDataSetChanged()
                updateTotalPrice()
                updateServingSummary()
            }
        }
        // 어댑터 초기화
        adapter = SubscribeCartAdapter(cartItems, { cartItem ->
            updateButtonColor()
        }, {
            updateTotalPrice() // 총 합계 업데이트
            updateServingSummary()
        })

        binding.rvMenuCart.layoutManager = LinearLayoutManager(requireContext())
        binding.rvMenuCart.adapter = adapter

        binding.creditbutton.setOnClickListener {
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.main_container, Subscribecredit())
            transaction.addToBackStack(null)
            transaction.commit()
        }

        binding.imageView7.setOnClickListener {
            isAllSelected = !isAllSelected
            val newImageRes = if (isAllSelected) R.drawable.check_on else R.drawable.real_add
            binding.imageView7.setImageResource(newImageRes)

            for (i in itemChecked.indices) {
                itemChecked[i] = isAllSelected
            }
            updateItemCheckState()
        }

        binding.textView45.setOnClickListener {
            if (popupWindow == null) {
                val tooltipView = layoutInflater.inflate(R.layout.dialog_price_policy, null)
                popupWindow = PopupWindow(tooltipView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
                    isOutsideTouchable = true
                    setBackgroundDrawable(ContextCompat.getDrawable(requireContext(), android.R.color.transparent)) // 배경 투명 설정
                }
            }
            if (isTooltipVisible) {
                popupWindow?.dismiss()
            } else {
                popupWindow?.showAsDropDown(binding.textView45, -30, 0)
            }
            isTooltipVisible = !isTooltipVisible
        }

        // 터치 인터셉터를 사용하여 다른 곳 클릭 시 팝업 창 닫기
        popupWindow?.setTouchInterceptor { v, event ->
            if (event.action == MotionEvent.ACTION_OUTSIDE) {
                popupWindow?.dismiss()
                isTooltipVisible = false
                v.performClick() // 클릭 이벤트 호출
                true
            } else {
                false
            }
        }

        // 총 합계 초기 업데이트
        updateTotalPrice()
    }

    private fun updateItemCheckState() {
        cartItems.forEachIndexed { index, cartItem ->
            cartItem.isChecked = itemChecked[index]
        }
        adapter.notifyDataSetChanged()
        updateButtonColor()
        updateTotalPrice() // 총 합계 업데이트
        updateServingSummary()
    }

    private fun updateButtonColor() {
        val isAnyChecked = cartItems.any { it.isChecked }
        val color = if (isAnyChecked) R.color.Primary_Orange1 else R.color.Gray7
        binding.creditbutton.setBackgroundColor(ContextCompat.getColor(requireContext(), color))
    }

    private fun updateTotalPrice() {
        var totalPrice = 0
        cartItems.forEach { cartItem ->
            if (cartItem.isChecked) {
                val price = when {
                    cartItem.serving == 1 -> 12000
                    cartItem.serving == 2 -> 10000 * cartItem.serving
                    cartItem.serving >= 3 -> 9000 * cartItem.serving
                    else -> 0
                }
                totalPrice += price
            }
        }
        val numberFormat = NumberFormat.getNumberInstance(Locale.KOREA)
        val formattedPrice = numberFormat.format(totalPrice)
        binding.textView11.text = String.format("%s원", formattedPrice)
    }

    private fun updateServingSummary() {
        val servingSummary = mutableMapOf<Int, Int>()
        cartItems.forEach { cartItem ->
            if (cartItem.isChecked) {
                servingSummary[cartItem.serving] = servingSummary.getOrDefault(cartItem.serving, 0) + 1
            }
        }
        val servingSummaryText = servingSummary.entries.joinToString(" + ") { "(${it.key}인분×${it.value})" }
        binding.tvSummaryServing.text = servingSummaryText
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        (activity as? MainActivity)?.showTitle("장바구니", true)
        (activity as? MainActivity)?.showBottomBar()
    }
}
