package com.example.umc.Diet

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.umc.R
import com.example.umc.UserApi.RetrofitClient
import com.example.umc.databinding.ItemMenuBinding
import com.example.umc.model.request.PatchFavoriteDeleteRequest
import com.example.umc.model.request.PatchFavoriteRequest
import com.example.umc.model.request.PostCompleteMealRequest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MenuItemAdapter(
    private val onClick: (MenuItem, Int) -> Unit,
    private val onFavoriteChanged: ((MenuItem, Boolean) -> Unit)? = null,
    private val onDietCompleteChanged: ((MenuItem, Boolean) -> Unit)? = null,
    private val onRefresh: (MenuItem) -> Unit
) : ListAdapter<MenuItem, MenuItemAdapter.ViewHolder>(DiffCallback()) {

    private var selectedPosition = RecyclerView.NO_POSITION

    inner class ViewHolder(private val binding: ItemMenuBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(menuItem: MenuItem) {
            binding.apply {
                tvMenuName.text = menuItem.name
                tvMenuCalories.text = menuItem.calories

                // 이미지 로딩
                loadMealImage(menuItem.name, ivMenuImage)

                // 선택 상태 설정
                root.isSelected = adapterPosition == selectedPosition

                // 즐겨찾기 상태 설정
                ivStar.setImageResource(
                    if (menuItem.isFavorite) R.drawable.ic_star_filled
                    else R.drawable.ic_star
                )

                // 식단 완료 버튼 UI 설정
                btnDietComplete.isSelected = menuItem.isDietCompleted
                btnDietComplete.setTextColor(Color.WHITE)

                // 카드 클릭 리스너
                root.setOnClickListener {
                    val oldPosition = selectedPosition
                    selectedPosition = adapterPosition
                    notifyItemChanged(oldPosition)
                    notifyItemChanged(selectedPosition)
                    onClick(menuItem, adapterPosition)
                }

                // 즐겨찾기 버튼 설정
                ivStar.setOnClickListener {
                    menuItem.isFavorite = !menuItem.isFavorite
                    ivStar.setImageResource(
                        if (menuItem.isFavorite) R.drawable.ic_star_filled
                        else R.drawable.ic_star
                    )
                    onFavoriteChanged?.invoke(menuItem, menuItem.isFavorite)
                }

                // 식단 완료 버튼 설정
                btnDietComplete.setOnClickListener {
                    menuItem.isDietCompleted = !menuItem.isDietCompleted
                    btnDietComplete.isSelected = menuItem.isDietCompleted
                    btnDietComplete.setBackgroundColor(
                        ContextCompat.getColor(
                            binding.root.context,
                            if (menuItem.isDietCompleted) R.color.Primary_Orange1 else R.color.Gray7
                        )
                    )
                    onDietCompleteChanged?.invoke(menuItem, menuItem.isDietCompleted)
                }

                // 랜덤 변경 버튼 클릭 리스너
                btnRefresh.setOnClickListener {
                    onRefresh(menuItem)
                }
            }
        }
    }

    fun clearSelection() {
        val oldPosition = selectedPosition
        selectedPosition = RecyclerView.NO_POSITION
        notifyItemChanged(oldPosition)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemMenuBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
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
                        Log.e("MenuItemAdapter", "이미지 URL이 비어 있음")
                    }
                } else {
                    Log.e("MenuItemAdapter", "API 호출 실패: ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e("MenuItemAdapter", "네트워크 오류: ${e.message}")
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    private class DiffCallback : DiffUtil.ItemCallback<MenuItem>() {
        override fun areItemsTheSame(oldItem: MenuItem, newItem: MenuItem) =
            oldItem.name == newItem.name

        override fun areContentsTheSame(oldItem: MenuItem, newItem: MenuItem) =
            oldItem == newItem
    }
}
