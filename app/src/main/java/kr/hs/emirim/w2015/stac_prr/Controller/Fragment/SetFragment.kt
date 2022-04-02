package kr.hs.emirim.w2015.stac_prr.Controller.Fragment

import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_set.*
import kr.hs.emirim.w2015.stac_prr.BuildConfig
import kr.hs.emirim.w2015.stac_prr.Controller.Activity.MainActivity
import kr.hs.emirim.w2015.stac_prr.R
import java.util.*

class SetFragment : androidx.fragment.app.Fragment() {
    val db = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()
    private var alarmMgr: AlarmManager? = null
    private lateinit var push: SharedPreferences
    private lateinit var alarmIntent: PendingIntent
    private var isOpen : Boolean? = null
    private var noticeSize : Int =0

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
        isOpen = push.getBoolean("isOpen",false)
        noticeSize = push.getInt("noticeSize",0)
        setNoticeDot()  // 점 표시여부
        // 알림 푸쉬 스위치버튼 설정하기
        val isAlarm = push.getBoolean("isAlarm", true)
        set_switch_alarm.isChecked = isAlarm

        // 버전 가져오기
        val versionName = BuildConfig.VERSION_NAME
        set_imgbtn_version.text = versionName

        super.onViewCreated(view, savedInstanceState)
        val main = activity as MainActivity
        set_linear_nugu.setOnClickListener{   //누구 클릭
            main.fragmentChange_for_adapter(SetNuguFragment())
        }
        set_linear_iot.setOnClickListener(){    //아이오티 클릭
            main.fragmentChange_for_adapter(SetIotFragment())
        }
        set_linear_notice.setOnClickListener(){ //공지사항 클릭
            //열었을때 점 사라지게
            push.edit()
                .putBoolean("isOpen",true)
                .apply()
            if (isOpen ==false){
                set_notice_dot.visibility=View.INVISIBLE
            }
            main.fragmentChange_for_adapter(SetNoticeFragment())
        }
        set_linear_ask.setOnClickListener(){     // 문의하기 클릭
            main.fragmentChange_for_adapter(SetAskFragment())
        }
        set_linear_tos.setOnClickListener(){      //이용약관 클릭
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
                    commit()
                }
                "알림 예약을 취소하였습니다."
            }
            Toast.makeText(requireContext(), toastMessage, Toast.LENGTH_SHORT).show()
        }

    }
    fun setNoticeDot(){
        db.collection("noticed")
            .get()
            .addOnSuccessListener { result ->
                Log.d("TAG", "setNoticeDot: 공지사항 가지러옴 : ${result.size()} /$noticeSize")
                if (noticeSize < result.size() || noticeSize> result.size()){
                    Log.d("TAG", "setNoticeDot: 공지사항 가지러옴 개수 : ${result.size()} / $noticeSize")
                    push.edit()
                        .putInt("noticeSize",result.size())
                        .putBoolean("isOpen",false)
                        .apply()
                    Log.d("TAG", "setNoticeDot: 오픈햇냐 : $isOpen")
                    set_notice_dot.visibility=View.VISIBLE
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(requireContext(),"공지사항 불러오기 실패", Toast.LENGTH_SHORT).show()
                Log.d("TAG", "Error getting documents: ", exception)
            }
    }

}