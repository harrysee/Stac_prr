package kr.hs.emirim.w2015.stac_prr.Fragment

import android.content.Context
import android.graphics.Color
import android.graphics.Rect
import android.os.Bundle
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.prolificinteractive.materialcalendarview.*
import kotlinx.android.synthetic.main.fragment_calender.*
import kr.hs.emirim.w2015.stac_prr.Adapter.PlanAdapter
import kr.hs.emirim.w2015.stac_prr.ItemModel
import kr.hs.emirim.w2015.stac_prr.R
import kr.hs.emirim.w2015.stac_prr.databinding.FragmentCalenderBinding
import java.text.SimpleDateFormat
import java.util.*


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class CalenderFragment : Fragment(),View.OnClickListener {
    private var _binding: FragmentCalenderBinding? = null//<layout></layout>으로 감싼 것만 바인딩가능
    private val binding get() = _binding!!
    lateinit var adapter: PlanAdapter
    var model = ItemModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // onCreateView에서 바인딩을 시켜주고 binding 객체의 root를 리턴
        _binding = FragmentCalenderBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // 캘린더 부분 변수선언
        var startTimeCalendar = Calendar.getInstance()
        var endTimeCalendar = Calendar.getInstance()
        materialCalendar.topbarVisible = true

        val currentYear = startTimeCalendar.get(Calendar.YEAR)
        val currentMonth = startTimeCalendar.get(Calendar.MONTH)
        val currentDate = startTimeCalendar.get(Calendar.DATE)
        
        endTimeCalendar.set(Calendar.MONTH, currentMonth+3)

        // 달력 처음 설정
        binding.materialCalendar.state().edit()
            .setMinimumDate(CalendarDay.from(2017, 8, 1))
            .setMaximumDate(CalendarDay.from(currentYear+3, currentMonth, endTimeCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)))
            .setCalendarDisplayMode(CalendarMode.MONTHS)
            .commit()

        // 헤더 레이블 수정
        val date = Calendar.getInstance()

        //글자 커스텀
        binding.materialCalendar.setHeaderTextAppearance(R.style.CalendarWidgetHeader)
        binding.materialCalendar.setWeekDayTextAppearance(R.style.CalendarWidgetWeek)
        binding.materialCalendar.setDateTextAppearance(R.style.CalendarWidgetDate)

        // 부가적 커스텀
        binding.materialCalendar.setTileHeightDp(55) // 타이틀 높이
        binding.materialCalendar.setSelectedDate(CalendarDay.today())  // 오늘 선택

        val sundayDecorator = SundayDecorator()     // 일요일 빨간색
        val saturdayDecorator = SaturdayDecorator() // 토요일 파란색
        val todayDecorator = TodayDecorator(requireContext())   //오늘 배경설정

        //데코레이터 추가
        binding.materialCalendar.addDecorators(sundayDecorator, saturdayDecorator, todayDecorator)

        //날짜 클릭했을때
        binding.materialCalendar.setOnDateChangedListener { widget, date, selected ->
            val simpleDateFormat = SimpleDateFormat("MM월 dd일")
            val date: String = simpleDateFormat.format(date.getCalendar().getTime())

            binding.planTxt.text = date
           /* Toast.makeText(
                ApplicationProvider.getApplicationContext<Context>(),
                date,
                Toast.LENGTH_SHORT
            ).show()
            val CAIntent = Intent(
                ApplicationProvider.getApplicationContext<Context>(),
                ConfirmScheduleActivity::class.java
            )
            CAIntent.putExtra("날짜", date)
            startActivity(CAIntent)*/
        }

        binding.planRecyclerview.addItemDecoration(RecyclerViewDecoration(18))
        init()  // recycleview 설정
        //plus btn listener
        binding.planPlusBtn.setOnClickListener(){
            Log.d("plus", "plus버튼 클릭됨")
        }
    }// onViewCreate end

    override fun onDestroyView() {
        super.onDestroyView()
        /*프래그먼트에서는 Nullable 처리를 위해 추가적인 코드가 필요합니다.
        프래그먼트는 뷰 보다 오래 지속되기 때문에 onDestroyView()에서 binding class 인스턴스 null 값으로 변경하여 참조를 정리해주어야 합니다.*/
        _binding = null
    }
    // 여기부터 리사이클뷰 코드-------------------
    fun init(){
        // 리사이클뷰 관련 변수 선언
        model.makeTestItems()
        adapter = PlanAdapter()
        adapter.items = model

        var linearLayoutManager = LinearLayoutManager(activity)
        linearLayoutManager.orientation = RecyclerView.VERTICAL
        binding.planRecyclerview.layoutManager = linearLayoutManager
        binding.planRecyclerview.adapter = adapter
    }

    override fun onClick(v: View?) {
        Log.d("", "onClick: 버튼이 없기때문에 없어도됨")
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

// 리사이클러뷰 위아래 간격 조절
class RecyclerViewDecoration(val divHeight: Int) : ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view!!, parent!!, state!!)
        outRect.top = divHeight
    }
}
