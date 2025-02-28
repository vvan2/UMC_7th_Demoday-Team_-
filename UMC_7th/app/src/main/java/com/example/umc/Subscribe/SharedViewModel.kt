package com.example.umc.Subscribe

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.umc.Diet.DietItem
import com.example.umc.model.CartItem

class SubscribeViewModel : ViewModel() {
    private val _selectedDiets = MutableLiveData<List<DietItem>>()
    val selectedDiets: LiveData<List<DietItem>> = _selectedDiets

    // 주문 상세 정보를 저장할 LiveData 추가
    private val _orderDetails = MutableLiveData<OrderDetails>()
    val orderDetails: LiveData<OrderDetails> = _orderDetails

    fun setSelectedDiets(diets: List<DietItem>) {
        _selectedDiets.value = diets.filter { it.isChecked }
    }
    // 주문 상세 정보를 설정하는 함수
    fun setOrderDetails(details: OrderDetails) {
        _orderDetails.value = details
    }
    fun convertToCartItems(selectedDiets: List<DietItem>): List<CartItem> {
        return selectedDiets.map { diet ->
            CartItem(
                date = diet.mealDate,
                time = diet.time,
                menu = diet.food,
                serving = 1, // 기본값 설정
                isChecked = true // 카트에 추가될 때 기본적으로 체크됨
            )
        }
    }
}

// 주문 상세 정보를 담는 데이터 클래스 생성
data class OrderDetails(
    val orderDate: String,
    val recipientName: String,
    val address: String,
    val phoneNumber: String,
    val deliveryMemo: String,
    val orderItems: List<OrderMenuItem>
)
