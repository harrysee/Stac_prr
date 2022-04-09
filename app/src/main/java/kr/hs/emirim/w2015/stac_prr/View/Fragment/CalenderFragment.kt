package kr.hs.emirim.w2015.stac_prr.View.Fragment

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.Rect
import android.os.Bundle
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.prolificinteractive.materialcalendarview.*
import kotlinx.android.synthetic.main.fragment_calender.*
import kotlinx.android.synthetic.main.fragment_new_journal.*
import kr.hs.emirim.w2015.stac_prr.Model.PlanModel
import kr.hs.emirim.w2015.stac_prr.View.Adapter.PlanAdapter
import kr.hs.emirim.w2015.stac_prr.View.Decorator.DotDecorator
import kr.hs.emirim.w2015.stac_prr.R
import kr.hs.emirim.w2015.stac_prr.viewModel.PlanViewModel
import kr.hs.emirim.w2015.stac_prr.databinding.FragmentCalenderBinding
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class CalenderFragment : Fragment(){
    private lateinit var binding : FragmentCalenderBinding
    private lateinit var adapter: PlanAdapter
    private lateinit var pref : SharedPreferences
    val dotPlanDay = mutableListOf<CalendarDay>()
    var dotDecorator : DotDecorator? = null
    val outDateFormat = SimpleDateFormat("yyyy. MM. dd")
    val selectDateFormat = SimpleDateFormat("yyyy/MM/dd")
    var datetext = outDateFormat.format(Date().time)
    val model by lazy{
        ViewModelProvider(requireActivity()).get(PlanViewModel::class.java)
    }
    var selec_date: String = selectDateFormat.format(Calendar.getInstance().time)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // onCreateView에서 바인딩을 시켜주고 binding 객체의 root를 리턴
        pref = context?.getSharedPreferences("pref", Context.MODE_PRIVATE)!!
        binding = FragmentCalenderBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // dot 추가할 리스트 넣기 - month는 인덱스가 0이라서 1월 -> 0월로 계산해야됨
        calendarOption()    //캘린더 기본세팅
        addDecorator()      // 추가 커스텀
        binding.planRecyclerview.addItemDecoration(RecyclerViewDecoration(18))
        getPlan()   // 해당 일정들
        val linearLayoutManager = LinearLayoutManager(activity)
        linearLayoutManager.orientation = RecyclerView.VERTICAL
        binding.planRecyclerview.layoutManager = linearLayoutManager

        //날짜 클릭했을때
        binding.materialCalendar.setOnDateChangedListener { widget, date, selected ->
            val simpleDateFormat = SimpleDateFormat("MM월 dd일")
            val dateMd: String = simpleDateFormat.format(date.getCalendar().getTime())
            datetext = outDateFormat.format(date.getCalendar().getTime())
            selec_date = selectDateFormat.format(date.calendar.time)    // 선택한 날짜 업데이트
            Log.d("TAG", "makeTestItems: 선택된 날짜 $selec_date")
            adapter.items = getPlan()
            // 어댑터 새로고침
            adapter.date = selec_date
            adapter.notifyDataSetChanged()
            binding.planTxt.text = dateMd
        }

        //plus btn listener
        binding.planPlusBtn.setOnClickListener() {
            val pcnt = pref.getInt("PlantCnt", 0)
            if (pcnt <= 0){
                Toast.makeText(requireContext(),"식물을 등록하세요", Toast.LENGTH_SHORT ).show()
                return@setOnClickListener
            }
            // 이동
            Log.i(datetext, "onViewCreated: datetext")
            val fragment = AddPlanFragment(); // Fragment 생성
            val bundle = Bundle()
            bundle.putString("date", datetext); //Key, Value
            fragment.arguments = bundle
            Log.d(fragment.arguments.toString(), "plus버튼 클릭됨")
            activity?.supportFragmentManager!!.beginTransaction()
                .replace(R.id.container, fragment)
                .commit()
        }
    }// onViewCreate end

    override fun onDestroyView() {
        super.onDestroyView()
        /*프래그먼트에서는 Nullable 처리를 위해 추가적인 코드가 필요합니다.
        프래그먼트는 뷰 보다 오래 지속되기 때문에 onDestroyView()에서 binding class 인스턴스 null 값으로 변경하여 참조를 정리해주어야 합니다.*/

    }

    // 여기부터 리사이클뷰 코드-------------------
    fun getPlan() : ArrayList<PlanModel>{
        getDotDate()
        var plans = ArrayList<PlanModel>()
        // 리사이클뷰 관련 변수 선언
        model.getDatePlans(selec_date).observe(requireActivity(), androidx.lifecycle.Observer {
            plans = it
            adapter = PlanAdapter(plans,model)      // 일정목록 어댑터 생성
            adapter.items = plans
            binding.planRecyclerview.adapter = adapter
            adapter.notifyDataSetChanged()
        })
        return plans
    }

    fun getDotDate() {
        model.getAllPlans().observe(requireActivity(), androidx.lifecycle.Observer {
            for (document in it) {
                val sundayDecorator = SundayDecorator()     // 일요일 빨간색
                val saturdayDecorator = SaturdayDecorator() // 토요일 파란색
                val todayDecorator = TodayDecorator(requireContext())   //오늘 배경설정
                dotDecorator = DotDecorator("#8EC057", it)  //일정있으면 점찍기

                //데코레이터 추가
                binding.materialCalendar.addDecorators(
                    sundayDecorator,
                    saturdayDecorator,
                    todayDecorator,
                    dotDecorator
                )
                //dotDecorator = DotDecorator("#8EC057", dotPlanDay)
                Log.d("TAG", "점날짜 ${it} => ${dotPlanDay}")
            }
        })
    }

    fun calendarOption() {
        // 캘린더 부분 변수선언
        var startTimeCalendar = Calendar.getInstance()
        var endTimeCalendar = Calendar.getInstance()
        materialCalendar.topbarVisible = true

        val currentYear = startTimeCalendar.get(Calendar.YEAR)
        val currentMonth = startTimeCalendar.get(Calendar.MONTH)
        val currentDate = startTimeCalendar.get(Calendar.DATE)

        endTimeCalendar.set(Calendar.MONTH, currentMonth + 3)

        // 달력 처음 설정
        binding.materialCalendar.state().edit()
            .setMinimumDate(CalendarDay.from(2017, 8, 1))
            .setMaximumDate(
                CalendarDay.from(
                    currentYear + 3,
                    currentMonth,
                    endTimeCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)
                )
            )
            .setCalendarDisplayMode(CalendarMode.MONTHS)
            .commit()

        // 헤더 레이블 수정
        val date = Calendar.getInstance()

        //글자 커스텀
        binding.materialCalendar.setHeaderTextAppearance(R.style.CalendarWidgetHeader)
        binding.materialCalendar.setWeekDayTextAppearance(R.style.CalendarWidgetWeek)
        binding.materialCalendar.setDateTextAppearance(R.style.CalendarWidgetDate)

        // 부가적 커스텀
        binding.materialCalendar.setTileHeightDp(57) // 타이틀 높이
        binding.materialCalendar.setSelectedDate(CalendarDay.today())  // 오늘 선택
    }

    fun addDecorator() {
        //getDotDate()    //점찍을 날짜 추가
        Log.d("TAG", "onViewCreated: ${dotPlanDay}")

        val sundayDecorator = SundayDecorator()     // 일요일 빨간색
        val saturdayDecorator = SaturdayDecorator() // 토요일 파란색
        val todayDecorator = TodayDecorator(requireContext())   //오늘 배경설정
        dotDecorator = DotDecorator("#8EC057", dotPlanDay)  //일정있으면 점찍기

        //데코레이터 추가
        val decorator = binding.materialCalendar.addDecorators(
            sundayDecorator,
            saturdayDecorator,
            todayDecorator,
            dotDecorator
        )

    }

}

