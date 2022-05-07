package kr.hs.emirim.w2015.stac_prr.View.Fragment

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.Timestamp
import kotlinx.android.synthetic.main.fragment_add_plan.*
import kr.hs.emirim.w2015.stac_prr.View.Dialog.CustomDialog
import kr.hs.emirim.w2015.stac_prr.MainActivity
import kr.hs.emirim.w2015.stac_prr.R
import kr.hs.emirim.w2015.stac_prr.Receiver.AlarmReceiver
import kr.hs.emirim.w2015.stac_prr.Receiver.DeviceBootReceiver
import kr.hs.emirim.w2015.stac_prr.viewModel.AddPlanViewModel
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class AddPlanFragment : Fragment() {
    var date_str: String? = null
    val cal: Calendar = Calendar.getInstance()
    lateinit var push: SharedPreferences
    lateinit var alarmPref: SharedPreferences
    lateinit var nadapter: ArrayAdapter<String>
    val model by lazy {
        ViewModelProvider(requireActivity()).get(AddPlanViewModel::class.java)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    @SuppressLint("SimpleDateFormat")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        Log.d("TAG", "argument: $arguments")
        date_str = arguments?.getString("date") // 전달한 key 값
        try {
            val date = SimpleDateFormat("yyyy. MM. dd").parse(date_str)
            cal.time = date
            Log.d("TAG", "onCreateView: string to date 저장")
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        push = context?.getSharedPreferences("push", Context.MODE_PRIVATE)!!
        alarmPref = context?.getSharedPreferences("alarms", Context.MODE_PRIVATE)!!
        return inflater.inflate(R.layout.fragment_add_plan, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val activity = activity as MainActivity
        R.style.AlertDialog_AppCompat
        addplan_date_txt.text = date_str

        // 이미지 화살표 눌렀을때
        addplan_pass_btn.setOnClickListener() {
            val dir = CustomDialog(requireContext())
                .setMessage("작성중인 내용이 사라집니다\n취소하시겠습니까?")
                .setPositiveBtn("네") {
                    activity.fragmentChange_for_adapter(CalenderFragment())
                }
                .setNegativeBtn("아니오") {}
                .show()
        }

        // 완료 눌렀을 때
        val planets_spinner: Spinner = view.findViewById(R.id.planets_spinner)
        val plant_name_spinner: Spinner = view.findViewById(R.id.plant_name_spinner)
        addplan_complate_btn.setOnClickListener {
            Toast.makeText(requireContext(), "업로드 중..", Toast.LENGTH_SHORT)
            // 파이어스토어에 데이터 저장
            val id = cal.timeInMillis.toInt()
            val date: Date = cal.time
            val str_date = SimpleDateFormat("yyyy/MM/dd").format(date)
            //val str_date = cal.get(Calendar.YEAR).toString() + "/"+ cal.get(Calendar.MONTH).toString() +"/"+cal.get(Calendar.DAY_OF_MONTH).toString()
            // 올릴 필드 설정하기
            val docData = mapOf<String,Any>(
                "title" to planets_spinner.selectedItem.toString(),
                "name" to plant_name_spinner.selectedItem.toString(),
                "checkbox" to false,
                "date" to Timestamp(date),   // 날짜
                "alarmId" to id,
                "str_date" to str_date,
                "memo" to addplan_memo.text.toString()
            )
            Log.d("TAG", "onViewCreated: 일정에 저장된 날짜 : $str_date")
            // 콜렉션에 문서 생성하기
            model?.insertPlan(docData)
            Toast.makeText(requireContext(), "업로드 완료 !", Toast.LENGTH_LONG).show()
            Log.d("TAG", "onViewCreated: 파이어 업로드 완료")

            //알람 일단 등록
            val pm = context?.packageManager
            val receiver = ComponentName(requireContext(), DeviceBootReceiver::class.java)
            val alarmIntent = Intent(context, AlarmReceiver::class.java)
            alarmIntent.putExtra("title", planets_spinner.selectedItem.toString())
            alarmIntent.putExtra("name", plant_name_spinner.selectedItem.toString())
            alarmIntent.putExtra("content", addplan_memo.text.toString())
            alarmIntent.putExtra("id", id)
            val pendingIntent = PendingIntent.getBroadcast(context, id, alarmIntent, 0)
            val alarmManager = context?.getSystemService(AppCompatActivity.ALARM_SERVICE) as AlarmManager

            //알림 등록 아이디 설정
            alarmPref.edit()
                .putBoolean(id.toString(), true)
                .apply()

            Log.d("TAG", "diaryNotification: 알림 이동시 : $id")
            // 알림 설정
            if  (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {  // 버전에 따른 도즈모드 처리 우선순위
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,
                    cal.timeInMillis,
                    pendingIntent)
            }else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, cal.timeInMillis, pendingIntent)
            }else{
                alarmManager.set(AlarmManager.RTC_WAKEUP, cal.timeInMillis, pendingIntent)
            }
            Log.d("TAG", "onViewCreated: 알림 시간 : ${cal.time}")
            // 부팅 후 실행되는 리시버 사용가능하게 설정
            pm?.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP)

            activity.fragmentChange_for_adapter(CalenderFragment()) // 이동하기
        }

        setSpinner()
        setTime()
    }// onViewcreated end

    fun setTime() {
        // 시간 받는 리스너 설정
        val timepickerlistener = TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
            cal.set(Calendar.HOUR_OF_DAY, hourOfDay)
            cal.set(Calendar.MINUTE, minute)
            var format = SimpleDateFormat("오전 HH:mm").format(cal.time)
            if (hourOfDay > 12) {
                cal.set(Calendar.HOUR_OF_DAY, hourOfDay - 12)
                format = SimpleDateFormat("오후 HH:mm").format(cal.time)
            }
            cal.set(Calendar.HOUR_OF_DAY, hourOfDay)
            addplan_time.text = format
        }

        addplan_time.setOnClickListener {
            val dialog = TimePickerDialog(
                requireContext(),
                R.style.TimePicker,
                timepickerlistener,
                cal.get(Calendar.HOUR_OF_DAY),
                cal.get(Calendar.MINUTE),
                false
            )
            //dialog.setTitle("알림 시간 선택")
            dialog.window!!.setBackgroundDrawableResource(R.color.back_gray)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.show()

        }
    }
    // 스피너 설정하는 함수
    fun setSpinner() {
        // title 부분
        val title = resources.getStringArray(R.array.title_arr)
        val adapter = ArrayAdapter<String>(
            requireContext(),
            R.layout.spinner_plan_title,
            title
        )
        Log.d("TAG", "onViewCreated: 어댑터 완성")
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        planets_spinner.adapter = adapter

        // 대상 부분
        model.getNames().observe(requireActivity(), androidx.lifecycle.Observer {
            Log.i("TAG", "setSpinner: 대상 안")
            val names_arr = it
            nadapter = ArrayAdapter<String>(
                requireContext(),
                R.layout.spinner_custom,
                names_arr
            )
            Log.i("TAG", "setSpinner: 대상 가져옴"+it)
            nadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            plant_name_spinner.adapter = nadapter
            nadapter.notifyDataSetChanged()
        })
    }

}