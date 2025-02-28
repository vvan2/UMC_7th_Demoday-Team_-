package com.example.umc.Quote

import MaterialFavoriteAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.GridLayoutManager
import com.example.umc.Main.MainActivity
import com.example.umc.R
import com.example.umc.databinding.FragmentMaterialFavoriteBinding
import com.example.umc.model.FavoriteMaterialItem

class MaterialFavoriteFragment : Fragment() {
    private var _binding: FragmentMaterialFavoriteBinding? = null
    private val binding get() = _binding!!

    private val favoriteItems = listOf(
        FavoriteMaterialItem("바나나","g","1560"),
        FavoriteMaterialItem("팽이버섯", "100g", "1100")
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMaterialFavoriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvMaterialFavorite.layoutManager = GridLayoutManager(context, 2)

        // Adapter 설정
        val adapter = MaterialFavoriteAdapter { item, position ->
        }
        binding.rvMaterialFavorite.adapter = adapter

        // 어댑터에 아이템 설정
        adapter.updateItems(favoriteItems)
    }

    private fun onFavoriteItemClicked(item: MaterialFavoriteFragment) {
        val materialFavoriteFragment = MaterialFavoriteFragment()

        val bundle = Bundle()
        materialFavoriteFragment.arguments = bundle

        val transaction: FragmentTransaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.main_container, materialFavoriteFragment)
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