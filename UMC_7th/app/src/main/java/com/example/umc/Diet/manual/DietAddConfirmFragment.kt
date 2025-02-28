package com.example.umc.Diet.manual

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.umc.R
import com.example.umc.databinding.FragmentDietAddConfirmBinding
import com.example.umc.model.ManualMeals
import android.util.Log

class DietAddConfirmFragment : Fragment(R.layout.fragment_diet_add_confirm) {

    private var _binding: FragmentDietAddConfirmBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: DietAddConfirmAdapter
    private val viewModel: DietAddConfirmViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDietAddConfirmBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = DietAddConfirmAdapter(mutableListOf()) { position ->
            val mealId = viewModel.mealList.value?.get(position)?.mealId ?: return@DietAddConfirmAdapter
            viewModel.deleteManualMeals(mealId, position)
        }

        binding.rvAddConfirm.layoutManager = LinearLayoutManager(requireContext())
        binding.rvAddConfirm.adapter = adapter

        // 전달된 인자 확인
        val foods = arguments?.getString("foods")?.split(", ") ?: emptyList()
        val date = arguments?.getString("date")
        val time = arguments?.getString("time")
        val calories = arguments?.getInt("calories")

        Log.d("MealLogging", "전달된 foods: $foods")
        Log.d("MealLogging", "전달된 date: $date")
        Log.d("MealLogging", "전달된 time: $time")
        Log.d("MealLogging", "전달된 calories: $calories")

        viewModel.mealList.observe(viewLifecycleOwner) { mealList ->
            adapter.updateMeals(mealList)
        }

        viewModel.fetchManualMeals(
            requireContext(),
            onSuccess = {
                Log.d("MealLogging", "식단 가져오기 성공")
            },
            onError = { errorMsg: String ->
                Log.e("MealLogging", "식단 가져오기 실패: $errorMsg")
            }
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
