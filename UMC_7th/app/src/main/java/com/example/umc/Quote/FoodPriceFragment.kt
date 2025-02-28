package com.example.umc.Quote.Sub

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.umc.Quote.FoodPriceViewModel
import com.example.umc.R
import com.example.umc.databinding.FragmentFoodPriceBinding
import com.example.umc.UserApi.RetrofitClient
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import kotlinx.coroutines.launch
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

class FoodPriceFragment : Fragment() {

    private var _binding: FragmentFoodPriceBinding? = null
    private val binding get() = _binding!!
    private val viewModel: FoodPriceViewModel by viewModels()

    private var foodName: String? = null
    private var foodPrice: String? = null
    private var foodUnit: String? = null
    private var priceRate: String? = null
    private var pricePercent: String? = null

    companion object {
        private const val ARG_FOOD_NAME = "food_name"
        private const val ARG_FOOD_PRICE = "food_price"
        private const val ARG_FOOD_UNIT = "food_unit"
        private const val ARG_PRICE_RATE = "price_rate"
        private const val ARG_PRICE_PERCENT = "price_percent"

        fun newInstance(
            foodName: String,
            foodPrice: String,
            foodUnit: String,
            priceRate: String,
            pricePercent: String
        ) = FoodPriceFragment().apply {
            arguments = Bundle().apply {
                putString(ARG_FOOD_NAME, foodName)
                putString(ARG_FOOD_PRICE, foodPrice)
                putString(ARG_FOOD_UNIT, foodUnit)
                putString(ARG_PRICE_RATE, priceRate)
                putString(ARG_PRICE_PERCENT, pricePercent)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            foodName = it.getString(ARG_FOOD_NAME)?.split("/")?.get(0)
            foodPrice = it.getString(ARG_FOOD_PRICE)
            foodUnit = it.getString(ARG_FOOD_UNIT)
            priceRate = it.getString(ARG_PRICE_RATE)
            pricePercent = it.getString(ARG_PRICE_PERCENT)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFoodPriceBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val itemId = arguments?.getString("item_id")

        itemId?.let {
            viewModel.setSelectedItemId(it)
            viewModel.fetchRecentlyPriceTrendList()
        } ?: run {
            Log.e("FoodPriceFragment", "itemId is null")
        }

        binding.tvFoodName.text = foodName
        binding.tvFoodPrice.text = foodPrice
        binding.tvFoodUnit.text = "($foodUnit)"
        binding.tvPriceRate.text = pricePercent?.toFloatOrNull().toString()
        binding.tvPricePercent.text = (pricePercent?.toFloatOrNull() ?: 0f * 100).toString()
        binding.tvPercent.text = (pricePercent?.toFloatOrNull() ?: 0f * 100).toString() + "%"
        binding.tvGraphUnit.text = foodUnit
        binding.tvGraphName.text = foodName
        // Buy link button click
        binding.ibtBuy.setOnClickListener {
            foodName?.let { name ->
                val encodedFoodName = URLEncoder.encode(name, StandardCharsets.UTF_8.toString())
                val url =
                    "https://www.coupang.com/np/search?component=&q=$encodedFoodName&channel=user"

                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                startActivity(intent)
            } ?: Toast.makeText(requireContext(), "상품명이 없습니다.", Toast.LENGTH_SHORT).show()
        }

        // Load image
        if (!foodName.isNullOrEmpty()) {
            loadMaterialImage(foodName!!)
        }

        viewModel.priceData.observe(viewLifecycleOwner, Observer { priceResponse ->
            priceResponse?.let {
                val prices = it.price.mapNotNull { priceItem ->
                    if (priceItem.yyyy == "평년") {
                        Log.e("DateParsing", "Skipping invalid year: ${priceItem.yyyy}")
                        return@mapNotNull null // Skip "평년" data
                    }

                    val formattedDate =
                        if (priceItem.yyyy.length == 4) "${priceItem.yyyy}-01-01" else priceItem.yyyy
                    Log.d("DateParsing", "Processing date: $formattedDate")

                    val localDate = try {
                        LocalDate.parse(formattedDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                    } catch (e: DateTimeParseException) {
                        return@mapNotNull null
                    }

                    val d40Value = priceItem.d40.toString().toFloatOrNull() ?: 0f
                    val d30Value = priceItem.d30.toString().toFloatOrNull() ?: 0f
                    val d20Value = priceItem.d20.toString().toFloatOrNull() ?: 0f
                    val d10Value = priceItem.d10.toString().toFloatOrNull() ?: 0f
                    val d0Value = priceItem.d0.toString().toFloatOrNull() ?: 0f

                    if (d40Value != 0f || d30Value != 0f || d20Value != 0f || d10Value != 0f || d0Value != 0f) {
                        return@mapNotNull Pair(localDate, d40Value)
                    } else {
                        return@mapNotNull null
                    }
                }

                if (prices.isNotEmpty()) {
                    setChart(prices)
                } else {
                    Log.e("FoodPriceFragment", "No valid price data available")
                }
            }
        })
    }

    private fun loadMaterialImage(foodName: String) {
        binding.imgPriceFood.setImageDrawable(null)

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.imageApiService.getMaterialImage(foodName)

                if (response.isSuccessful) {
                    val imageUrl = response.body()?.success?.imageUrl
                    if (!imageUrl.isNullOrEmpty()) {
                        Glide.with(requireContext())
                            .load(imageUrl)
                            .into(binding.imgPriceFood)
                        Log.d("FoodImage", "이미지 로드 성공: $imageUrl")
                    } else {
                        Log.e("FoodImage", "이미지 URL이 비어 있음")
                        Toast.makeText(context, "이미지 URL이 비어 있습니다.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Log.e("FoodImage", "API 호출 실패: ${response.message()}")
                    Toast.makeText(context, "API 호출 실패: ${response.message()}", Toast.LENGTH_SHORT)
                        .show()
                }
            } catch (e: Exception) {
                Log.e("FoodImage", "네트워크 오류: ${e.message}")
                Toast.makeText(context, "네트워크 오류: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setChart(prices: List<Pair<LocalDate, Float>>) {
        val lineChart: LineChart = binding.lineChart
        lineChart.visibility = View.VISIBLE // Make sure the chart is visible
        lineChart.invalidate()
        lineChart.clear()

        val entries = listOf(
            Entry(0f, prices.getOrNull(0)?.second ?: 0f),
            Entry(1f, prices.getOrNull(1)?.second ?: 0f),
            Entry(2f, prices.getOrNull(prices.size - 2)?.second ?: 0f),
            Entry(3f, prices.getOrNull(prices.size - 1 )?.second ?: 0f),
            Entry(4f, prices.getOrNull(4 )?.second ?: 0f)
        )

        val lineDataSet = LineDataSet(entries, "가격 변동").apply {
            color = ContextCompat.getColor(requireContext(), R.color.Blue)
            setCircleColor(ContextCompat.getColor(requireContext(), R.color.Blue))
            circleHoleColor = ContextCompat.getColor(requireContext(), R.color.white)
            mode = LineDataSet.Mode.HORIZONTAL_BEZIER
            lineWidth = 3f
            circleRadius = 6f
            circleHoleRadius = 3f
        }

        val lineData = LineData(lineDataSet).apply {
            setValueTextColor(ContextCompat.getColor(requireContext(), R.color.black))
            setValueTextSize(9f)
        }

        // XAxis에 고정된 날짜 넣기
        val xAxis = lineChart.xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM
            valueFormatter = object : ValueFormatter() {
                // 고정된 날짜 리스트
                private val dates = listOf("01-12", "01-22", "02-01", "02-11", "02-21")

                override fun getFormattedValue(value: Float): String {
                    return dates.getOrNull(value.toInt()) ?: ""
                }
            }
            setLabelCount(5, true) // 정확히 5개의 레이블 표시
            textColor = ContextCompat.getColor(requireContext(), R.color.black)
            gridColor = ContextCompat.getColor(requireContext(), R.color.black)
            labelRotationAngle = -45f
            setDrawGridLines(false)
        }

        // YAxis는 가격에 맞춰서 기본 설정
        lineChart.axisLeft.setLabelCount(4, true)
        lineChart.axisRight.apply {
            setDrawLabels(false)
            setDrawAxisLine(false)
            setDrawGridLines(false)
        }

        lineChart.description = null
        lineChart.legend.isEnabled = false
        lineChart.data = lineData
    }
}