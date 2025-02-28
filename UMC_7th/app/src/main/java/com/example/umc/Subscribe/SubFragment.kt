package com.example.umc.Subscribe

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.umc.Main.MainActivity
import com.example.umc.R
import com.example.umc.cart.SubscribeCart
import com.example.umc.databinding.FragmentSubBinding
import com.example.umc.model.SubItem
import com.example.umc.Subscribe.SubAdapter
import com.example.umc.Subscribe.SubscriptionManageFragment
import com.example.umc.UserApi.SharedPreferencesManager

class SubFragment : Fragment() {

    private var _binding: FragmentSubBinding? = null
    private val binding get() = _binding!!

    private lateinit var subAdapter: SubAdapter
    private lateinit var subList: List<SubItem>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSubBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // ✅ SharedPreferences에서 저장된 name 가져오기
        val userName = SharedPreferencesManager.getUserName(requireContext()) ?: "사용자"

        // ✅ name을 tv_user_name에 적용
        binding.tvUserName.text = getString(R.string.serve).format(userName)

        subList = listOf(
            SubItem("맛있는 일상 음식 구독", "누구나 좋아하는 맛있는 일상 음식을 구독해보세요!"),
            SubItem("다이어트 식단 구독", "맛있는 다이어트 식단을 정기 배송받아 보세요!"),
            SubItem("건강 음식 구독", "당뇨, 고혈압 등 지병이 있는 분들께 추천해요!")
        )

        subAdapter = SubAdapter(subList) { subItem ->

            val fragmentDietSubFragment = DietSubFragment()
            val bundle = Bundle()
            bundle.putString("item1", subItem.item1)
            bundle.putString("item2", subItem.item2)
            fragmentDietSubFragment.arguments = bundle

            val transaction: FragmentTransaction = parentFragmentManager.beginTransaction()
            transaction.replace(R.id.main_container, fragmentDietSubFragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }

        binding.rvSubItem.layoutManager = LinearLayoutManager(context)
        binding.rvSubItem.adapter = subAdapter

        // bt_manage_sub 클릭 리스너 설정
        binding.llManageSubscription.setOnClickListener {
            navigateToSubscriptionManage()
        }

        binding.llCart.setOnClickListener {
            navigateToCart()
        }
    }

    private fun navigateToSubscriptionManage() {
        val transaction: FragmentTransaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.main_container, SubscriptionManageFragment())
        transaction.addToBackStack(null)
        transaction.commit()
    }

    private fun navigateToCart() {
        val transaction: FragmentTransaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.main_container, SubscribeCart())
        transaction.addToBackStack(null)
        transaction.commit()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Prevent memory leak
    }

    override fun onResume() {
        super.onResume()
        (activity as? MainActivity)?.hideTitle()
    }
}
