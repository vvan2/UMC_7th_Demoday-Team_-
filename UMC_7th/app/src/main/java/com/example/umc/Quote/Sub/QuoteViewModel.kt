import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.umc.model.RankingItem
import com.example.umc.UserApi.RetrofitClient
import kotlinx.coroutines.launch

class QuoteViewModel : ViewModel() {
    val rankingList = MutableLiveData<List<RankingItem>>()

    fun fetchRankingList(variety: String) {
        val correctedVariety = when (variety) {
            "육류" -> "축산물"
            "어류/수산물" -> "수산물"
            else -> variety
        }

        Log.d("QuoteViewModel", "Fetching ranking list for variety: $correctedVariety")

        viewModelScope.launch {
            try {
                val response = RetrofitClient.materialApiService.getMaterialRankVariety(correctedVariety)
                Log.d("QuoteViewModel", "API Response: ${response.body()}")
                if (response.isSuccessful) {
                    val data = response.body()?.success?.data ?: emptyList()
                    Log.d("QuoteViewModel", "API Success: Received ${data.size} items for variety: $correctedVariety")

                    val mappedRankingItems = data.mapIndexed { index, item ->
                        RankingItem(
                            variety = item.variety.name,
                            name = item.name,
                            rank = (index + 1).toString(),
                            itemId = "123",
                            unit = "kg",
                            price = "",
                            delta = 10.0,
                            imgUrl = "https://example.com/default.jpg"
                        )
                    }

                    rankingList.postValue(mappedRankingItems)
                } else {
                    Log.e("QuoteViewModel", "API Error: ${response.code()} - ${response.message()}")
                    rankingList.postValue(emptyList())
                }
            } catch (e: Exception) {
                Log.e("QuoteViewModel", "API Exception: ${e.message}", e)
                rankingList.postValue(emptyList())
            }
        }
    }
}
