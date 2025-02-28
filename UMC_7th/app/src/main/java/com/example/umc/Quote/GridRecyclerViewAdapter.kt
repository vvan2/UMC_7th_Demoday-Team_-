package com.example.pricefruit

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.umc.Quote.FoodItem
import com.example.umc.R

class GridRecyclerViewAdapter(
    private val itemList: List<FoodItem>,
    private val fragment: Fragment // 현재 Fragment를 전달
) : RecyclerView.Adapter<GridRecyclerViewAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemImage: View = itemView.findViewById(R.id.item_image)
        val itemText: TextView = itemView.findViewById(R.id.item_text)
        val itemPrice: TextView = itemView.findViewById(R.id.item_price)

        init {
            // 아이템 클릭 시 처리할 코드 추가
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_grid, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val foodItem = itemList[position]
        holder.itemText.text = foodItem.name
        holder.itemPrice.text = foodItem.price

        // 이미지 로딩 코드 추가 (예: Glide 또는 Picasso 사용)
        // Glide.with(fragment).load(foodItem.imageUrl).into(holder.itemImage)
    }

    override fun getItemCount(): Int = itemList.size
}
