package com.example.umc.Subscribe

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.umc.databinding.ItemDeliveryInfoBinding

class DeliveryInfoAdapter : RecyclerView.Adapter<DeliveryInfoViewHolder>() {
    private var item: DeliveryInfo? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeliveryInfoViewHolder {
        val binding = ItemDeliveryInfoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return DeliveryInfoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DeliveryInfoViewHolder, position: Int) {
        item?.let { holder.bind(it) }
    }

    override fun getItemCount() = if (item != null) 1 else 0

    fun submitData(newItem: DeliveryInfo) {
        item = newItem
        notifyDataSetChanged()
    }
}