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
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.prolificinteractive.materialcalendarview.*
import kotlinx.android.synthetic.main.fragment_calender.*
import kotlinx.android.synthetic.main.fragment_new_journal.*
import kr.hs.emirim.w2015.stac_prr.Adapter.PlanAdapter
import kr.hs.emirim.w2015.stac_prr.Decorator.DotDecorator
import kr.hs.emirim.w2015.stac_prr.ItemModel
import kr.hs.emirim.w2015.stac_prr.R
import kr.hs.emirim.w2015.stac_prr.databinding.FragmentCalenderBinding
import java.lang.String.format
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


class CalenderFragment : Fragment(), View.OnClickListener {
    private var _binding: FragmentCalenderBinding? = null//<layout></layout>으로 감싼 것만 바인딩가능
    private val binding get() = _binding!!
    lateinit var adapter: PlanAdapter
    val db = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()
    var model = ItemModel()
    val dotPlanDay = mutableListOf<CalendarDay>()
    var dotDecorator : DotDecorator? = null
    val outDateFormat = SimpleDateFormat("yyyy. MM. dd")
    val selectDateFormat = SimpleDateFormat("yyyy/MM/dd")
    var datetext = outDateFormat.format(Date().time)
    var selec_date: String = selectDateFormat.format(Calendar.getInstance().time)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // onCreateView에서 바인딩을 시켜주고 binding 객체의 root를 리턴
        _binding = FragmentCalenderBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // dot 추가할 리스트 넣기 - month는 인덱스가 0이라서 1월 -> 0월로 계산해야됨
        calendarOption()    //캘린더 기본세팅
        addDecorator()      // 추가 커스텀
        binding.planRecyclerview.addItemDecoration(RecyclerViewDecoration(18))
        init()  // recycleview 설정

        //날짜 클릭했을때
        binding.materialCalendar.setOnDateChangedListener { widget, date, selected ->
            val simpleDateFormat = SimpleDateFormat("MM월 dd일")
            val dateMd: String = simpleDateFormat.format(date.getCalendar().getTime())
            datetext = outDateFormat.format(date.getCalendar().getTime())
            selec_date = selectDateFormat.format(date.calendar.time)
            // 어댑터 새로고침
            model.date = selec_date
            adapter.date = selec_date

            model.items.clear()
            Log.d("TAG", "makeTestItems: 선택된 날짜 $selec_date")
            db.collection("schedule")
                .document(auth.uid.toString())
                .collection("plans")
                .whereEqualTo("str_date",selec_date)
                .get()
                .addOnSuccessListener {
                    Log.d("", "makeTestItems: 해당 날짜 데이터 가져오기 성공")
                    for (document in it){
                        var item = model.ItemEntity()
                        Log.d("TAG", "makeTestItems: ${document.data}")
                        if (document["str_date"] != null) {
                            Log.d("TAG", "makeTestItems: 모델 ${document.data}")
                            item.contents = document["title"] as String?
                            item.name = document["name"] as String?
                            item.isChecked = document["checkbox"] as Boolean
                            item.memo = document["memo"] as String?
                            item.docId = document.id
                            model.items.add(item)
                        }
                        adapter.items = model
                        adapter.notifyDataSetChanged()
                        Log.d("TAG", "makeTestItems: 추가된 아이템 : ${model.items[0]?.isChecked}")
                    }
                }

            binding.planTxt.text = dateMd
        }

        //plus btn listener
        binding.planPlusBtn.setOnClickListener() {
            Log.i(datetext, "onViewCreated: datetext")
            //val activity = activity as MainActivity
            val fragment = AddPlanFragment(); // Fragment 생성
            val bundle = Bundle()
            bundle.putString("date", datetext); //Key, Value
            fragment.arguments = bundle
            Log.i(bundle.toString(), "onViewCreated: bundle")
            //activity.fragmentChange_for_adapter(AddPlanFragment())
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
        _binding = null
    }

    // 여기부터 리사이클뷰 코드-------------------
    fun init() {
        getDotDate()
        adapter = PlanAdapter()
        // 리사이클뷰 관련 변수 선언
        model.items.clear()
        Log.d("TAG", "makeTestItems: 선택된 날짜 $selec_date")
        db.collection("schedule")
            .document(auth.uid.toString())
            .collection("plans")
            .whereEqualTo("str_date",selec_date)
            .get()
            .addOnSuccessListener {
                Log.d("", "makeTestItems: 해당 날짜 데이터 가져오기 성공")
                for (document in it){
                    var item = model.ItemEntity()
                    Log.d("TAG", "makeTestItems: ${document.data}")
                    if (document["str_date"] != null) {
                        Log.d("TAG", "makeTestItems: 모델 ${document.data}")
                        item.contents = document["title"] as String?
                        item.name = document["name"] as String?
                        item.isChecked = document["checkbox"] as Boolean
                        item.memo = document["memo"] as String?
                        item.docId = document.id
                        model.items.add(item)
                    }
                    adapter.items=model
                    adapter.notifyDataSetChanged()
                    Log.d("TAG", "makeTestItems: 추가된 아이템 : ${model.items[0]?.isChecked}")
                }
            }

        var linearLayoutManager = LinearLayoutManager(activity)
        linearLayoutManager.orientation = RecyclerView.VERTICAL
        binding.planRecyclerview.layoutManager = linearLayoutManager
        binding.planRecyclerview.adapter = adapter
        Log.d("TAG", "init: 일정 어댑터 적용함 ${PlanAdapter()}")
    }

    override fun onClick(v: View?) {
        Log.d("", "onClick: 버튼이 없기때문에 없어도됨")
    }

    /* fun getDate(timestamp: Long) :String {
         val calendar = Calendar.getInstance(Locale.ENGLISH)
         calendar.timeInMillis = timestamp * 1000L
         val date = DateFormat.format("dd-MM-yyyy",calendar).toString()
         return date
     }*/

    fun getDotDate() {
        val cal = Calendar.getInstance()
        db.collection("schedule")
            .document(auth.uid.toString())
            .collection("plans")
            .get()
            .addOnSuccessListener {
                Log.d("", "makeTestItems: 해당 날짜 데이터 가져오기 성공")
                for (document in it) {
                    /*val date = document["date"] as Timestamp
                    val milliseconds = date.seconds * 1000 + date.nanoseconds / 1000000
                    val netDate = Date(milliseconds)*/
                    if (document["str_date"] != null) {
                        val str_date = document["str_date"] as String?
                        val cal_str = SimpleDateFormat("yyyy/MM/dd").parse(str_date)
                        dotPlanDay.add(CalendarDay.from(cal_str))
                    }
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
                    //dotDecorator = DotDecorator("#8EC057", dotPlanDay)
                    Log.d("TAG", "점날짜 ${document.id} => ${dotPlanDay}")
                }
            }
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
