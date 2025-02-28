package com.example.umc.Subscribe

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.PopupMenu
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.umc.R
import com.example.umc.databinding.ViewSearchEditTextBinding

class SearchEditTextComponent @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val binding: ViewSearchEditTextBinding
    private var searchAction: ((String) -> Unit)? = null

    init {
        binding = ViewSearchEditTextBinding.inflate(LayoutInflater.from(context), this, true)
        setupListeners()
        setupEditText()
    }

    private fun setupEditText() {
        binding.etSearchInput.apply {
            // 한 줄 입력으로 설정
            maxLines = 1
            // 엔터 키를 검색 액션으로 설정
            imeOptions = EditorInfo.IME_ACTION_SEARCH

            // 키 이벤트 리스너 설정
            setOnEditorActionListener { _, actionId, event ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    (event?.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN)) {
                    // 검색 실행
                    searchAction?.invoke(text.toString())
                    // 키보드 숨기기
                    hideKeyboard()
                    true
                } else {
                    false
                }
            }
        }
    }

    private fun setupListeners() {
        // 검색 버튼 클릭 리스너
        binding.ivSearchButton.setOnClickListener {
            searchAction?.invoke(binding.etSearchInput.text.toString())
            hideKeyboard()
        }

        // EditText 포커스 변화 리스너
        binding.etSearchInput.setOnFocusChangeListener { _, hasFocus ->
            binding.clSearchField.isActivated = hasFocus
        }

        // 부모 레이아웃 클릭시 EditText 포커스
        binding.clSearchField.setOnClickListener {
            binding.etSearchInput.requestFocus()
            binding.etSearchInput.showKeyboard()
        }
    }

    fun setOnSearchClickListener(action: (String) -> Unit) {
        this.searchAction = action
    }

    private fun View.showKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
    }

    private fun hideKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.etSearchInput.windowToken, 0)
    }

    fun setText(text: String) {
        binding.etSearchInput.setText(text)
    }

    fun getText(): String = binding.etSearchInput.text.toString()

    fun clearText() {
        binding.etSearchInput.text?.clear()
    }
}