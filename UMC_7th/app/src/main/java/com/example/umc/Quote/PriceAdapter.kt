package com.example.umc.Quote

import com.example.umc.R
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.umc.model.Product
import com.example.umc.UserApi.RetrofitClient
import kotlinx.coroutines.launch

class PriceAdapter(
    private var productList: List<Product>,
    private val onClick: (Product) -> Unit
) : RecyclerView.Adapter<PriceAdapter.ViewHolder>() {

    fun updateData(newProductList: List<Product>) {
        productList = newProductList
        notifyDataSetChanged()  // 변경된 데이터 적용
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productImage: ImageView = itemView.findViewById(R.id.productImage)
        val productName: TextView = itemView.findViewById(R.id.productName)
        val productPrice: TextView = itemView.findViewById(R.id.productPrice)

        fun bind(product: Product) {
            Log.d("PriceAdapter", "Delta Value: ${product.price}") // delta 값 로그로 확인
            productName.text = product.name.split("/")[0]
            productPrice.text = "${product.price}원 / ${product.unit}"
            loadProductImage(product.name, productImage)

            itemView.setOnClickListener {
                onClick(product)
            }
        }

        private fun loadProductImage(foodName: String, imageView: ImageView) {
            val context = imageView.context
            (context as? LifecycleOwner)?.lifecycleScope?.launch {
                try {
                    val response = RetrofitClient.imageApiService.getMaterialImage(foodName)

                    if (response.isSuccessful) {
                        val imageUrl = response.body()?.success?.imageUrl
                        if (!imageUrl.isNullOrEmpty()) {
                            Glide.with(context)
                                .load(imageUrl)
                                .into(imageView)
                        } else {
                            Log.e("ProductAdapter", "이미지 URL이 비어 있음")
                        }
                    } else {
                        Log.e("ProductAdapter", "API 호출 실패: ${response.message()}")
                    }
                } catch (e: Exception) {
                    Log.e("ProductAdapter", "네트워크 오류: ${e.message}")
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_product, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(productList[position])
    }

    override fun getItemCount(): Int = productList.size
}
