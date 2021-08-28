package kr.hs.emirim.w2015.stac_prr.Decorator

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import com.google.android.material.resources.TextAppearance
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import kr.hs.emirim.w2015.stac_prr.R
import java.util.*


class OtherDaysDecorator(val context: Context, val calendarView: MaterialCalendarView) : DayViewDecorator {

    override fun shouldDecorate(day: CalendarDay): Boolean {
        val cal1: Calendar = day.calendar
        val cal2: Calendar = calendarView.currentDate.calendar

        return cal1.get(Calendar.ERA) == cal2.get(Calendar.ERA) &&
                cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.MONTH) != cal2.get(Calendar.MONTH)
    }

    @SuppressLint("RestrictedApi")
    override fun decorate(view: DayViewFacade) {
        Log.d("TAG", "decorate: 다른날 실행됨")
        view.addSpan(TextAppearance(context,R.style.OtherDay))
    }

}