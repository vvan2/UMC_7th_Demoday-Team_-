package com.example.umc.Quote

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.umc.Main.MainActivity
import com.example.umc.databinding.FragmentPriceDetailBinding

class PriceDetailFragment : Fragment() {

    private var _binding: FragmentPriceDetailBinding? = null
    private val binding get() = _binding!!
    private var categoryName: String? = null
    private val dateList = arrayOf("오늘")

    // ViewModel 초기화
    private lateinit var priceDetailViewModel: PriceDetailViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            categoryName = it.getString("category_name")
        }

        // ViewModel 초기화
        priceDetailViewModel = ViewModelProvider(this).get(PriceDetailViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPriceDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val scrollIndicator: ProgressBar = binding.scrollIndicator

        // 가로 RecyclerView 설정
        val horizontalRecyclerView: RecyclerView = binding.horizontalRecyclerView
        horizontalRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        // ViewModel에서 제공하는 데이터를 관찰하여 가로 RecyclerView에 반영
        priceDetailViewModel.varietyMaterialList.observe(viewLifecycleOwner, { materialList ->
            // materialList가 갱신될 때마다 어댑터에 데이터를 설정
            horizontalRecyclerView.adapter = HorizontalPriceDetailAdapter(materialList, this@PriceDetailFragment)
        })

        // 스크롤 리스너 설정
        horizontalRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                updateIndicator(recyclerView, scrollIndicator)
            }
        })

        // 초기 진행 상태 업데이트
        updateIndicator(horizontalRecyclerView, scrollIndicator)

        // 세로 RecyclerView 설정
        val recyclerView: RecyclerView = binding.recyclerView
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)

        // Variety 이름을 ViewModel에 전달하여 데이터 로드
        categoryName?.let {
            priceDetailViewModel.fetchVarietyMaterialList(it) // 서버에서 데이터를 불러옴
        }

        // 세로 RecyclerView 어댑터 설정
        priceDetailViewModel.varietyMaterialList.observe(viewLifecycleOwner, { materialList ->
            recyclerView.adapter = PriceDetailAdapter(materialList, this@PriceDetailFragment)
        })
    }

    private fun updateIndicator(recyclerView: RecyclerView, indicator: ProgressBar) {
        val totalWidth = recyclerView.computeHorizontalScrollRange()
        val visibleWidth = recyclerView.computeHorizontalScrollExtent()
        val scrollOffset = recyclerView.computeHorizontalScrollOffset()

        val scrollProgress = if (totalWidth - visibleWidth > 0) {
            scrollOffset.toFloat() / (totalWidth - visibleWidth)
        } else {
            0f
        }

        // ProgressBar의 위치를 고정하고, 진행 상태를 업데이트
        indicator.progress = (scrollProgress * 100).toInt()
    }

    override fun onResume() {
        super.onResume()
        (activity as? MainActivity)?.showTitle(categoryName ?: "Variety", true)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
