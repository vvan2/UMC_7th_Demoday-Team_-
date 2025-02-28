package com.example.umc.Subscribe

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.umc.R
import com.example.umc.databinding.ItemOrderDateGroupBinding

class OrderHistoryAdapter(
    private val onDetailViewClickListener: (OrderGroup) -> Unit // 상세보기 클릭 리스너 추가
) : RecyclerView.Adapter<OrderHistoryAdapter.OrderViewHolder>() {
    private var orderGroups = listOf<OrderGroup>()

    inner class OrderViewHolder(private val binding: ItemOrderDateGroupBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(orderGroup: OrderGroup) {
            binding.apply {
                // 주문 날짜 헤더 설정
                tvOrderDate.text = orderGroup.orderDate

                // 상세보기 클릭 리스너 설정
                tvDetailView.setOnClickListener {
                    onDetailViewClickListener(orderGroup)
                }

                // 기존 주문 아이템들 제거
                orderItemsContainer.removeAllViews()

                // 각 주문 아이템 추가
                orderGroup.orderItems.forEach { orderItem ->
                    val orderItemView = LayoutInflater.from(itemView.context)
                        .inflate(R.layout.item_order_detail, orderItemsContainer, false)

                    // 주문 아이템 뷰 설정
                    orderItemView.apply {
                        findViewById<TextView>(R.id.tvDeliveryDate).text =
                            "${orderItem.deliveryDate} ${orderItem.deliveryStatus}"
                        findViewById<TextView>(R.id.tvDeliveryLocation).text =
                            orderItem.deliveryLocation
                        findViewById<TextView>(R.id.tvMenuTitle).text =
                            orderItem.menuName
                        findViewById<TextView>(R.id.tvMenuBreakfast).text =
                            orderItem.breakfastMenu
                        findViewById<TextView>(R.id.tvMenuLunch).text =
                            orderItem.lunchMenu

                        // 이미지 로딩
                        Glide.with(this)
                            .load(orderItem.imageUrl)
                            .into(findViewById(R.id.ivFoodImage))

                        // 리뷰 버튼 설정
                        val btnReview = findViewById<TextView>(R.id.btnReview)
                        btnReview.isEnabled = orderItem.isReviewEnabled

                        // 리뷰 버튼 클릭 리스너 설정
                        btnReview.setOnClickListener {
                            if(orderItem.isReviewEnabled) {
                                btnReview.isEnabled = false
                                // 여기에 리뷰 작성 로직 추가
                            }
                        }
                    }

                    orderItemsContainer.addView(orderItemView)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val binding = ItemOrderDateGroupBinding.inflate(
            LayoutInflater.from(parent.context), parent, false)
        return OrderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.bind(orderGroups[position])
    }

    override fun getItemCount() = orderGroups.size

    fun submitList(newList: List<OrderGroup>) {
        orderGroups = newList
        notifyDataSetChanged()
    }
}