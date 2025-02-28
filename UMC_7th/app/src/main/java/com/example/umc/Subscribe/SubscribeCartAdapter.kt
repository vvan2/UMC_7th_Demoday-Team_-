package com.example.umc.Subscribe

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.umc.R
import com.example.umc.databinding.ItemSubCartBinding
import com.example.umc.model.CartItem

class SubscribeCartAdapter(
    private val cartItems: MutableList<CartItem>,
    private val itemClickListener: (CartItem) -> Unit,
    private val updateTotalPrice: () -> Unit // 총 합계를 업데이트하는 함수
) : RecyclerView.Adapter<SubscribeCartAdapter.CartViewHolder>() {

    inner class CartViewHolder(private val binding: ItemSubCartBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(cartItem: CartItem) {
            binding.tvCartDate.text = cartItem.date
            binding.tvCartTime.text = cartItem.time
            binding.tvMenuCart.text = cartItem.menu
            binding.tvServingCart.text = cartItem.serving.toString()
            updateCheckButtonDrawable(cartItem.isChecked, binding.ibtCheckCart)

            // plus 버튼 클릭 시 serving 증가
            binding.ibtPlus.setOnClickListener {
                cartItem.serving += 1
                binding.tvServingCart.text = cartItem.serving.toString()
                updateTotalPrice() // 총 합계 업데이트
            }

            // minus 버튼 클릭 시 serving 감소 (최소값 1)
            binding.ibtMinus.setOnClickListener {
                if (cartItem.serving > 1) {
                    cartItem.serving -= 1
                    binding.tvServingCart.text = cartItem.serving.toString()
                    updateTotalPrice() // 총 합계 업데이트
                }
            }

            // 체크 버튼 클릭 시 상태 토글
            binding.ibtCheckCart.setOnClickListener {
                cartItem.isChecked = !cartItem.isChecked
                updateCheckButtonDrawable(cartItem.isChecked, binding.ibtCheckCart)
                itemClickListener(cartItem) // 아이템 클릭 리스너 호출
                updateTotalPrice() // 총 합계 업데이트
            }
        }

        private fun updateCheckButtonDrawable(isChecked: Boolean, button: ImageButton) {
            val drawableResId = if (isChecked) R.drawable.check_on else R.drawable.real_add
            button.setImageResource(drawableResId)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = ItemSubCartBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.bind(cartItems[position])
    }

    override fun getItemCount(): Int {
        return cartItems.size
    }
}
