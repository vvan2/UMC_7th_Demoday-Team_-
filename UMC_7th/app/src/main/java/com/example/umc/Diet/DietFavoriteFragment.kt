package com.example.umc.Diet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.example.umc.Main.MainActivity
import com.example.umc.R
import com.example.umc.databinding.FragmentFavoriteBinding
import com.example.umc.model.response.GetFavoriteMeals
import com.example.umc.model.response.GetFavoriteMealsResponse

class DietFavoriteFragment : Fragment() {
    private var _binding: FragmentFavoriteBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DietFavoriteViewModel by viewModels()
    private lateinit var adapter: DietFavoriteAdapter
    private var favoriteItems: List<GetFavoriteMeals> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvDietFavorite.layoutManager = GridLayoutManager(context, 2)

        // Adapter 설정
        val adapter = DietFavoriteAdapter { item, position ->
            onFavoriteItemClicked(item)
        }
        binding.rvDietFavorite.adapter = adapter

        // ViewModel Observe 설정
        viewModel.favoriteMeals.observe(viewLifecycleOwner) { mealList ->
            favoriteItems = mealList
            adapter.updateItems(mealList)
        }

        // 서버로부터 데이터 가져오기
        viewModel.fetchFavoriteMeals()

        // 텍스트뷰 클릭 리스너 설정
        binding.tvNew.setOnClickListener {
            binding.tvNew.setTextColor(ContextCompat.getColor(requireContext(), R.color.Primary_Orange1))
            binding.tvCalorie.setTextColor(ContextCompat.getColor(requireContext(), R.color.Gray7))

            viewModel.fetchFavoriteMeals()
        }

        binding.tvCalorie.setOnClickListener {
            binding.tvNew.setTextColor(ContextCompat.getColor(requireContext(), R.color.Gray7))
            binding.tvCalorie.setTextColor(ContextCompat.getColor(requireContext(), R.color.Primary_Orange1))

            // 칼로리 순으로 데이터 가져오기
            viewModel.getFavoriteMealsCalorie()
        }
    }

    private fun onFavoriteItemClicked(item: GetFavoriteMeals) {
        val dietDetailFragment = DietDetailFragment()

        val bundle = Bundle()
        bundle.putInt("mealId", item.mealId) // mealId를 Bundle에 추가
        bundle.putString("name", item.food)
        bundle.putString("calories", "${item.calorieTotal} kcal")
        dietDetailFragment.arguments = bundle

        val transaction: FragmentTransaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.main_container, dietDetailFragment)
        transaction.addToBackStack(null)
        transaction.commit()

        val mainActivity = activity as? MainActivity
        mainActivity?.showTitle("즐겨찾기", true)
        mainActivity?.hideBottomBar()
    }

    override fun onResume() {
        super.onResume()
        (activity as? MainActivity)?.showTitle("즐겨찾기", true)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
