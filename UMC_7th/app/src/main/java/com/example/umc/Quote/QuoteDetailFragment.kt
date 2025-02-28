package com.example.umc.Quote

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.umc.Diet.DietDetailAdapter
import com.example.umc.R
import com.example.umc.databinding.FragmentQuoteDetailBinding
import com.example.umc.model.Nutrition

class QuoteDetailFragment : Fragment() {
    private var _binding: FragmentQuoteDetailBinding? = null
    private val binding get() = _binding!!
    private var popupWindow: PopupWindow? = null // 툴팁 열기

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentQuoteDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 버튼 설정
        setupButtons()

        binding.tvRecipeTitle.text = "이모카세 김"

        // 더미 데이터
        val nutritionList = listOf(
            Nutrition("계란", "70"),
            Nutrition("식빵", "80"),
            Nutrition("바나나", "100"),
            Nutrition("버터", "35"),
            Nutrition("올리브유", "45")
        )

        val recipeSteps = listOf(
            "식빵을 토스터기나 후라이팬에 약불로 데워주세요.",
            "후라이팬에 약간의 기름을 두른 후 계란을 올려주세요.",
            "바나나와 함께 토스트를 섭취"
        )

        val dummyPrice = 4000 // 예상 가격
        val dummyCalories = 350 // 예상 열량

        // 가격, 열량 연결
        binding.tvPrice.text = getString(R.string.diet_price_contents, dummyPrice)
        binding.tvCalories.text = getString(R.string.diet_calories_contents, dummyCalories)

        // 열량 테이블
        binding.recyclerNutrition.layoutManager = LinearLayoutManager(context)
        binding.recyclerNutrition.adapter = DietDetailAdapter(nutritionList)

        // 필요 식재료 및 레시피 출력
        binding.tvIngredients.text = buildString {
            nutritionList.forEachIndexed { index, nutrition ->
                append("${index + 1}. ${nutrition.name}\n")
            }
        }

        binding.tvRecipe.text = buildString {
            recipeSteps.forEachIndexed { index, step ->
                append("${index + 1}. $step\n")
            }
        }
    }

    private fun setupButtons() {
        // 물음표 버튼 설정
        binding.btQuestion.setOnClickListener {
            if (popupWindow == null) {
                val tooltipView = layoutInflater.inflate(R.layout.dialog_tooltip, null)
                popupWindow = PopupWindow(tooltipView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            }
            if (popupWindow?.isShowing == true) {
                popupWindow?.dismiss()
            } else {
                popupWindow?.showAsDropDown(binding.btQuestion, -180, 0)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
