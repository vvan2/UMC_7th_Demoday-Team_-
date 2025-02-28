package com.example.umc

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.umc.databinding.ItemCategoryBinding
import com.example.umc.databinding.ItemProductBinding
import com.example.umc.model.Category
import com.example.umc.model.Product


// CategoryAdapter.kt
class CategoryAdapter(
    private val categories: List<Category>,
    private val onItemClick: (Category) -> Unit // 클릭 리스너 추가
) : RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemCategoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(category: Category) {
            binding.apply {
                categoryName.text = category.name
                categoryIcon.setImageResource(category.iconResId)

                root.setOnClickListener {
                    onItemClick(category) // 아이템 클릭 시 호출
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCategoryBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(categories[position])
    }

    override fun getItemCount() = categories.size
}


class ProductAdapter(
    private val products: List<Product>,
    private val onItemClick: (Product) -> Unit // 클릭 리스너 추가
) : RecyclerView.Adapter<ProductAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemProductBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(product: Product) {
            binding.apply {
                productName.text = product.name
                productPrice.text = "${product.price}원/${product.unit}"

                root.setOnClickListener {
                    onItemClick(product) // 아이템 클릭 시 호출
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemProductBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(products[position])
    }

    override fun getItemCount() = products.size
}
