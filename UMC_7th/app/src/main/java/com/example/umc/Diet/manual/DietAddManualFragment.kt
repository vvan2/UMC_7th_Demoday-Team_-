package com.example.umc.Diet.manual

import com.example.umc.Diet.manual.DietAddManualViewModel
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.viewModels
import com.example.umc.Diet.manual.DietAddConfirmFragment
import com.example.umc.Main.MainActivity
import com.example.umc.R
import com.example.umc.UserApi.UserRepository
import com.example.umc.model.request.PostManualMealsRequest
import java.util.*

class DietAddManualFragment : Fragment(R.layout.fragment_diet_add_manual) {

    private lateinit var dateSpinner: Spinner
    private lateinit var etAddFood: LinearLayout
    private lateinit var etAddCalorie: EditText
    private lateinit var btSave: Button
    private val foodList = mutableListOf<String>()
    private val viewModel: DietAddManualViewModel by viewModels()
    private var selectedTime: String? = null
    private lateinit var monthButton: Button
    private var selectedDate: String? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // UI 요소 초기화
        dateSpinner = view.findViewById(R.id.spinner_date)
        etAddFood = view.findViewById(R.id.et_add_food)
        etAddCalorie = view.findViewById(R.id.et_add_calorie)
        monthButton = view.findViewById(R.id.bt_month)
        btSave = view.findViewById(R.id.bt_save)

        // 날짜 선택 스피너 설정
        val calendar = Calendar.getInstance()
        val currentYear = calendar.get(Calendar.YEAR)
        val currentMonth = calendar.get(Calendar.MONTH) + 1
        monthButton.text = "${currentMonth}월"

        val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        val dateList = (1..daysInMonth).map { "${it}일" }
        val dateAdapter = ArrayAdapter(requireContext(), R.layout.item_spinner, dateList)
        dateAdapter.setDropDownViewResource(R.layout.item_spinner_dropdown)
        dateSpinner.adapter = dateAdapter

        dateSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, view: View?, position: Int, id: Long
            ) {
                val selectedDay = dateList[position].replace("일", "").toInt()
                val dateStr =
                    "$currentYear-${"%02d".format(currentMonth)}-${"%02d".format(selectedDay)}T00:00:00.000Z"
                selectedDate = dateStr
                dateSpinner.setBackgroundResource(R.drawable.bg_time_selected)
                Log.d("MealLogging", "선택한 날짜: $selectedDate")
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // 시간 선택 버튼 설정
        val timeButtons = listOf(
            view.findViewById<Button>(R.id.bt_morning),
            view.findViewById<Button>(R.id.bt_lunch),
            view.findViewById<Button>(R.id.bt_dinner),
            view.findViewById<Button>(R.id.bt_snack)
        )

        timeButtons.forEach { button ->
            button.setOnClickListener {
                selectedTime = button.text.toString()
                Log.d("MealLogging", "선택한 시간대: $selectedTime")

                timeButtons.forEach { btn ->
                    btn.setBackgroundColor(
                        if (btn == button) resources.getColor(R.color.Primary_Orange1, null)
                        else resources.getColor(R.color.Gray7, null)
                    )
                }
            }
        }

        // 음식 추가 버튼 설정
        view.findViewById<Button>(R.id.bt_add_food_text).setOnClickListener {
            addNewEditText()
        }

        // btSave 클릭 시 데이터 저장
        btSave.setOnClickListener {
            foodList.clear()
            for (i in 0 until etAddFood.childCount) {
                val editText = etAddFood.getChildAt(i) as EditText
                val food = editText.text.toString().trim() // 공백 제거
                if (food.isNotEmpty()) foodList.add(food)
            }

            val calorie = etAddCalorie.text.toString().trim().toIntOrNull() ?: 0
            Log.d("MealLogging", "입력된 칼로리 값: ${etAddCalorie.text}, 변환된 값: $calorie") // 디버깅 로그

            if (selectedTime != null && selectedDate != null) {
                // Manual meal request 전송
                sendManualMealsRequest(selectedDate!!, selectedTime!!, foodList, calorie)

                // UI 초기화
                btSave.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.Primary_Orange1
                    )
                )
                etAddCalorie.text.clear()
                for (i in 0 until etAddFood.childCount) {
                    val editText = etAddFood.getChildAt(i) as EditText
                    editText.text.clear()
                }
                dateSpinner.setSelection(0)
                timeButtons.forEach { btn ->
                    btn.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.Gray7))
                }

                Handler().postDelayed({
                    btSave.setBackgroundColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.Gray7
                        )
                    )
                }, 100)
                val confirmFragment = DietAddConfirmFragment().apply {
                    arguments = Bundle().apply {
                        putString("foods", foodList.joinToString(", "))
                        putString("date", selectedDate)
                        putString("time", selectedTime)
                        putInt("calories", calorie)
                    }
                }
            } else {
                Log.e("MealLogging", "날짜와 시간대를 선택해주세요.")
            }
        }

        val dietAddConfirmButton: Button = view.findViewById(R.id.bt_add_confirm)
        dietAddConfirmButton.setOnClickListener {
            val bundle = Bundle().apply {
                putSerializable("mealList", ArrayList(foodList))
                putString("date", selectedDate)
                putString("time", selectedTime)
                putInt(
                    "calories",
                    etAddCalorie.text.toString().trim().toIntOrNull() ?: 0
                )
            }

            val confirmFragment = DietAddConfirmFragment().apply {
                arguments = bundle
            }

            val transaction: FragmentTransaction = parentFragmentManager.beginTransaction()
            transaction.replace(R.id.main_container, confirmFragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }
    }

        private fun addNewEditText() {
        val inflater = LayoutInflater.from(requireContext())
        val newEditText = inflater.inflate(R.layout.item_edit_text, etAddFood, false) as EditText
        etAddFood.addView(newEditText)
    }

    private fun sendManualMealsRequest(date: String, time: String, foods: List<String>, calorie: Int) {
        val context = requireContext()
        val accessToken = UserRepository.getAuthToken(context)
        if (accessToken == null) {
            Log.e("DietAddManualFragment", "토큰이 존재하지 않습니다.")
            return
        }

        val request = PostManualMealsRequest(
            addedByUser = true,
            calorieTotal = calorie,
            foods = foods,
            mealDate = date,
            time = time
        )

        Log.d("DietAddManualFragment", "Request: $request")

        viewModel.postManualMeals(context, request, accessToken,
            onSuccess = { Log.d("MealLogging", "식단 추가 성공") },
            onError = { errorMsg: String -> Log.e("MealLogging", "식단 추가 실패: $errorMsg") }
        )
    }

    override fun onResume() {
        super.onResume()
        val mainActivity = activity as? MainActivity
        mainActivity?.showTitle("식단 수동 등록", true)
        mainActivity?.hideBottomBar()
    }
}
