package com.example.umc.Subscribe

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.umc.R

class OrderMenuViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    private val deliveryDateTv = view.findViewById<TextView>(R.id.deliveryDateTv)
    private val breakfastTitleTv = view.findViewById<TextView>(R.id.breakfastTitleTv)
    private val breakfastMenuTv = view.findViewById<TextView>(R.id.breakfastMenuTv)
    private val breakfastKcalTv = view.findViewById<TextView>(R.id.breakfastKcalTv)
    private val breakfastPriceTv = view.findViewById<TextView>(R.id.breakfastPriceTv)
    private val lunchTitleTv = view.findViewById<TextView>(R.id.lunchTitleTv)
    private val lunchMenuTv = view.findViewById<TextView>(R.id.lunchMenuTv)
    private val lunchKcalTv = view.findViewById<TextView>(R.id.lunchKcalTv)
    private val lunchPriceTv = view.findViewById<TextView>(R.id.lunchPriceTv)

    fun bind(item: OrderMenuItem) {
        deliveryDateTv.text = item.menuDate
        breakfastTitleTv.text = "아침"
        breakfastMenuTv.text = item.breakfastName
        breakfastKcalTv.text = "${item.breakfastKcal}Kcal"
        breakfastPriceTv.text = "${item.breakfastPrice}원"
        lunchTitleTv.text = "점심"
        lunchMenuTv.text = item.lunchName
        lunchKcalTv.text = "${item.lunchKcal}Kcal"
        lunchPriceTv.text = "${item.lunchPrice}원"
    }
}
