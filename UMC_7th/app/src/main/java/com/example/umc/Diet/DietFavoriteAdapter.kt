package com.example.umc.Diet

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.umc.R
import com.example.umc.UserApi.RetrofitClient
import com.example.umc.databinding.ItemFavoriteBinding
import com.example.umc.model.response.GetFavoriteMeals
import kotlinx.coroutines.launch

class DietFavoriteAdapter(
    private val onClick: (GetFavoriteMeals, Int) -> Unit
) : RecyclerView.Adapter<DietFavoriteAdapter.ViewHolder>() {

    private val favoriteItems = mutableListOf<GetFavoriteMeals>()

    inner class ViewHolder(private val binding: ItemFavoriteBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(favoriteItem: GetFavoriteMeals) {
            binding.apply {
                // 아이템 데이터를 바인딩합니다
                tvFavoriteName.text = favoriteItem.food
                tvFavoriteCalories.text = "${favoriteItem.calorieTotal} kcal"

                loadMealImage(favoriteItem.food, ivFavoriteImage) // favoriteItem.food로 수정


                // 아이템 클릭 리스너 설정
                root.setOnClickListener {
                    onClick(favoriteItem, adapterPosition)
                }
            }
        }
    }

    private fun loadMealImage(foodName: String, imageView: ImageView) {
        val context = imageView.context
        (context as? LifecycleOwner)?.lifecycleScope?.launch {
            try {
                val response = RetrofitClient.imageApiService.getMealImage(foodName)

                if (response.isSuccessful) {
                    val imageUrl = response.body()?.success?.imageUrl
                    if (!imageUrl.isNullOrEmpty()) {
                        Glide.with(context)
                            .load(imageUrl)
                            .into(imageView)
                    } else {
                        Log.e("DietFavoriteAdapter", "이미지 URL이 비어 있음")
                    }
                } else {
                    Log.e("DietFavoriteAdapter", "API 호출 실패: ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e("DietFavoriteAdapter", "네트워크 오류: ${e.message}")
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemFavoriteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(favoriteItems[position])
    }

    override fun getItemCount(): Int = favoriteItems.size

    // 아이템 업데이트 메서드
    fun updateItems(items: List<GetFavoriteMeals>) {
        favoriteItems.clear()
        favoriteItems.addAll(items)
        notifyDataSetChanged()
    }
}
