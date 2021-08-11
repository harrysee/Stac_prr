package kr.hs.emirim.w2015.stac_prr.Fragment

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.renderscript.ScriptGroup
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.prolificinteractive.materialcalendarview.*
import kotlinx.android.synthetic.main.fragment_calender.*
import kr.hs.emirim.w2015.stac_prr.R
import java.util.*


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class CalenderFragment : Fragment(),View.OnClickListener {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_calender, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var startTimeCalendar = Calendar.getInstance()
        var endTimeCalendar = Calendar.getInstance()
        materialCalendar.topbarVisible = true

        val currentYear = startTimeCalendar.get(Calendar.YEAR)
        val currentMonth = startTimeCalendar.get(Calendar.MONTH)
        val currentDate = startTimeCalendar.get(Calendar.DATE)

        lateinit var binding: ScriptGroup.Binding
        //lateinit var adapter:ItemAdapter
        endTimeCalendar.set(Calendar.MONTH, currentMonth+3)

        // 달력 처음 설정
        materialCalendar.state().edit()
            .setMinimumDate(CalendarDay.from(2017, 8, 1))
            .setMaximumDate(CalendarDay.from(currentYear+3, currentMonth, endTimeCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)))
            .setCalendarDisplayMode(CalendarMode.MONTHS)
            .commit()

        // 헤더 레이블 수정
        val date = Calendar.getInstance()

        //글자 커스텀
        materialCalendar.setHeaderTextAppearance(R.style.CalendarWidgetHeader)
        materialCalendar.setWeekDayTextAppearance(R.style.CalendarWidgetWeek)
        materialCalendar.setDateTextAppearance(R.style.CalendarWidgetDate)

        // 부가적 커스텀
        materialCalendar.setTileHeightDp(55) // 타이틀 높이
        materialCalendar.setSelectedDate(CalendarDay.today())  // 오늘 선택

        val selectDecorator = CurrentDayDecorator(requireContext(),CalendarDay.from(2021,8,20))
        val sundayDecorator = SundayDecorator()     // 일요일 빨간색
        val saturdayDecorator = SaturdayDecorator() // 토요일 파란색
        val todayDecorator = TodayDecorator(requireContext())   //오늘 배경설정

        //데코레이터 추가
        materialCalendar.addDecorators(sundayDecorator, saturdayDecorator, todayDecorator,selectDecorator)
    }

    // 여기부터 리사이클뷰 코드-------------------
    override fun onClick(v: View?) {
        TODO("Not yet implemented")
    }

}

// 투데이 데코레이터
class TodayDecorator(context: Context): DayViewDecorator {
    private var date = CalendarDay.today()
    val drawable = context.resources.getDrawable(R.drawable.calendar_today_back)   // 오늘날짜 배경 설정
    override fun shouldDecorate(day: CalendarDay?): Boolean {
        return day?.equals(date)!!
    }
    override fun decorate(view: DayViewFacade?) {
        view?.setBackgroundDrawable(drawable)
    }
}
// 일요일 빨갛게
class SundayDecorator:DayViewDecorator {
    private val calendar = Calendar.getInstance()
    override fun shouldDecorate(day: CalendarDay?): Boolean {
        day?.copyTo(calendar)
        val weekDay = calendar.get(Calendar.DAY_OF_WEEK)
        return weekDay == Calendar.SUNDAY
    }
    override fun decorate(view: DayViewFacade?) {
        view?.addSpan(object: ForegroundColorSpan(Color.parseColor("#EA7159")){})
    }
}
// 토요일 파랗게
class SaturdayDecorator:DayViewDecorator {
    private val calendar = Calendar.getInstance()
    override fun shouldDecorate(day: CalendarDay?): Boolean {
        day?.copyTo(calendar)
        val weekDay = calendar.get(Calendar.DAY_OF_WEEK)
        return weekDay == Calendar.SATURDAY
    }
    override fun decorate(view: DayViewFacade?) {
        view?.addSpan(object: ForegroundColorSpan(Color.parseColor("#7A8FDE")){})
    }
}
class CurrentDayDecorator(context: Context?, currentDay: CalendarDay) : DayViewDecorator {
    val drawable = ContextCompat.getDrawable(context!!, R.drawable.checkbox_off_background)
    var myDay = currentDay
    override fun shouldDecorate(day: CalendarDay): Boolean {
        return day == myDay
    }

    override fun decorate(view: DayViewFacade) {
        view.setSelectionDrawable(drawable!!)
    }
}


