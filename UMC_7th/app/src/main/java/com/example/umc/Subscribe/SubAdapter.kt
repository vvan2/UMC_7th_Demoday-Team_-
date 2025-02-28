package com.example.umc.Subscribe

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.umc.databinding.ItemSubMainBinding
import com.example.umc.databinding.ItemSubscriptionFooterBinding
import com.example.umc.model.SubItem

class SubAdapter(
    private val subList: List<SubItem>,
    private val itemClickListener: (SubItem) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_ITEM = 0
        private const val VIEW_TYPE_FOOTER = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_FOOTER -> {
                val binding = ItemSubscriptionFooterBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                FooterViewHolder(binding)
            }
            else -> {
                val binding = ItemSubMainBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                SubViewHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is SubViewHolder -> {
                if (position < subList.size) {
                    val subItem = subList[position]
                    holder.bind(subItem)
                }
            }
            is FooterViewHolder -> {
                // Footer는 별도의 바인딩이 필요없음
            }
        }
    }

    override fun getItemCount(): Int = subList.size + 1  // +1 for footer

    override fun getItemViewType(position: Int): Int {
        return if (position == subList.size) VIEW_TYPE_FOOTER else VIEW_TYPE_ITEM
    }

    inner class SubViewHolder(private val binding: ItemSubMainBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(subItem: SubItem) {
            binding.tvSubItem1.text = subItem.item1
            binding.tvSubItem2.text = subItem.item2

            binding.root.setOnClickListener {
                if (subItem.item1 == "맛있는 일상 음식 구독") {
                    itemClickListener(subItem)
                }
            }
        }
    }

    class FooterViewHolder(binding: ItemSubscriptionFooterBinding) : RecyclerView.ViewHolder(binding.root)
}