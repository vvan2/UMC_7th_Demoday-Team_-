package com.example.umc.Diet

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.umc.model.Nutrition
import com.example.umc.databinding.ItemNutritionBinding

class DietDetailAdapter(private val nutritionList: List<Nutrition>) :
    RecyclerView.Adapter<DietDetailAdapter.NutritionViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NutritionViewHolder {
        val binding = ItemNutritionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NutritionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NutritionViewHolder, position: Int) {
        val nutrition = nutritionList[position]
        holder.bind(nutrition)
    }

    override fun getItemCount(): Int = nutritionList.size

    class NutritionViewHolder(private val binding: ItemNutritionBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(nutrition: Nutrition) {
            binding.textNutrientName.text = nutrition.name
            binding.textNutrientValue.text = nutrition.value
        }
    }
}
