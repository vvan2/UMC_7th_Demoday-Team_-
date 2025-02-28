package com.example.pricefruit

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.umc.Quote.FoodItem
import com.example.umc.R

class FoodPriceReFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Fragment의 layout을 inflate
        val binding = inflater.inflate(R.layout.activity_fruit_price, container, false)

        // ProgressBar 설정
        val scrollIndicator: ProgressBar = binding.findViewById(R.id.scrollIndicator)

        // 날짜 선택 설정
        val tvToday: TextView = binding.findViewById(R.id.tv_today)
        val dateSelector: View = binding.findViewById(R.id.date_selector)
        val dateList = arrayOf("오늘", "내일", "모레")

        dateSelector.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("날짜 선택")
                .setItems(dateList) { _, which ->
                    tvToday.text = dateList[which]
                }
                .show()
        }

        // 가로 RecyclerView 설정
        val horizontalRecyclerView: RecyclerView = binding.findViewById(R.id.horizontal_recycler_view)
        horizontalRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        val priceList = listOf("26,828원", "3166원/kg", "12,500원", "10,900원", "9,800원")
        horizontalRecyclerView.adapter = HorizontalRecyclerViewAdapter(priceList, this)

        // 스크롤 리스너 설정
        horizontalRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val offset = recyclerView.computeHorizontalScrollOffset()
                val extent = recyclerView.computeHorizontalScrollExtent()
                val range = recyclerView.computeHorizontalScrollRange()
                val progress = (offset.toFloat() / (range - extent) * 100).toInt()
                scrollIndicator.progress = progress
            }
        })

        // 세로 RecyclerView 설정
        val recyclerView: RecyclerView = binding.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
/*

        // itemList를 실제 과일 이름으로 변경
        val itemList = listOf(
            FoodItem("바나나", "26,828원", "1kg", "https://example.com/banana.jpg",""),
            FoodItem("딸기", "12,500원", "500g", "https://example.com/strawberry.jpg",""),
            FoodItem("토마토", "9,800원", "1kg", "https://example.com/tomato.jpg",""),
            FoodItem("샤인머스켓", "38,000원", "500g", "https://example.com/grapes.jpg",""),
            FoodItem("귤", "16,000원", "1kg", "https://example.com/tangerine.jpg",""),
            FoodItem("수박", "22,000원", "1개", "https://example.com/watermelon.jpg","")
        )
*//*

        // 이 Fragment에서 RecyclerView에 Adapter 설정
        recyclerView.adapter = GridRecyclerViewAdapter(itemList, this)  // 'this'는 현재 Fragment를 가리킴


*/

        return binding
    }
}
