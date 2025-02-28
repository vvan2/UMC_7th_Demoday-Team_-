package com.example.umc.Subscribe

import androidx.recyclerview.widget.RecyclerView
import com.example.umc.databinding.ItemDeliveryInfoBinding

class DeliveryInfoViewHolder(private val binding: ItemDeliveryInfoBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(item: DeliveryInfo) {
        with(binding) {
            nameTv.text = item.name
            postcodeTv.text = item.postcode
            addressTv.text = item.address
            phoneTv.text = item.phone
            memoTv.text = item.memo

            // 변경 버튼 클릭 리스너 설정
            changeBtn.setOnClickListener {
                // 변경 버튼 클릭 처리
            }
        }
    }
}