// 투데이 데코레이터
class TodayDecorator(context: Context) : DayViewDecorator {
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
class SundayDecorator : DayViewDecorator {
    private val calendar = Calendar.getInstance()
    override fun shouldDecorate(day: CalendarDay?): Boolean {
        day?.copyTo(calendar)
        val weekDay = calendar.get(Calendar.DAY_OF_WEEK)
        return weekDay == Calendar.SUNDAY
    }

    override fun decorate(view: DayViewFacade?) {
        view?.addSpan(object : ForegroundColorSpan(Color.parseColor("#EA7159")) {})
    }
}

// 토요일 파랗게
class SaturdayDecorator : DayViewDecorator {
    private val calendar = Calendar.getInstance()
    override fun shouldDecorate(day: CalendarDay?): Boolean {
        day?.copyTo(calendar)
        val weekDay = calendar.get(Calendar.DAY_OF_WEEK)
        return weekDay == Calendar.SATURDAY
    }

    override fun decorate(view: DayViewFacade?) {
        view?.addSpan(object : ForegroundColorSpan(Color.parseColor("#7A8FDE")) {})
    }
}

// 리사이클러뷰 위아래 간격 조절
class RecyclerViewDecoration(val divHeight: Int) : ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State,
    ) {
        super.getItemOffsets(outRect, view!!, parent!!, state!!)
        outRect.top = divHeight
    }
}
