//package com.example.umc.Subscribe
//
//import android.os.Bundle
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.TextView
//import androidx.fragment.app.Fragment
//import com.example.umc.Main.MainActivity
//import com.example.umc.R
//import com.prolificinteractive.materialcalendarview.CalendarDay
//import com.prolificinteractive.materialcalendarview.MaterialCalendarView
//import android.view.MotionEvent
//import android.widget.ImageView
//import androidx.core.content.ContextCompat
//import com.example.umc.Subscribe.Repository.AddressRepository
//import com.example.umc.Subscribe.Repository.DeliveryAddressRepository
//import com.example.umc.Subscribe.SubscribeResponse.Get.DeliveryGetResponse
//import com.example.umc.Subscribe.SubscriptionHistoryFragment
//
//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.launch
//import kotlinx.coroutines.withContext
//
//class SubscriptionManageFragment : Fragment() {
//
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        return inflater.inflate(R.layout.fragment_subscription_manage, container, false)
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        (activity as? MainActivity)?.showTitle("구독 관리", true)
//        (activity as? MainActivity)?.hideBottomBar()
//
//        setupDetailButton(view)
//        setupCalendar(view)
//        setupManageAddressButton(view)  // 주소 관리 버튼 설정
//
//        loadDefaultAddress(view)
//    }
//
//    private fun setupDetailButton(view: View) {
//        // 상세내역 버튼 클릭 리스너
//        view.findViewById<TextView>(R.id.detailButton)?.setOnClickListener {
//            navigateToHistory()
//        }
//    }
//
//    private fun setupManageAddressButton(view: View) {
//        // 주소 관리 레이아웃 클릭 리스너
//        view.findViewById<View>(R.id.ll_manage_address)?.setOnClickListener {
//            (activity as? MainActivity)?.showTitle("주소록 관리", true)
//            navigateToAddress()
//        }
//    }
//    private fun loadDefaultAddress(view: View) {
//        val context = requireContext()
//
//        // 🚀 먼저 SharedPreferences에서 기본 배송지 정보 가져오기
//        val savedAddress = AddressRepository.getDefaultAddress(context)
//
//        if (savedAddress != null) {
//            // ✅ 저장된 정보가 있다면 UI에 반영
//            updateUIWithAddress(view, savedAddress)
//        } else {
//            // ✅ 저장된 정보가 없으면 서버에서 가져오기
//            CoroutineScope(Dispatchers.IO).launch {
//                val result = DeliveryAddressRepository.Get.getDefaultDeliveryAddress(context)
//
//                withContext(Dispatchers.Main) {
//                    if (result.isSuccess) {
//                        val address = result.getOrNull()
//                        if (address != null) {
//                            AddressRepository.saveDefaultAddress(context, address) // ✅ SharedPreferences에 저장
//                            updateUIWithAddress(view, address)
//                        }
//                    } else {
//                        view.findViewById<TextView>(R.id.tv_recipient1)?.text = "기본 배송지 정보 없음"
//                    }
//                }
//
//            }
//        }
//    }
//    private fun updateUIWithAddress(view: View, address: DeliveryGetResponse) {
//        view.findViewById<TextView>(R.id.tv_recipient1).text = address.name
//        view.findViewById<TextView>(R.id.tv_address1).text = address.address
//        view.findViewById<TextView>(R.id.tv_phone1).text = address.phoneNum
//        view.findViewById<TextView>(R.id.tv_gatecode).text = address.memo
//    }
//
//
//    private lateinit var calendarView: MaterialCalendarView
//    private var calendarDates: List<String> = listOf() // 날짜 리스트 저장
//
//    private fun setupCalendar(view: View) {
//        calendarView = view.findViewById(R.id.calendar_view)
//
//
//        // ID를 문자열로 찾기
//        val leftArrowId = resources.getIdentifier("mcv_arrow_previous", "id", requireContext().packageName)
//        val rightArrowId = resources.getIdentifier("mcv_arrow_next", "id", requireContext().packageName)
//
//        val leftArrow = view.findViewById<ImageView>(leftArrowId)
//        val rightArrow = view.findViewById<ImageView>(rightArrowId)
//
//        leftArrow?.setOnTouchListener { _, event ->
//            when (event.action) {
//                MotionEvent.ACTION_DOWN -> {
//                    leftArrow.setColorFilter(ContextCompat.getColor(requireContext(), R.color.Primary_Orange1))
//                    true
//                }
//                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
//                    leftArrow.setColorFilter(ContextCompat.getColor(requireContext(), R.color.Gray5))
//                    true
//                }
//                else -> false
//            }
//        }
//
//        rightArrow?.setOnTouchListener { _, event ->
//            when (event.action) {
//                MotionEvent.ACTION_DOWN -> {
//                    rightArrow.setColorFilter(ContextCompat.getColor(requireContext(), R.color.Primary_Orange1))
//                    true
//                }
//                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
//                    rightArrow.setColorFilter(ContextCompat.getColor(requireContext(), R.color.Gray5))
//                    true
//                }
//                else -> false
//            }
//        }
//
//        // Null 체크 후 기본 색상 설정
//        leftArrow?.setColorFilter(ContextCompat.getColor(requireContext(), R.color.Gray5))
//        rightArrow?.setColorFilter(ContextCompat.getColor(requireContext(), R.color.Gray5))
//    }
//    private fun navigateToHistory() {
//        // 구독 내역 프래그먼트로 전환
//        parentFragmentManager.beginTransaction()
//            .replace(R.id.main_container, SubscriptionHistoryFragment())
//            .addToBackStack(null)
//            .commit()
//    }
//
//    private fun navigateToAddress() {
//        parentFragmentManager.beginTransaction()
//            .replace(R.id.main_container, SubAddressFragment())
//            .addToBackStack(null)
//            .commit()
//    }
//
//    override fun onDestroyView() {
//        super.onDestroyView()
//    }
//
//    override fun onResume() {
//        super.onResume()
//        (activity as? MainActivity)?.showBottomBar()
//    }
//}
package com.example.umc.Subscribe

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.umc.Main.MainActivity
import com.example.umc.R
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import android.view.MotionEvent
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.example.umc.Subscribe.Repository.AddressRepository
import com.example.umc.Subscribe.Repository.DeliveryAddressRepository
import com.example.umc.Subscribe.SubscribeResponse.Get.DeliveryGetResponse
import com.example.umc.Subscribe.SubscriptionHistoryFragment
import com.example.umc.UserApi.SharedPreferencesManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SubscriptionManageFragment : Fragment() {

    private lateinit var calendarView: MaterialCalendarView
    private var calendarDates: List<String> = listOf() // 날짜 리스트 저장

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_subscription_manage, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as? MainActivity)?.showTitle("구독 관리", true)
        (activity as? MainActivity)?.hideBottomBar()

        setupDetailButton(view)
        setupCalendar(view)
        setupManageAddressButton(view)  // 주소 관리 버튼 설정

        loadCalendarData(view)  // 캘린더 데이터 로드
        loadDefaultAddress(view)
        updateUserInfo(view)  // ✅ 사용자 정보 업데이트
    }

    private fun setupDetailButton(view: View) {
        view.findViewById<TextView>(R.id.detailButton)?.setOnClickListener {
            navigateToHistory()
        }
    }

    private fun setupManageAddressButton(view: View) {
        view.findViewById<View>(R.id.ll_manage_address)?.setOnClickListener {
            (activity as? MainActivity)?.showTitle("주소록 관리", true)
            navigateToAddress()
        }
    }

    private fun loadDefaultAddress(view: View) {
        val context = requireContext()

        // 🚀 먼저 SharedPreferences에서 기본 배송지 정보 가져오기
        val savedAddress = AddressRepository.getDefaultAddress(context)

        if (savedAddress != null) {
            // ✅ 저장된 정보가 있다면 UI에 반영
            updateUIWithAddress(view, savedAddress)
        } else {
            // ✅ 저장된 정보가 없으면 서버에서 가져오기
            CoroutineScope(Dispatchers.IO).launch {
                val result = DeliveryAddressRepository.Get.getDefaultDeliveryAddress(context)

                withContext(Dispatchers.Main) {
                    if (result.isSuccess) {
                        val address = result.getOrNull()
                        if (address != null) {
                            AddressRepository.saveDefaultAddress(context, address) // ✅ SharedPreferences에 저장
                            updateUIWithAddress(view, address)
                        }
                    } else {
                        view.findViewById<TextView>(R.id.tv_recipient1)?.text = "기본 배송지 정보 없음"
                    }
                }
            }
        }
    }

    private fun updateUIWithAddress(view: View, address: DeliveryGetResponse) {
        view.findViewById<TextView>(R.id.tv_recipient1).text = address.name
        view.findViewById<TextView>(R.id.tv_address1).text = address.address
        view.findViewById<TextView>(R.id.tv_phone1).text = address.phoneNum
        view.findViewById<TextView>(R.id.tv_gatecode).text = address.memo
    }

    private fun setupCalendar(view: View) {
        calendarView = view.findViewById(R.id.calendar_view)
        // 요청 받은 날짜 목록을 이용해서 데코레이터 적용
        val calendarDecorator = CalendarDateDecorator(requireContext(), calendarDates)
        calendarView.addDecorator(calendarDecorator)

        // ID를 문자열로 찾기
        val leftArrowId = resources.getIdentifier("mcv_arrow_previous", "id", requireContext().packageName)
        val rightArrowId = resources.getIdentifier("mcv_arrow_next", "id", requireContext().packageName)

        val leftArrow = view.findViewById<ImageView>(leftArrowId)
        val rightArrow = view.findViewById<ImageView>(rightArrowId)

        leftArrow?.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    leftArrow.setColorFilter(ContextCompat.getColor(requireContext(), R.color.Primary_Orange1))
                    true
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    leftArrow.setColorFilter(ContextCompat.getColor(requireContext(), R.color.Gray5))
                    true
                }
                else -> false
            }
        }

        rightArrow?.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    rightArrow.setColorFilter(ContextCompat.getColor(requireContext(), R.color.Primary_Orange1))
                    true
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    rightArrow.setColorFilter(ContextCompat.getColor(requireContext(), R.color.Gray5))
                    true
                }
                else -> false
            }
        }

        // Null 체크 후 기본 색상 설정
        leftArrow?.setColorFilter(ContextCompat.getColor(requireContext(), R.color.Gray5))
        rightArrow?.setColorFilter(ContextCompat.getColor(requireContext(), R.color.Gray5))
    }

    private fun loadCalendarData(view: View) {
        // 서버로부터 날짜 리스트를 받아오는 부분
        CoroutineScope(Dispatchers.IO).launch {
            val result = DeliveryAddressRepository.Get.getCalendarDates(requireContext()) // 서버 호출

            withContext(Dispatchers.Main) {
                if (result.isSuccess) {
                    val response = result.getOrNull()
                    // 받았던 subDateList를 calendarDates에 저장
                    calendarDates = response?.success?.subDateList ?: listOf()


                    // 데이터 로드 후 캘린더 갱신
                    setupCalendar(view)
                } else {
                    Toast.makeText(requireContext(), "캘린더 데이터를 가져오는 데 실패했습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    private fun updateUserInfo(view: View) {
        val context = requireContext()

        // ✅ SharedPreferences에서 저장된 사용자 정보 가져오기
        val userName = SharedPreferencesManager.getUserName(context) ?: "이름 없음"
        val userPhone = SharedPreferencesManager.getUserPhoneNumber(context) ?: "전화번호 없음"
        val userEmail = SharedPreferencesManager.getUserEmail(context) ?: "이메일 없음"

        // ✅ UI 업데이트
        view.findViewById<TextView>(R.id.tv_name_sub).text = userName
        view.findViewById<TextView>(R.id.tv_phone_sub).text = userPhone
        view.findViewById<TextView>(R.id.tv_email_sub).text = userEmail
    }


    private fun navigateToHistory() {
        parentFragmentManager.beginTransaction()
            .replace(R.id.main_container, SubscriptionHistoryFragment())
            .addToBackStack(null)
            .commit()
    }

    private fun navigateToAddress() {
        parentFragmentManager.beginTransaction()
            .replace(R.id.main_container, SubAddressFragment())
            .addToBackStack(null)
            .commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    override fun onResume() {
        super.onResume()
        (activity as? MainActivity)?.showBottomBar()
    }
}

class CalendarDateDecorator(
    private val context: Context,
    private val dates: List<String>
) : DayViewDecorator {

    override fun shouldDecorate(day: CalendarDay): Boolean {
        // 날짜를 "yyyy-MM-dd" 형식으로 변환
        val dayString = "${day.year}-${String.format("%02d", day.month)}-${String.format("%02d", day.day)}"
        return dates.contains(dayString) // 해당 날짜가 목록에 있으면 데코레이터 추가
    }

    override fun decorate(view: DayViewFacade) {
        // 해당 날짜에 이미지나 스타일링 추가
        view.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.ic_background)!!)
    }
}
