package com.example.umc.Quote

import android.os.Bundle
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
import com.example.umc.Quote.Sub.FoodPriceFragment
import com.example.umc.R
import com.example.umc.UserApi.RetrofitClient
import kotlinx.coroutines.launch

class PriceDetailAdapter(
    private val itemList: List<FoodItem>,
    private val fragment: Fragment
) : RecyclerView.Adapter<PriceDetailAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemPrice: TextView = itemView.findViewById(R.id.item_price)
        val itemText: TextView = itemView.findViewById(R.id.item_text)
        val itemImage: ImageView = itemView.findViewById(R.id.item_image) // Add this to reference ImageView

        init {
            // 아이템 클릭 이벤트 처리
            itemView.setOnClickListener {
                val foodItem = itemList[adapterPosition]

                // 클릭된 아이템 정보를 담아 FoodPriceFragment로 이동
                val foodPriceFragment = FoodPriceFragment()
                val bundle = Bundle().apply {
                    putString("food_name", foodItem.name)
                    putString("food_price", foodItem.price)
                    putString("food_unit", foodItem.unit)
                }
                foodPriceFragment.arguments = bundle

                // Fragment 전환
                val transaction = fragment.parentFragmentManager.beginTransaction()
                transaction.replace(R.id.main_container, foodPriceFragment)  // 컨테이너 ID를 변경
                transaction.addToBackStack(null)  // 백스택에 추가 (뒤로가기 기능)
                transaction.commit()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // 기존 item_horizontal.xml을 사용
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_horizontal, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val foodItem = itemList[position]
        holder.itemPrice.text = foodItem.price
        holder.itemText.text = foodItem.name

        // Call the loadMaterialImage function to load the image into the ImageView
        loadMaterialImage(foodItem.name, holder.itemImage)
    }

    // Function to load images dynamically using the food name
    private fun loadMaterialImage(foodName: String, imageView: ImageView) {
        imageView.setImageDrawable(null)

        // Network call to fetch image URL
        fragment.lifecycleScope.launch {
            try {
                val response = RetrofitClient.imageApiService.getMaterialImage(foodName)

                if (response.isSuccessful) {
                    val imageUrl = response.body()?.success?.imageUrl
                    if (!imageUrl.isNullOrEmpty()) {
                        // Load the image using Glide
                        Glide.with(fragment.requireContext())
                            .load(imageUrl)
                            .into(imageView)  // Set the image into the ImageView
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

    override fun getItemCount(): Int = itemList.size
}
