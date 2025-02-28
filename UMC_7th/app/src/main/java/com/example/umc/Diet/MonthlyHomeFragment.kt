package com.example.umc.Diet

import android.content.Context
import android.os.Bundle
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.umc.Main.MainActivity
import com.example.umc.R
import com.example.umc.databinding.FragmentMonthlyHomeBinding
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade

class MonthlyHomeFragment : Fragment() {
    private var _binding: FragmentMonthlyHomeBinding? = null
    private val binding get() = _binding!!
    private var selectedDate: CalendarDay? = null // 선택된 날짜를 저장할 변수

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMonthlyHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupCalendar()
    }

    private fun setupCalendar() {
        val selectedMonth = CalendarDay.today().month

        val selectedMonthDecorator = SelectedMonthDecorator(requireContext(), selectedMonth)
        binding.calendarView.addDecorator(selectedMonthDecorator)

        // 상단 날짜 커스텀
        binding.calendarView.setTitleFormatter { day ->
            "${day.month}월"
        }

        // 월이 바뀔 때 기존 데코레이터 제거하고 새로 추가
        binding.calendarView.setOnMonthChangedListener { _, date ->
            binding.calendarView.removeDecorators()
        }

        // 날짜 클릭 이벤트 처리
        binding.calendarView.setOnDateChangedListener { widget, date, selected ->
            if (selected) {
                selectedDate = date

                // 선택한 날짜 데코레이터 추가
                val selectedDateDecorator = SelectedDateDecorator(requireContext(), date)
                binding.calendarView.addDecorator(selectedDateDecorator)

                val month = date.month
                val day = date.day
                val title = String.format("%d월 %d일 식단", month, day)

                requireActivity().runOnUiThread {
                    val mainActivity = activity as? MainActivity
                    mainActivity?.showTitle(title, true)
                    mainActivity?.hideBottomBar()

                    val dailyDietFragment = DailyDietFragment().apply {
                        arguments = Bundle().apply {
                            putInt("month", month) // month 값을 전달
                            putInt("day", day)     // day 값을 전달
                        }
                    }

                    val transaction: FragmentTransaction = parentFragmentManager.beginTransaction()
                    transaction.replace(R.id.main_container, dailyDietFragment)
                    transaction.addToBackStack(null)
                    transaction.commit()

                    parentFragmentManager.executePendingTransactions()

                    // 프래그먼트 전환 후 선택한 날짜 데코레이터 제거
                    parentFragmentManager.addOnBackStackChangedListener {
                        binding.calendarView.removeDecorator(selectedDateDecorator)
                    }
                }
            }
        }
    }

    // 선택된 월 데코레이터
    private class SelectedMonthDecorator(
        private val context: Context,
        private val selectedMonth: Int
    ) : DayViewDecorator {

        override fun shouldDecorate(day: CalendarDay): Boolean {
            return day.month != selectedMonth // 선택된 달의 날짜가 아닌 경우 데코레이터 적용
        }

        override fun decorate(view: DayViewFacade) {
            // 선택되지 않은 달의 날짜에 대해 색깔 적용
            val gray7Color = ContextCompat.getColor(context, R.color.Gray7)
            view.addSpan(ForegroundColorSpan(gray7Color))
        }
    }

    // 선택한 날짜 데코레이터
    private class SelectedDateDecorator(private val context: Context, private val date: CalendarDay) :
        DayViewDecorator {

        override fun shouldDecorate(day: CalendarDay): Boolean {
            return day == date // 선택한 날짜에만 데코레이터 적용
        }

        override fun decorate(view: DayViewFacade) {
            view.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.ic_background)!!)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}