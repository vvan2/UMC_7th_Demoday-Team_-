package com.example.umc.Diet

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import androidx.recyclerview.widget.RecyclerView

class ScrollIndicatorDecoration : RecyclerView.ItemDecoration() {
    private val paint = Paint().apply {
        color = Color.parseColor("#FF7300") // 오렌지색
        style = Paint.Style.FILL
    }

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(c, parent, state)

        val itemCount = parent.adapter?.itemCount ?: return
        if (itemCount == 0) return

        // 전체 스크롤 가능한 너비
        val totalWidth = parent.computeHorizontalScrollRange()
        // 현재 스크롤된 위치
        val scrollOffset = parent.computeHorizontalScrollOffset()
        // 보이는 너비
        val visibleWidth = parent.computeHorizontalScrollExtent()

        // 인디케이터 바의 전체 너비
        val indicatorWidth = parent.width

        // 인디케이터 바의 실제 너비 계산
        val ratio = visibleWidth.toFloat() / totalWidth
        val barWidth = (indicatorWidth * ratio).toInt()

        // 인디케이터 바의 위치 계산
        val maxScrollOffset = totalWidth - visibleWidth
        val scrollProgress = if (maxScrollOffset > 0) {
            scrollOffset.toFloat() / maxScrollOffset
        } else 0f
        val barOffset = ((indicatorWidth - barWidth) * scrollProgress).toInt()

        // 인디케이터 바 그리기
        c.drawRect(
            barOffset.toFloat(),
            (parent.height - 4).toFloat(),  // 바의 높이는 4dp
            (barOffset + barWidth).toFloat(),
            parent.height.toFloat(),
            paint
        )
    }
}