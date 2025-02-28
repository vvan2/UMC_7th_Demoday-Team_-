import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.umc.UserApi.RetrofitClient
import com.example.umc.databinding.ItemMaterialFavoriteBinding // 바인딩 클래스 변경
import com.example.umc.model.FavoriteMaterialItem
import kotlinx.coroutines.launch

class MaterialFavoriteAdapter (
    private val onClick: (FavoriteMaterialItem, Int) -> Unit
) : RecyclerView.Adapter<MaterialFavoriteAdapter.ViewHolder>() {

    private val favoriteMaterialItem = mutableListOf<FavoriteMaterialItem>()

    inner class ViewHolder(private val binding: ItemMaterialFavoriteBinding) : // 바인딩 클래스 변경
        RecyclerView.ViewHolder(binding.root) {

        fun bind(favoriteMaterialItem: FavoriteMaterialItem) {
            binding.apply {
                // XML에 있는 올바른 ID로 바인딩
                tvFavoriteName.text = favoriteMaterialItem.material // 변경 필요할 수도 있음
                tvFavoritePrice.text = favoriteMaterialItem.price // 변경 필요할 수도 있음
                tvFavoriteUnit.text = favoriteMaterialItem.unit // 변경 필요할 수도 있음

                loadMaterialImage(favoriteMaterialItem.material, ivFavoriteImage) // 이미지 로드
                root.setOnClickListener {
                    onClick(favoriteMaterialItem, adapterPosition)
                }
            }
        }
    }

    private fun loadMaterialImage(foodName: String, imageView: ImageView) {
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
                        Log.e("MaterialFavoriteAdapter", "이미지 URL이 비어 있음")
                    }
                } else {
                    Log.e("MaterialFavoriteAdapter", "API 호출 실패: ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e("MaterialFavoriteAdapter", "네트워크 오류: ${e.message}")
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemMaterialFavoriteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(favoriteMaterialItem[position])
    }

    override fun getItemCount(): Int = favoriteMaterialItem.size

    fun updateItems(items: List<FavoriteMaterialItem>) {
        favoriteMaterialItem.clear()
        favoriteMaterialItem.addAll(items)
        notifyDataSetChanged()
    }
}
