package com.example.umc.Subscribe

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.umc.model.Address

class AddressViewModel : ViewModel() {
    // 주소 목록을 LiveData로 관리하여 UI 업데이트 가능
    val addressList = MutableLiveData<MutableList<Address>>().apply {
        value = mutableListOf(
            Address("김태현", "[00000]", "서울시 송파구 송파동 송파아파트 101동 101호", "010-1234-5678", "문 앞(1234)"),
            Address("양유진", "[00000]", "서울시 강남구 강남동 강남아파트 101동 101호", "010-1234-5678", "문 앞(5678)")
        )
    }

    // 주소 추가 메서드
    fun addAddress(address: Address) {
        val updatedList = addressList.value ?: mutableListOf()
        updatedList.add(address)
        addressList.value = updatedList
    }
}
