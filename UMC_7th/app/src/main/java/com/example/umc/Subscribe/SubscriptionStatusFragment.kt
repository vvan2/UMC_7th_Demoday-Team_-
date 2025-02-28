package com.example.umc.Subscribe

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.umc.R
import com.example.umc.databinding.FragmentDietStatusBinding

class SubscriptionStatusFragment : Fragment() {
    private var _binding: FragmentDietStatusBinding? = null
    private val binding get() = _binding!!
    private var isSubscriber = true // 구독 상태 (실제로는 API에서 받아와야 함)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDietStatusBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupSubscriptionStatus()
    }



    private fun setupSubscriptionStatus() {
        binding.tvSubscriptionStatus.run {
            if (isSubscriber) {
                text = "정기구독회원"
                setBackgroundResource(R.drawable.bg_sub_status_orange)
            } else {
                text = "일반회원"
                setBackgroundResource(R.drawable.bg_sub_status_gray)
            }
        }
    }
//isSubscriber 값을 API 응답이나 로컬 데이터베이스에서 가져오도록 수정
//bg_sub_status_gray.xml 파일로 일반회원일 때의 회색 배경 스타일 정의
//로그인 상태 변경 시 setupSubscriptionStatus() 메서드 호출하여 UI 업데이트


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}