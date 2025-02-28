package com.example.umc

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.umc.Main.MainActivity
import com.example.umc.UserApi.SharedPreferencesManager
import com.example.umc.databinding.FragmentAnimationBinding

class AnimationFragment : Fragment(R.layout.fragment_animation) {

    interface AnimationCompleteListener {
        fun onAnimationComplete()
    }

    private var _binding: FragmentAnimationBinding? = null
    private val binding get() = _binding!!

    private lateinit var aniNameTextView: TextView

    private lateinit var texts: List<TextView>
    private lateinit var imageViews: List<ImageView>
    private lateinit var linearLayout: View

    private var handler: Handler? = null
    private var animationCompleteListener: AnimationCompleteListener? = null

    companion object {
        private const val ANIMATION_STEP_DURATION = 1000L
        private const val ANIMATION_START_DELAY = 500L
        private const val ANIMATION_COMPLETE_DELAY = 5500L
        private const val Y_TRANSLATION_STEP = 50f
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handler = Handler(Looper.getMainLooper())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAnimationBinding.inflate(inflater, container, false)

        // View 초기화
        aniNameTextView = binding.aniName
        texts = listOf(binding.textView1, binding.textView2, binding.textView3)
        imageViews = listOf(binding.icAnima2, binding.icAnima3, binding.icAnima4, binding.icAnima5)
        linearLayout = binding.textlinear // LinearLayout의 ID를 확인하고 바인딩해주세요
        val userName = SharedPreferencesManager.getUserName(requireContext())

        // 3. userName이 존재하면 텍스트에 설정
        aniNameTextView.text = userName ?: "사용자"
        // 1단계: 초기 설정
        initializeStep1()

        binding.completeButton.setOnClickListener {
            navigateToMainActivity()
        }

        // 애니메이션 시작
        startAnimation()
        binding.lotti.setAnimation(R.raw.loading)  // 직접 경로를 지정하여 애니메이션 로드
        binding.lotti.playAnimation()  // 애니메이션 시작


        return binding.root

    }

    fun setAnimationCompleteListener(listener: AnimationCompleteListener) {
        animationCompleteListener = listener
    }

    private fun initializeStep1() {
        // 1단계: dot 숨기기, 텍스트 색상 초기화, 이미지 위치 초기화
        texts.forEach { it.setTextColor(ContextCompat.getColor(requireContext(), R.color.Gray7)) }
        imageViews.forEach { it.translationY = 0f }
        binding.completeButton.visibility = View.GONE
    }

    private fun startAnimation() {
        val step2Animation = createStep2Animation()
        val step3Animation = createStep3Animation()
        val step4Animation = createStep4Animation()

        AnimatorSet().apply {
            playSequentially(
                step2Animation,
                step3Animation,
                step4Animation
            )
            startDelay = ANIMATION_START_DELAY
            start()
        }
    }

    private fun createStep2Animation(): AnimatorSet {

        // 텍스트 색상 변경 애니메이션
        val textColorAnimation = ValueAnimator.ofArgb(
            ContextCompat.getColor(requireContext(), R.color.Gray7),
            ContextCompat.getColor(requireContext(), R.color.Gray2)
        ).apply {
            duration = ANIMATION_STEP_DURATION
            addUpdateListener { texts[0].setTextColor(it.animatedValue as Int) }
        }
        // 이미지 이동 애니메이션
        val imageAnimation = ObjectAnimator.ofFloat(
            imageViews[0],
            "translationY",
            0f,
            -Y_TRANSLATION_STEP
        ).apply {
            duration = ANIMATION_STEP_DURATION
        }

        return AnimatorSet().apply {
            playTogether(textColorAnimation, imageAnimation)
        }
    }


    private fun createStep3Animation(): AnimatorSet {


        val textColorAnimation = ValueAnimator.ofArgb(
            ContextCompat.getColor(requireContext(), R.color.Gray7),
            ContextCompat.getColor(requireContext(), R.color.Gray2)
        ).apply {
            duration = ANIMATION_STEP_DURATION
            addUpdateListener { texts[1].setTextColor(it.animatedValue as Int) }
        }

        val imageAnimation = ObjectAnimator.ofFloat(
            imageViews[1],
            "translationY",
            -Y_TRANSLATION_STEP,
            -(Y_TRANSLATION_STEP * 2)
        ).apply {
            duration = ANIMATION_STEP_DURATION
        }

        return AnimatorSet().apply {
            play(textColorAnimation).with(imageAnimation)
        }
    }

    private fun createStep4Animation(): AnimatorSet {
        // 4단계: 세 번째 dot 표시 후 모든 dot/text 숨기기, 이미지 최종 이동, 버튼 표시
        val fadeOutAnimation = ObjectAnimator.ofFloat(linearLayout, "alpha", 1f, 0f).apply {
            duration = ANIMATION_STEP_DURATION
            startDelay = ANIMATION_STEP_DURATION
        }

        val imageAnimation = ObjectAnimator.ofFloat(
            imageViews[2],
            "translationY",
            -(Y_TRANSLATION_STEP * 2),
            -(Y_TRANSLATION_STEP * 3)
        ).apply {
            duration = ANIMATION_STEP_DURATION
        }

        val buttonAnimation = ObjectAnimator.ofFloat(binding.completeButton, "alpha", 0f, 1f).apply {
            duration = ANIMATION_STEP_DURATION
            startDelay = ANIMATION_STEP_DURATION * 2
        }

        return AnimatorSet().apply {
            play(imageAnimation).before(fadeOutAnimation)
            play(fadeOutAnimation).before(buttonAnimation)
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    binding.lotti.visibility = View.INVISIBLE
                    binding.completeButton.visibility = View.VISIBLE
                    animationCompleteListener?.onAnimationComplete()
                }
            })
        }
    }
    private fun navigateToMainActivity() {
        val intent = Intent(requireContext(), MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
        handler?.removeCallbacksAndMessages(null)
        handler = null
    }
}