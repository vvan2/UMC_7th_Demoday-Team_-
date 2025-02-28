package com.example.umc.Diet

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.umc.Diet.meal.MealViewModelFactory
import com.example.umc.Main.MainActivity
import com.example.umc.R
import com.example.umc.UserApi.RetrofitClient
import com.example.umc.databinding.FragmentDailyDietBinding
import com.example.umc.meal.MealViewModel
import com.example.umc.model.repository.MealRepository
import com.example.umc.model.response.PostDailyMealSuccess
import java.text.SimpleDateFormat
import java.util.*

class DailyDietFragment : Fragment() {
    private var _binding: FragmentDailyDietBinding? = null
    private val binding get() = _binding!!

    private lateinit var breakfastAdapter: MenuItemAdapter
    private lateinit var lunchAdapter: MenuItemAdapter
    private lateinit var dinnerAdapter: MenuItemAdapter

    private val mealApiService by lazy { RetrofitClient.mealApiService }
    private val mealRepository by lazy {
        MealRepository(
            mealApiService,
            requireContext()
        )
    }

    private val viewModel: MealViewModel by viewModels {
        MealViewModelFactory(
            requireContext(),
            mealRepository
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d("UI_DEBUG", "DailyDietFragment - onCreateView 시작")
        _binding = FragmentDailyDietBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("UI_DEBUG", "DailyDietFragment - onViewCreated 시작")

        val month = arguments?.getInt("month") ?: 1
        val day = arguments?.getInt("day") ?: 1

        // mealDate 생성
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.MONTH, month - 1)  // Calendar는 0부터 시작하므로 1을 빼야 함
        calendar.set(Calendar.DAY_OF_MONTH, day)

        // 날짜 포맷 설정
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        sdf.timeZone = TimeZone.getTimeZone("UTC")  // UTC 시간대 설정

        // mealDate 생성
        val mealDate = sdf.format(calendar.time)

        // UI에 날짜 표시
        binding.tvDailyDietToday.text = getString(R.string.daily_diet_day, month, day)

        initializeAdapters()
        setupRecyclerViews()
        observeViewModel()

        // mealDate를 viewModel의 fetchDailyMeal에 전달
        Log.d("UI_DEBUG", "fetchDailyMeal 호출 with mealDate: $mealDate")
        viewModel.fetchDailyMeal(mealDate)
    }

    private fun initializeAdapters() {
        Log.d("UI_DEBUG", "어댑터 초기화 시작")

        breakfastAdapter = MenuItemAdapter(
            onClick = { item: MenuItem, position: Int ->
                Log.d("UI_DEBUG", "아침 메뉴 클릭 - item: ${item.name}, position: $position")
                onMenuItemClicked(item, "아침")
            },
            onFavoriteChanged = { item, isFavorite ->
                Log.d("UI_DEBUG", "아침 메뉴 즐겨찾기 변경 - item: ${item.name}, favorite: $isFavorite")
            },
            onDietCompleteChanged = { item, isCompleted ->
                Log.d("UI_DEBUG", "아침 메뉴 완료 상태 변경 - item: ${item.name}, completed: $isCompleted")
            },
            onRefresh = { item ->
                Log.d("UI_DEBUG", "아침 메뉴 새로고침 - item: ${item.name}, mealId: ${item.mealId}")
                viewModel.refreshMeal(item.mealId)
            }
        )

        lunchAdapter = MenuItemAdapter(
            onClick = { item: MenuItem, position: Int ->
                Log.d("UI_DEBUG", "점심 메뉴 클릭 - item: ${item.name}, position: $position")
                onMenuItemClicked(item, "점심")
            },
            onFavoriteChanged = { item, isFavorite ->
                Log.d("UI_DEBUG", "점심 메뉴 즐겨찾기 변경 - item: ${item.name}, favorite: $isFavorite")
            },
            onDietCompleteChanged = { item, isCompleted ->
                Log.d("UI_DEBUG", "점심 메뉴 완료 상태 변경 - item: ${item.name}, completed: $isCompleted")
            },
            onRefresh = { item ->
                Log.d("UI_DEBUG", "점심 메뉴 새로고침 - item: ${item.name}, mealId: ${item.mealId}")
                viewModel.refreshMeal(item.mealId)
            }
        )

        dinnerAdapter = MenuItemAdapter(
            onClick = { item: MenuItem, position: Int ->
                Log.d("UI_DEBUG", "저녁 메뉴 클릭 - item: ${item.name}, position: $position")
                onMenuItemClicked(item, "저녁")
            },
            onFavoriteChanged = { item, isFavorite ->
                Log.d("UI_DEBUG", "저녁 메뉴 즐겨찾기 변경 - item: ${item.name}, favorite: $isFavorite")
            },
            onDietCompleteChanged = { item, isCompleted ->
                Log.d("UI_DEBUG", "저녁 메뉴 완료 상태 변경 - item: ${item.name}, completed: $isCompleted")
            },
            onRefresh = { item ->
                Log.d("UI_DEBUG", "저녁 메뉴 새로고침 - item: ${item.name}, mealId: ${item.mealId}")
                viewModel.refreshMeal(item.mealId)
            }
        )
        Log.d("UI_DEBUG", "어댑터 초기화 완료")
    }

