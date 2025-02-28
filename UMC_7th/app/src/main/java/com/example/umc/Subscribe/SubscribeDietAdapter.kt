package com.example.umc.Subscribe

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.umc.Diet.DietItem
import com.example.umc.R
import com.example.umc.databinding.ItemDietSubBinding

class SubscribeDietAdapter(private val dietList: List<DietItem>, private val listener: OnDietCheckedChangeListener) :
    RecyclerView.Adapter<SubscribeDietAdapter.DietViewHolder>() {

    private val groupedDietList = dietList.groupBy { it.mealDate }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DietViewHolder {
        val binding = ItemDietSubBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DietViewHolder(binding, listener)
    }

    override fun onBindViewHolder(holder: DietViewHolder, position: Int) {
        val date = groupedDietList.keys.toList()[position]
        val items = groupedDietList[date] ?: emptyList()
        holder.bind(date, items)
    }

    override fun getItemCount(): Int = groupedDietList.size

    inner class DietViewHolder(private val binding: ItemDietSubBinding, private val listener: OnDietCheckedChangeListener) : RecyclerView.ViewHolder(binding.root) {

        private lateinit var items: List<DietItem> // 클래스 멤버 변수로 선언

        fun bind(date: String, items: List<DietItem>) {
            this.items = items // 초기화
            binding.tvDate.text = date
            binding.tvWeek.text = items.firstOrNull()?.week ?: ""

            // 기본적으로 모든 시간대를 숨김
            binding.llSubMealBreakfast.visibility = View.GONE
            binding.llSubMealLunch.visibility = View.GONE
            binding.llSubMealDinner.visibility = View.GONE

            // 시간대에 따라 가시성을 설정
            items.forEach { dietItem ->
                when (dietItem.time) {
                    "아침" -> {
                        binding.llSubMealBreakfast.visibility = View.VISIBLE
                        binding.tvFoodBreakfast.text = dietItem.food
                        binding.ibtnCheckBreakfast.setImageResource(if (dietItem.isChecked) R.drawable.ic_check else R.drawable.ic_uncheck)
                        binding.ibtnCheckBreakfast.setOnClickListener {
                            dietItem.isChecked = !dietItem.isChecked
                            binding.ibtnCheckBreakfast.setImageResource(if (dietItem.isChecked) R.drawable.ic_check else R.drawable.ic_uncheck)
                            updateBackground()
                            listener.onDietCheckedChange(dietItem.isChecked)
                        }
                    }
                    "점심" -> {
                        binding.llSubMealLunch.visibility = View.VISIBLE
                        binding.tvFoodLunch.text = dietItem.food
                        binding.ibtnCheckLunch.setImageResource(if (dietItem.isChecked) R.drawable.ic_check else R.drawable.ic_uncheck)
                        binding.ibtnCheckLunch.setOnClickListener {
                            dietItem.isChecked = !dietItem.isChecked
                            binding.ibtnCheckLunch.setImageResource(if (dietItem.isChecked) R.drawable.ic_check else R.drawable.ic_uncheck)
                            updateBackground()
                            listener.onDietCheckedChange(dietItem.isChecked)
                        }
                    }
                    "저녁" -> {
                        binding.llSubMealDinner.visibility = View.VISIBLE
                        binding.tvFoodDinner.text = dietItem.food
                        binding.ibtnCheckDinner.setImageResource(if (dietItem.isChecked) R.drawable.ic_check else R.drawable.ic_uncheck)
                        binding.ibtnCheckDinner.setOnClickListener {
                            dietItem.isChecked = !dietItem.isChecked
                            binding.ibtnCheckDinner.setImageResource(if (dietItem.isChecked) R.drawable.ic_check else R.drawable.ic_uncheck)
                            updateBackground()
                            listener.onDietCheckedChange(dietItem.isChecked)
                        }
                    }
                }
            }

            updateBackground()
        }

        private fun updateBackground() {
            val isAnyChecked = items.any { it.isChecked }
            binding.root.setBackgroundResource(
                if (isAnyChecked) R.drawable.bg_diet_sub_selected else R.drawable.bg_diet_sub_unselected
            )
        }
    }
}

interface OnDietCheckedChangeListener {
    fun onDietCheckedChange(isAnyChecked: Boolean)
}
