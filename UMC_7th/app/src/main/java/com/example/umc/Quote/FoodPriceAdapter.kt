/*
package com.example.umc.Quote

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.umc.databinding.ItemMidQualityBinding

class FoodPriceAdapter(private val items: List<FoodPriceViewModel.MidQuality>) : RecyclerView.Adapter<FoodPriceAdapter.FoodPriceViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodPriceViewHolder {
        val binding = ItemMidQualityBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FoodPriceViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FoodPriceViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    class FoodPriceViewHolder(private val binding: ItemMidQualityBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: FoodPriceViewModel.MidQuality) {
            binding.tvMidQualityName.text = item.name
            binding.tvMidQualityPrice.text = "${item.price}원"
            binding.tvMidQualityRate.text = "${item.rate}%"
        }
    }
}
*/
