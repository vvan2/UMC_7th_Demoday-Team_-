package com.example.pricefruit

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.umc.R

class HorizontalRecyclerViewAdapter(
    private val itemList: List<String>,
    private val fragment: Fragment // 'FoodPriceReFragment' 대신 'Fragment'를 받음
) : RecyclerView.Adapter<HorizontalRecyclerViewAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemImage: View = itemView.findViewById(R.id.item_image)
        val itemText: TextView = itemView.findViewById(R.id.item_text)
        val itemPrice: TextView = itemView.findViewById(R.id.item_price)

        init {
            // 아이템 클릭 시 FoodPriceReFragment로 전환
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val foodItemName = itemList[position]

                    Log.d("HorizontalRecyclerViewAdapter", "클릭됨: $foodItemName") // 클릭된 아이템 로그 출력

                    // 새로운 Fragment 인스턴스 생성
                    val foodPriceFragment = FoodPriceReFragment()
                    val bundle = Bundle().apply {
                        putString("food_name", foodItemName)  // food 이름 전달
                        putString("food_price", "가격 예시")  // 가격 예시를 추가 (실제 데이터로 교체)
                        putString("price_unit", "단위 예시")  // 단위 예시를 추가 (실제 데이터로 교체)
                        putString("price_percent", "+3.8%") // 예시로 % 추가
                    }
                    foodPriceFragment.arguments = bundle

                    // 프래그먼트 교체
                    fragment.requireActivity().supportFragmentManager.beginTransaction().apply {
                        replace(R.id.fragment_container, foodPriceFragment)  // 해당 컨테이너에 교체
                        addToBackStack(null) // 백스택에 추가하여 뒤로가기 가능
                        commit() // 트랜잭션 실행
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_horizontal, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val foodItemName = itemList[position]
        holder.itemText.text = foodItemName  // itemList에 저장된 텍스트 값 설정
        holder.itemPrice.text = "가격 예시" // 가격 텍스트 설정 (실제 데이터로 교체)
    }

    override fun getItemCount(): Int = itemList.size
}
