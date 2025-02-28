package com.example.umc.Mypage

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.umc.R
import com.example.umc.databinding.FragmentMyAlarmBinding


class MyFragmentalarm : Fragment() {
    private var _binding: FragmentMyAlarmBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyAlarmBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListeners()
        setupSwitches()
    }

    private fun setupListeners() {
        // 뒤로가기 버튼 클릭 리스너
        binding.backButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun setupSwitches() {
        val switches = listOf(
            binding.switch1,
            binding.switch2,
            binding.switch3,
            binding.switch4,
            binding.switch5
        )

        val states = arrayOf(
            intArrayOf(android.R.attr.state_checked),
            intArrayOf(-android.R.attr.state_checked)
        )

        val colors = intArrayOf(
            ContextCompat.getColor(requireContext(), R.color.Primary_Orange1),
            ContextCompat.getColor(requireContext(), R.color.Gray7)
        )

        val colorStateList = ColorStateList(states, colors)

        switches.forEach { switch ->
            switch.thumbTintList = colorStateList
            switch.trackTintList = colorStateList
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}