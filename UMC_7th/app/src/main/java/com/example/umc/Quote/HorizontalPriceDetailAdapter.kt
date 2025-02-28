package com.example.umc.Quote

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.umc.R
import com.example.umc.UserApi.RetrofitClient
import kotlinx.coroutines.launch

class HorizontalPriceDetailAdapter(
    private val itemList: List<FoodItem>,
    private val fragment: Fragment
) : RecyclerView.Adapter<HorizontalPriceDetailAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemPrice: TextView = itemView.findViewById(R.id.item_price)
        val itemText: TextView = itemView.findViewById(R.id.item_text)
        val itemImage: ImageView = itemView.findViewById(R.id.item_image)

        init {
            // Handle item click
            itemView.setOnClickListener {
                val foodItem = itemList[adapterPosition]
                // Handle item click logic here
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_horizontal, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val foodItem = itemList[position]
        holder.itemPrice.text = foodItem.price  // 가격을 표시
        holder.itemText.text = foodItem.name  // 품목명 표시

        // 이미지 로드를 위한 함수 호출
        loadMaterialImage(foodItem.name, holder.itemImage)
    }

    override fun getItemCount(): Int {
        return if (itemList.size > 5) 5 else itemList.size
    }

    // 이미지 동적으로 로드하는 함수
    private fun loadMaterialImage(foodName: String, imageView: ImageView) {
        imageView.setImageDrawable(null)

        // 네트워크 호출로 이미지 URL 가져오기
        fragment.lifecycleScope.launch {
            try {
                val response = RetrofitClient.imageApiService.getMaterialImage(foodName)

                if (response.isSuccessful) {
                    val imageUrl = response.body()?.success?.imageUrl
                    if (!imageUrl.isNullOrEmpty()) {
                        // Glide를 사용하여 이미지 로드
                        Glide.with(fragment.requireContext())
                            .load(imageUrl)
                            .into(imageView)  // ImageView에 이미지 설정
                        Log.d("FoodImage", "이미지 로드 성공: $imageUrl")
                    } else {
                        Log.e("FoodImage", "이미지 URL이 비어 있음")
                        Toast.makeText(fragment.context, "이미지 URL이 비어 있습니다.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Log.e("FoodImage", "API 호출 실패: ${response.message()}")
                    Toast.makeText(fragment.context, "API 호출 실패: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("FoodImage", "네트워크 오류: ${e.message}")
                Toast.makeText(fragment.context, "네트워크 오류: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
