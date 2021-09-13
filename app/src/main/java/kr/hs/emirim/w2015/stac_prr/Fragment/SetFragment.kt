package kr.hs.emirim.w2015.stac_prr.Fragment

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_set.*
import kr.hs.emirim.w2015.alarm_test.AlarmReceiver
import kr.hs.emirim.w2015.alarm_test.Constant.Companion.ALARM_TIMER
import kr.hs.emirim.w2015.stac_prr.BuildConfig
import kr.hs.emirim.w2015.stac_prr.MainActivity
import kr.hs.emirim.w2015.stac_prr.R
import java.util.*

class SetFragment : Fragment() {
    val db = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()
    private var alarmMgr: AlarmManager? = null
    private lateinit var alarmIntent: PendingIntent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_set, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        
        alarmMgr = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmIntent = Intent(context, AlarmReceiver::class.java).let { intent ->
            PendingIntent.getBroadcast(context, 0, intent, 0)
        }

        // 버전 가져오기
        val versionName = BuildConfig.VERSION_NAME
        set_imgbtn_version.text = versionName

        super.onViewCreated(view, savedInstanceState)
        val main = activity as MainActivity
        set_imgbtn_nugu.setOnClickListener(){
            main.fragmentChange_for_adapter(SetTosFragment())
        }
        set_imgbtn_iot.setOnClickListener(){
            main.fragmentChange_for_adapter(SetTosFragment())
        }
        set_imgbtn_notice.setOnClickListener(){
            main.fragmentChange_for_adapter(SetNoticeFragment())
        }
        set_imgbtn_ask.setOnClickListener(){
            main.fragmentChange_for_adapter(SetTosFragment())
        }
        set_imgbtn_tos.setOnClickListener(){
            main.fragmentChange_for_adapter(SetTosFragment())
        }

        //알림 체크되면 알림주기
        set_switch_alarm.setOnCheckedChangeListener { buttonView, isChecked ->
            var calendar: Calendar = Calendar.getInstance().apply {
                timeInMillis = System.currentTimeMillis()
                set(Calendar.HOUR_OF_DAY, 8)
                set(Calendar.MINUTE, 30)
            }

            val toastMessage = if (isChecked) {
                auth.currentUser?.let {
                    db.collection("scadule")
                        .document(it.uid)
                        .collection("plan")
                        .get()
                        .addOnSuccessListener {
                            for (doc in it){
                                val date = doc["date"] as Timestamp
                                calendar.time = date.toDate()
                                
                                // 알림 설정
                                alarmMgr?.set(
                                    AlarmManager.RTC_WAKEUP,    //해당시간 절전모드해제
                                    calendar.timeInMillis,
                                    alarmIntent
                                )
                            }
                        }
                }

                "해당시간에 알림이 울립니다"
            } else {
                if (alarmIntent != null && alarmMgr != null) {
                    alarmMgr!!.cancel(alarmIntent)
                }
                "알림 예약을 취소하였습니다."
            }
            Toast.makeText(requireContext(), toastMessage, Toast.LENGTH_SHORT).show()
        }

    }

}