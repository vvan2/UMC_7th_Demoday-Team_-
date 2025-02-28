package com.example.umc.Quote.Sub

import QuoteViewModel
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.umc.Main.MainActivity
import com.example.umc.databinding.FragmentQuoteSubBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class QuoteFragmentSub : Fragment() {

    private var _binding: FragmentQuoteSubBinding? = null
    private val binding get() = _binding!!
    private val viewModel: QuoteViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentQuoteSubBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewPager: ViewPager2 = binding.viewPager
        val tabLayout: TabLayout = binding.tabLayout

        val adapter = ViewPagerAdapter(this)
        viewPager.adapter = adapter
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = adapter.getPageTitle(position)
        }.attach()

        // ViewModel의 데이터 관찰
        viewModel.rankingList.observe(viewLifecycleOwner, Observer { rankingItems ->
            val fragment = childFragmentManager.findFragmentByTag("f${viewPager.currentItem}") as? RankingFragment
            fragment?.updateRankingList(rankingItems)
        })

        // ViewPager 페이지 전환 이벤트 리스너 추가
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                val variety = adapter.getVariety(position)
                viewModel.fetchRankingList(variety)
            }
        })

        // 초기 탭 데이터 가져오기 (선택 사항)
        viewModel.fetchRankingList("채소류")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private inner class ViewPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
        private val tabTitles = arrayOf("채소", "육류", "과일", "어류/수산물")

        override fun getItemCount(): Int {
            return tabTitles.size
        }

        override fun createFragment(position: Int): Fragment {
            return RankingFragment().apply {
                arguments = Bundle().apply {
                    putString("variety", getVariety(position))
                }
            }
        }

        fun getPageTitle(position: Int): String {
            return tabTitles[position]
        }

        fun getVariety(position: Int): String {
            return when (position) {
                0 -> "채소류"
                1 -> "축산물"
                2 -> "과일류"
                3 -> "수산물"
                else -> ""
            }
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as? MainActivity)?.showTitle("제철", true)
    }
}
