package com.example.umc.Quote.Sub

import QuoteViewModel
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.umc.R
import com.example.umc.UserApi.RetrofitClient
import com.example.umc.databinding.FragmentRankingBinding
import com.example.umc.model.RankingItem
import kotlinx.coroutines.launch

class RankingFragment : Fragment() {

    private var _binding: FragmentRankingBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: QuoteViewModel
    private val rankingList = mutableListOf<RankingItem>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRankingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireParentFragment()).get(QuoteViewModel::class.java)

        val variety = arguments?.getString("variety") ?: ""
        viewModel.fetchRankingList(variety)

        viewModel.rankingList.observe(viewLifecycleOwner, { rankingItems ->
            updateRankingList(rankingItems)
        })
    }

    fun updateRankingList(newRankingList: List<RankingItem>) {
        rankingList.clear()
        rankingList.addAll(newRankingList)
        updateCardViews()
    }

    private fun updateCardViews() {
        val cardViews = listOf(
            binding.cvRanking1 to binding.tvMaterial1,
            binding.cvRanking2 to binding.tvMaterial2,
            binding.cvRanking3 to binding.tvMaterial3,
            binding.cvRanking4 to binding.tvMaterial4,
            binding.cvRanking5 to binding.tvMaterial5
        )

        for ((index, pair) in cardViews.withIndex()) {
            if (index < rankingList.size) {
                val rankingItem = rankingList[index]
                pair.second.text = rankingItem.name
                // Directly reference the ImageView from the binding object
                val imageView = when (index) {
                    0 -> binding.darkenedImageView // Assuming these are the correct names from your layout
                    1 -> binding.darkenedImageView2
                    2 -> binding.darkenedImageView3
                    3 -> binding.darkenedImageView4
                    4 -> binding.darkenedImageView5
                    else -> null
                }
                imageView?.let {
                    loadMaterialImage(rankingItem.name, it)
                }
            } else {
                pair.first.visibility = View.GONE
            }
        }
    }

    private fun loadMaterialImage(foodName: String, imageView: ImageView) {
        imageView.setImageDrawable(null)

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.imageApiService.getMaterialImage(foodName)

                if (response.isSuccessful) {
                    val imageUrl = response.body()?.success?.imageUrl
                    if (!imageUrl.isNullOrEmpty()) {
                        Glide.with(requireContext())
                            .load(imageUrl)
                            .into(imageView)
                    } else {
                        imageView.setImageResource(R.drawable.img_sample)
                    }
                } else {
                    imageView.setImageResource(R.drawable.img_sample)
                }
            } catch (e: Exception) {
                imageView.setImageResource(R.drawable.img_sample)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