    private fun setupRecyclerViews() {
        Log.d("UI_DEBUG", "RecyclerView 설정 시작")
        binding.apply {
            rvBreakfast.apply {
                Log.d("UI_DEBUG", "아침 RecyclerView 설정")
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                adapter = breakfastAdapter
                addOnScrollListener(object : RecyclerView.OnScrollListener() {
                    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                        updateIndicator(recyclerView, binding.breakfastIndicatorBar)
                    }
                })
            }

            rvLunch.apply {
                Log.d("UI_DEBUG", "점심 RecyclerView 설정")
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                adapter = lunchAdapter
                addOnScrollListener(object : RecyclerView.OnScrollListener() {
                    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                        updateIndicator(recyclerView, binding.lunchIndicatorBar)
                    }
                })
            }

            rvDinner.apply {
                Log.d("UI_DEBUG", "저녁 RecyclerView 설정")
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                adapter = dinnerAdapter
                addOnScrollListener(object : RecyclerView.OnScrollListener() {
                    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                        updateIndicator(recyclerView, binding.dinnerIndicatorBar)
                    }
                })
            }
        }
        Log.d("UI_DEBUG", "RecyclerView 설정 완료")
    }

    private fun observeViewModel() {
        viewModel.dailyMeals.observe(viewLifecycleOwner) { mealWrapper ->
            Log.d("UI_DEBUG", "데이터 수신: $mealWrapper")

            // mealDate 활용 (필요한 경우)
            mealWrapper.mealDate?.let { date ->
                Log.d("UI_DEBUG", "식단 날짜: $date")
            }

            val meals = mealWrapper.existingMeals ?: emptyList()
            if (meals.isEmpty()) {
                Log.d("UI_DEBUG", "식사 데이터가 없습니다")
                return@observe
            }

            val mealsCount = meals.size
            Log.d("UI_DEBUG", "전체 식사 수: $mealsCount")

            // 전체 식사 수가 3의 배수가 아닌 경우 처리
            if (mealsCount % 3 != 0) {
                Log.e("UI_DEBUG", "식사 데이터 개수가 3의 배수가 아닙니다: $mealsCount")
            }

            val mealSize = mealsCount / 3
            try {
                val breakfastMeals = meals.subList(0, mealSize)
                val lunchMeals = meals.subList(mealSize, mealSize * 2)
                val dinnerMeals = meals.subList(mealSize * 2, mealsCount)

                Log.d("UI_DEBUG", """
                |식사별 개수:
                |아침: ${breakfastMeals.size}
                |점심: ${lunchMeals.size}
                |저녁: ${dinnerMeals.size}
            """.trimMargin())

                breakfastAdapter.submitList(breakfastMeals.map {
                    Log.d("UI_DEBUG", "아침 메뉴 변환: ${it.food}")
                    it.toMenuItem()
                })
                lunchAdapter.submitList(lunchMeals.map {
                    Log.d("UI_DEBUG", "점심 메뉴 변환: ${it.food}")
                    it.toMenuItem()
                })
                dinnerAdapter.submitList(dinnerMeals.map {
                    Log.d("UI_DEBUG", "저녁 메뉴 변환: ${it.food}")
                    it.toMenuItem()
                })
            } catch (e: Exception) {
                Log.e("UI_DEBUG", "식사 데이터 처리 중 오류 발생", e)
                Toast.makeText(requireContext(), "식단 데이터 처리 중 오류가 발생했습니다", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateIndicator(recyclerView: RecyclerView, indicator: View) {
        val totalWidth = recyclerView.computeHorizontalScrollRange()
        val visibleWidth = recyclerView.computeHorizontalScrollExtent()
        val scrollOffset = recyclerView.computeHorizontalScrollOffset()

        val scrollProgress = if (totalWidth - visibleWidth > 0) {
            scrollOffset.toFloat() / (totalWidth - visibleWidth)
        } else {
            0f
        }

        val maxScroll = (indicator.parent as View).width - indicator.width
        indicator.translationX = maxScroll * scrollProgress
    }

    private fun onMenuItemClicked(item: MenuItem, mealTime: String) {
        val dietDetailFragment = DietDetailFragment()
        val bundle = Bundle().apply {
            putString("name", item.name)
            putString("calories", item.calories)
            putString("material", item.material)
            putString("recipe", item.recipe)
            putString("calorieDetail", item.calorieDetail)
            putInt("difficulty", item.difficulty ?: 0) // null일 경우 기본값 0으로 설정
            putInt("mealId", item.mealId)
            putInt("price", item.price ?: 0)           // 추가
            putBoolean("addedByUser", item.addedByUser) // 추가
        }
        dietDetailFragment.arguments = bundle

        val transaction: FragmentTransaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.main_container, dietDetailFragment)
        transaction.addToBackStack(null)
        transaction.commit()

        val mainActivity = activity as? MainActivity
        mainActivity?.showTitle(mealTime, true)
        mainActivity?.hideBottomBar()
    }

    private fun PostDailyMealSuccess.toMenuItem(): MenuItem {
        return MenuItem(
            imageUrl = "",
            name = this.food ?: "Unknown Meal",
            calories = "${this.calorieTotal}Kcal", // 칼로리 단위 추가
            mealId = this.mealId,
            material = this.material,
            recipe = this.recipe,
            calorieDetail = this.calorieDetail,
            difficulty = this.difficulty,
            price = this.price,              // price 추가
            addedByUser = this.addedByUser,  // addedByUser 추가
            isFavorite = false,
            isDietCompleted = false
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        val month = arguments?.getInt("month") ?: 1
        val day = arguments?.getInt("day") ?: 1
        val mainActivity = activity as? MainActivity
        mainActivity?.showTitle(getString(R.string.daily_diet_day, month, day), true)
    }
}
