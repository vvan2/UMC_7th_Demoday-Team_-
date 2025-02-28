// DeliveryDateDecorator.kt
package com.example.umc.Subscribe

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import com.example.umc.R
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade

class DeliveryDateDecorator(context: Context) : DayViewDecorator {
    private val drawable: Drawable
    private val dates = HashSet<CalendarDay>()

    init {
        drawable = ContextCompat.getDrawable(context, R.drawable.bg_delivery_date)!!
    }

    fun addDate(day: CalendarDay) {
        dates.add(day)
    }

    override fun shouldDecorate(day: CalendarDay): Boolean {
        return dates.contains(day)
    }

    override fun decorate(view: DayViewFacade) {
        view.setSelectionDrawable(drawable)
    }
}