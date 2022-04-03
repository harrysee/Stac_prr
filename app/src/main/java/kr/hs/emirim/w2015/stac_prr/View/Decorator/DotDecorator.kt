package kr.hs.emirim.w2015.stac_prr.View.Decorator

import android.graphics.Color
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import com.prolificinteractive.materialcalendarview.spans.DotSpan



class DotDecorator(private val color: String, dates: Collection<CalendarDay>?) : DayViewDecorator {
    private val dates: HashSet<CalendarDay> = HashSet(dates)
    //private val drawble : Drawable? = ContextCompat.getDrawable(context,R.drawable.calendar_addspan1)

    override fun shouldDecorate(day: CalendarDay): Boolean {
        return dates.contains(day)
    }

    override fun decorate(view: DayViewFacade) {
        view.addSpan(DotSpan(6F, Color.parseColor(color)))
        //view.addSpan(drawble!!)
    }

}