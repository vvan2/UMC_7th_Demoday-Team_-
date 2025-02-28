package com.example.umc.Diet.manual

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.umc.databinding.ItemAddConfirmBinding
import com.example.umc.model.ManualMeals
import android.util.Log
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class DietAddConfirmAdapter(
    private var mealList: MutableList<ManualMeals>,
    private val onDeleteClick: (Int) -> Unit // Lambda to handle delete action
) : RecyclerView.Adapter<DietAddConfirmAdapter.MealViewHolder>() {

    inner class MealViewHolder(private val binding: ItemAddConfirmBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(mealData: ManualMeals) {

            // ISO-8601 형식을 MM/dd 형식으로 변환
            val isoFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val outputFormat = SimpleDateFormat("MM/dd", Locale.getDefault())
            val formattedDate = try {
                if (mealData.mealDate.isNotBlank()) {
                    val date = isoFormat.parse(mealData.mealDate)
                    outputFormat.format(date)
                } else {
                    "Unknown" // Response에 없음
                }
            } catch (e: ParseException) {
                Log.e("MealLogging", "날짜 변환 오류: ${e.message}")
                "Unknown" // Response에 없음
            }

            binding.tvMealDate.text = formattedDate
            binding.tvMealTime.text = mealData.time.ifBlank { "Unknown" }
            binding.tvMealFoods.text = if (mealData.foods.isEmpty()) "No food items" else mealData.foods.joinToString(", ")
            binding.tvMealCalorie.text = mealData.calorieTotal.toString()

            binding.ibDelete.setOnClickListener {
                onDeleteClick(adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MealViewHolder {
        val binding = ItemAddConfirmBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MealViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MealViewHolder, position: Int) {
        Log.d("MealLogging", "Binding meal at position: $position")
        holder.bind(mealList[position])
    }

    override fun getItemCount(): Int = mealList.size

    fun updateMeals(newMeals: List<ManualMeals>) {
        Log.d("MealLogging", "Updating meal list in adapter")
        mealList = newMeals.toMutableList()
        notifyDataSetChanged()
    }

    fun removeItem(position: Int) {
        mealList.removeAt(position)
        notifyItemRemoved(position)
    }
}
