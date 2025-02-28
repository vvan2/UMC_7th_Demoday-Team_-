package com.example.umc.Diet

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.umc.model.Nutrition
import com.example.umc.R
import com.example.umc.UserApi.RetrofitClient
import com.example.umc.databinding.FragmentDietDetailBinding
import com.example.umc.model.request.PatchFavoriteDeleteRequest
import com.example.umc.model.request.PatchFavoriteRequest
import com.example.umc.model.request.PatchMealsDislikeDeleteRequest
import com.example.umc.model.request.PatchMealsDislikeRequest
import com.example.umc.model.request.PatchPreferenceRequest
import com.example.umc.model.request.PostCompleteMealRequest
import com.example.umc.model.response.GetMealsDetailSuccess
import kotlinx.coroutines.launch
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class DietDetailFragment : Fragment() {
    private var _binding: FragmentDietDetailBinding? = null
    private val binding get() = _binding!!

    private var isLiked = false  // 좋아요
    private var isDisliked = false // 싫어요
    private var isFavorited = false // 즐겨찾기
    private var isCompleted = false // 식단 완료
    private var isTooltipVisible = false // 툴팁
    private var popupWindow: PopupWindow? = null // 툴팁 열기

    private val viewModel: DietDetailViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDietDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mealId = arguments?.getInt("mealId") ?: return

        Log.d("DietDetailFragment", "전달된 mealId: $mealId")

        val token = viewModel.getAuthToken()

        viewModel.mealDetail.observe(viewLifecycleOwner) { mealDetail ->
            updateUI(mealDetail)
        }

        // 서버로부터 데이터 가져오기
        viewModel.fetchMealDetail(mealId, token)

        // 버튼 설정
        setupButtons(mealId, token)
    }

    private fun updateUI(mealDetail: GetMealsDetailSuccess) {
        binding.tvRecipeTitle.text = mealDetail.mealDetail.food
        binding.tvPrice.text = "약 ${mealDetail.mealDetail.price}원"
        binding.tvCalories.text = "${mealDetail.mealDetail.calorieTotal} kcal"
        binding.tvIngredients.text = mealDetail.mealDetail.material
        binding.tvRecipe.text = mealDetail.mealDetail.recipe

        val nutritionList = mealDetail.mealDetail.calorieDetail.split(", ").map { item ->
            val parts = item.split(": ")
            Nutrition(parts[0], parts[1])
        }

        binding.recyclerNutrition.layoutManager = LinearLayoutManager(context)
        binding.recyclerNutrition.adapter = DietDetailAdapter(nutritionList)

        val recipeSteps = mealDetail.mealDetail.recipe.split(Regex("(?=\\d\\.\\s)")).drop(1)
        binding.tvRecipe.text = recipeSteps.joinToString("\n")

        binding.ratingBar.rating = mealDetail.mealDetail.difficulty.toFloat()

        // Update the button states based on the fetched data
        isLiked = mealDetail.mealUser.isLike
        isDisliked = mealDetail.mealUser.isHate
        isFavorited = mealDetail.mealUser.isMark

        binding.btLike.setColorFilter(ContextCompat.getColor(requireContext(), if (isLiked) R.color.Primary_Orange1 else R.color.Gray7))
        binding.btDislike.setColorFilter(ContextCompat.getColor(requireContext(), if (isDisliked) R.color.Primary_Orange1 else R.color.Gray7))
        binding.btFavorite.setColorFilter(ContextCompat.getColor(requireContext(), if (isFavorited) R.color.Primary_Orange1 else R.color.Gray7))

        loadMealImage(mealDetail.mealDetail.food)
    }



    private fun setupButtons(mealId: Int, token: String) {
        // 좋아요 버튼 설정
        binding.btLike.setOnClickListener {
            isLiked = !isLiked
            binding.btLike.setColorFilter(ContextCompat.getColor(requireContext(), if (isLiked) R.color.Primary_Orange1 else R.color.Gray7))

            // 싫어요가 눌린 상태라면 해제
            if (isLiked && isDisliked) {
                isDisliked = false
                binding.btDislike.setColorFilter(ContextCompat.getColor(requireContext(), R.color.Gray7))
            }

            // 좋아요 상태가 true이면 선호도 API 요청
            if (isLiked) {
                val preferenceRequest = PatchPreferenceRequest(mealId = mealId)
                viewModel.addToPreference(preferenceRequest, token)
            }
        }

        binding.btDislike.setOnClickListener {
            isDisliked = !isDisliked
            binding.btDislike.setColorFilter(ContextCompat.getColor(requireContext(), if (isDisliked) R.color.Primary_Orange1 else R.color.Gray7))

            if (isDisliked && isLiked) {
                isLiked = false
                binding.btLike.setColorFilter(ContextCompat.getColor(requireContext(), R.color.Gray7))
            }

            if(isDisliked) {
                val dislikeRequest = PatchMealsDislikeRequest(mealId = mealId)
                viewModel.addToDislike(dislikeRequest, token)
            }

            if (!isDisliked) {
                val dislikeDeleteRequest = PatchMealsDislikeDeleteRequest(mealId = mealId)
                viewModel.deleteDislike(dislikeDeleteRequest, token)
            }
        }

        // 즐겨찾기 버튼 설정
        binding.btFavorite.setOnClickListener {
            isFavorited = !isFavorited
            binding.btFavorite.setColorFilter(ContextCompat.getColor(requireContext(), if (isFavorited) R.color.Primary_Orange1 else R.color.Gray7))

            if (isFavorited) {
                val favoriteRequest = PatchFavoriteRequest(mealId = mealId)
                viewModel.addToFavorite(favoriteRequest, token)
            }

            if (!isFavorited) {
                val favoriteDeleteRequest = PatchFavoriteDeleteRequest(mealId = mealId)
                viewModel.deleteFavorite(favoriteDeleteRequest, token)
            }
        }

        // 식단 완료 버튼 설정
        binding.btDietComplete.setOnClickListener {
            isCompleted = !isCompleted
            binding.btDietComplete.setBackgroundColor(ContextCompat.getColor(requireContext(), if (isCompleted) R.color.Primary_Orange1 else R.color.Gray7))

            // API 요청
            if (isCompleted) {
                val currentDate = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault()).format(Date())
                val completedRequest = PostCompleteMealRequest(mealId = mealId, mealDate = currentDate)
                viewModel.mealComplete(completedRequest, token)
            }
        }

        binding.btQuestion.setOnClickListener {
            if (popupWindow == null) {
                val tooltipView = layoutInflater.inflate(R.layout.dialog_tooltip, null)
                popupWindow = PopupWindow(tooltipView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
                    isOutsideTouchable = true // 다른 곳을 클릭하면 닫히도록 설정
                    setBackgroundDrawable(ContextCompat.getDrawable(requireContext(), android.R.color.transparent)) // 배경 투명 설정
                }
            }
            if (isTooltipVisible) {
                popupWindow?.dismiss()
            } else {
                popupWindow?.showAsDropDown(binding.btQuestion, -80, 0)
            }
            isTooltipVisible = !isTooltipVisible
        }
        popupWindow?.setTouchInterceptor { v, event ->
            if (event.action == MotionEvent.ACTION_OUTSIDE) {
                popupWindow?.dismiss()
                isTooltipVisible = false
                v.performClick() // 클릭 이벤트 호출
                true
            } else {
                false
            }
        }
    }

    private fun loadMealImage(foodName: String) {
        // 이미지 초기화
        binding.imgSample.setImageDrawable(null)

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.imageApiService.getMealImage(foodName)

                if (response.isSuccessful) {
                    val imageUrl = response.body()?.success?.imageUrl
                    if (!imageUrl.isNullOrEmpty()) {
                        Glide.with(requireContext())
                            .load(imageUrl)
                            .into(binding.imgSample) // 이미지 뷰에 적용
                        Log.d("FoodImage", "이미지 로드 성공: $imageUrl")
                    } else {
                        Log.e("FoodImage", "이미지 URL이 비어 있음")
                        Toast.makeText(context, "이미지 URL이 비어 있습니다.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Log.e("FoodImage", "API 호출 실패: ${response.message()}")
                    Toast.makeText(context, "API 호출 실패: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("FoodImage", "네트워크 오류: ${e.message}")
                Toast.makeText(context, "네트워크 오류: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
