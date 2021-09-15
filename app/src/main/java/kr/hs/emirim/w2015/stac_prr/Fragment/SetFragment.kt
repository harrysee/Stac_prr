package kr.hs.emirim.w2015.stac_prr.Fragment

import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_add_plan.*
import kotlinx.android.synthetic.main.fragment_set.*
import kr.hs.emirim.w2015.stac_prr.BuildConfig
import kr.hs.emirim.w2015.stac_prr.MainActivity
import kr.hs.emirim.w2015.stac_prr.R
import kr.hs.emirim.w2015.stac_prr.Receiver.AlarmReceiver
import kr.hs.emirim.w2015.stac_prr.Receiver.DeviceBootReceiver
import java.text.SimpleDateFormat
import java.util.*

class SetFragment : Fragment() {
    val db = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()
    private var alarmMgr: AlarmManager? = null
    private lateinit var push: SharedPreferences
    private lateinit var alarmIntent: PendingIntent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        push = context?.getSharedPreferences("push", Context.MODE_PRIVATE)!!
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_set, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        // 알림 푸쉬 스위치버튼 설정하기
        val isAlarm = push.getBoolean("isAlarm", false)
        set_switch_alarm.isChecked = isAlarm

        // 버전 가져오기
        val versionName = BuildConfig.VERSION_NAME
        set_imgbtn_version.text = versionName

        super.onViewCreated(view, savedInstanceState)
        val main = activity as MainActivity
        set_imgbtn_nugu.setOnClickListener(){
            main.fragmentChange_for_adapter(SetNuguFragment())
        }
        set_imgbtn_iot.setOnClickListener(){
            main.fragmentChange_for_adapter(SetIotFragment())
        }
        set_imgbtn_notice.setOnClickListener(){
            main.fragmentChange_for_adapter(SetNoticeFragment())
        }
        set_imgbtn_ask.setOnClickListener(){
            main.fragmentChange_for_adapter(SetAskFragment())
        }
        set_imgbtn_tos.setOnClickListener(){
            main.fragmentChange_for_adapter(SetTosFragment())
        }

        //알림 체크되면 알림주기
        set_switch_alarm.setOnCheckedChangeListener { buttonView, isChecked ->
            var cal: Calendar = Calendar.getInstance().apply {
                timeInMillis = System.currentTimeMillis()
                set(Calendar.HOUR_OF_DAY, 8)
                set(Calendar.MINUTE, 30)
            }
            var cal2: Calendar = Calendar.getInstance()

            val toastMessage = if (isChecked) {
                auth.currentUser?.let {
                    db.collection("schedule")
                        .document(it.uid)
                        .collection("plans")
                        .get()
                        .addOnSuccessListener {
                            for (doc in it){
                                val date = doc["date"] as Timestamp
                                cal.time = date.toDate()
                                cal2.time = Date(System.currentTimeMillis()) // 현재날짜
                                
                                // 날짜 지났는지 확인
                                if (cal2.after(cal)){   //인자보다 미래인지 확인
                                    continue
                                }
                                
                                // 알림 설정
                                val idCnt = push.getInt("notifyid",0)

                                val pm = context?.packageManager
                                val receiver = ComponentName(requireContext(), DeviceBootReceiver::class.java)
                                val alarmIntent = Intent(context, AlarmReceiver::class.java)
                                alarmIntent.putExtra("title", doc["title"] as String?)
                                if (plant_name_spinner.selectedItem as String? != null){
                                    alarmIntent.putExtra("name",plant_name_spinner.selectedItem as String?)
                                }else{
                                    alarmIntent.putExtra("name","미정")
                                }
                                alarmIntent.putExtra("content",addplan_memo.text.toString())
                                val pendingIntent = PendingIntent.getBroadcast(context, idCnt , alarmIntent, 0)
                                val alarmManager = context?.getSystemService(AppCompatActivity.ALARM_SERVICE) as AlarmManager

                                if (alarmManager != null) {
                                    push.edit()     //알림 등록 겹치지않게
                                        .putInt("notifyid", idCnt + 1)
                                        .apply()

                                    alarmManager.setRepeating(
                                        AlarmManager.RTC_WAKEUP,
                                        cal.timeInMillis,
                                        AlarmManager.INTERVAL_DAY,
                                        pendingIntent)
                                    Log.d("TAG", "diaryNotification: 알림 이동시 : $idCnt")
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,
                                            cal.timeInMillis,
                                            pendingIntent)
                                    }
                                    // 부팅 후 실행되는 리시버 사용가능하게 설정
                                    pm?.setComponentEnabledSetting(receiver,
                                        PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                                        PackageManager.DONT_KILL_APP)
                                } // alarmManager if 삭제
                            }
                        }
                }
                with(push.edit()) {
                    putBoolean("isAlarm", true)
                    commit()
                }
                
                "알림설정을 활성화 했습니다"
            } else {
                //알림 초기화
                val notificationManager = context?.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.cancelAll()
                
                //알림 인덱스 초기화, 토글버튼 false
                with(push.edit()) {
                    putBoolean("isAlarm", false)
                    putInt("notifyid",0)
                    commit()
                }
                "알림 예약을 취소하였습니다."
            }
            Toast.makeText(requireContext(), toastMessage, Toast.LENGTH_SHORT).show()
        }

    }

}