package com.example.umc.Diet

import com.example.umc.Diet.manual.DietAddManualViewModel
import com.example.umc.Diet.HomePagerAdapter
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.umc.Diet.manual.DietAddManualFragment
import com.example.umc.Main.MainActivity
import com.example.umc.Mypage.MyFragment
import com.example.umc.R
import com.example.umc.databinding.FragmentHomeContainerBinding
import com.google.android.material.tabs.TabLayoutMediator

class HomeContainerFragment : Fragment() {
    private var _binding: FragmentHomeContainerBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeContainerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewPager()
        setupTabLayout()
        setupButtons()
        binding.tvServe.text = getString(R.string.serve).format("토미")
    }

    private fun setupViewPager() {
        val pagerAdapter = HomePagerAdapter(requireActivity())
        binding.viewPager.apply {
            adapter = pagerAdapter
            isUserInputEnabled = false  // 스와이프 비활성화
        }
    }

    private fun setupTabLayout() {
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = "오늘"
                1 -> tab.text = "월간"
            }
        }.attach()
    }

    private fun setupButtons() {
        binding.btAddManual.setOnClickListener {
            val transaction: FragmentTransaction = parentFragmentManager.beginTransaction()
            transaction.replace(R.id.main_container, DietAddManualFragment())
            transaction.addToBackStack(null)
            transaction.commit()
        }

        binding.settingsButton.setOnClickListener {
            val transaction = parentFragmentManager.beginTransaction()
            transaction.replace(R.id.main_container, MyFragment())
            transaction.addToBackStack(null)
            transaction.commit()
        }

        binding.questionButton.setOnClickListener {
            val dialog = Dialog(requireContext(), R.style.DialogTheme)
            dialog.setContentView(R.layout.dialog_tooltip)

            dialog.window?.apply {
                setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                setDimAmount(0.5f)

                val layoutParams = WindowManager.LayoutParams().apply {
                    copyFrom(dialog.window?.attributes)
                    width = WindowManager.LayoutParams.WRAP_CONTENT
                    height = WindowManager.LayoutParams.WRAP_CONTENT
                    gravity = Gravity.TOP or Gravity.START
                    x = -50 // X 좌표 설정 (왼쪽으로 이동)
                    y = 30 // Y 좌표 설정 (아래로 이동)
                }
                dialog.window?.attributes = layoutParams
            }

            dialog.findViewById<View>(android.R.id.content).setOnClickListener {
                dialog.dismiss()
            }

            dialog.setOnShowListener {
                dialog.window?.decorView?.setOnTouchListener { view, event ->
                    if (event.action == MotionEvent.ACTION_DOWN) {
                        dialog.dismiss()
                        view.performClick()
                        true
                    } else {
                        false
                    }
                }
            }
            dialog.show()
        }


        binding.markButton.setOnClickListener {
            val transaction = parentFragmentManager.beginTransaction()
            transaction.replace(R.id.main_container, DietFavoriteFragment())
            transaction.addToBackStack(null)
            transaction.commit()

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        (activity as? MainActivity)?.hideTitle()
    }
}
