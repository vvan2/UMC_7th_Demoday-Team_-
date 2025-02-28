package com.example.umc.Main

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.umc.Diet.DietDetailFragment
import com.example.umc.Diet.HomeContainerFragment
import com.example.umc.Mypage.MyFragment
import com.example.umc.Quote.PriceFragment
import com.example.umc.R
import com.example.umc.Subscribe.SubFragment
import com.example.umc.UserApi.RetrofitClient
import com.example.umc.databinding.ActivityMainBinding
import android.Manifest


class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        enableEdgeToEdge()
        setBottomNavigationView()

        requestStoragePermission()

        // 앱 초기 실행 시 홈화면으로 설정
        if (savedInstanceState == null) {
            binding.bottomNavigationView.selectedItemId = R.id.fragment_home
        }
    }
    private fun requestStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 100)
            }
        }
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            } else {

            }
        }
    }

    private fun setBottomNavigationView() {
        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.fragment_home -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_container, HomeContainerFragment())
                        .commit()
                    true
                }
                R.id.fragment_price -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_container, PriceFragment())
                        .commit()
                    true
                }
                R.id.fragment_sub -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_container, SubFragment())
                        .commit()
                    true
                }
                R.id.fragment_my -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_container, MyFragment())
                        .commit()
                    true
                }
                else -> false
            }
        }
    }

    private fun handleBackPressed() {
        val currentFragment = supportFragmentManager.findFragmentById(R.id.main_container)

        if (currentFragment is DietDetailFragment) {
            hideBottomBar()
            supportFragmentManager.popBackStack()
        } else if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
        } else {
            finish()
        }
        hideTitle()
        showBottomBar()
    }

    // Title을 설정
    fun showTitle(title: String, isBackBtn: Boolean) {
        binding.flTitle.visibility = View.VISIBLE
        binding.tvTitle.text = title
        if (isBackBtn) {
            binding.ibtnBack.visibility = View.VISIBLE
            binding.ibtnBack.setOnClickListener {
                handleBackPressed()
            }
        } else {
            binding.ibtnBack.visibility = View.VISIBLE
        }
    }

    fun hideTitle() {
        binding.flTitle.visibility = View.GONE
    }

    fun hideBottomBar() {
        binding.bottomNavigationView.visibility = View.GONE
    }

    fun showBottomBar() {
        binding.bottomNavigationView.visibility = View.VISIBLE
    }
}