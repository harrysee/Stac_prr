package kr.hs.emirim.w2015.stac_prr.View.Fragment

import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_set.*
import kr.hs.emirim.w2015.stac_prr.BuildConfig
import kr.hs.emirim.w2015.stac_prr.MainActivity
import kr.hs.emirim.w2015.stac_prr.R
import kr.hs.emirim.w2015.stac_prr.Repository.NoticeRepository
import kr.hs.emirim.w2015.stac_prr.databinding.FragmentSetBinding
import kr.hs.emirim.w2015.stac_prr.viewModel.NoticeViewModel
import java.util.*

class SetFragment : Fragment() {
    val auth = FirebaseAuth.getInstance()
    private var alarmMgr: AlarmManager? = null
    private lateinit var push: SharedPreferences
    private lateinit var alarmIntent: PendingIntent
    private var isOpen : Boolean? = null
    private lateinit var binding : FragmentSetBinding
    val model by lazy {
        ViewModelProvider(requireActivity()).get(NoticeViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        push = context?.getSharedPreferences("push", Context.MODE_PRIVATE)!!
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSetBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        isOpen = push.getBoolean("isOpen",false)
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
    
    // 공지사항 읽었는지 보여주기
    fun setNoticeDot(){
        model.getNoticeDot(requireActivity()).observe(requireActivity(), androidx.lifecycle.Observer {
            Log.i("TAG", "setNoticeDot: 가져오기"+it)
            if(it){
                val isopen = push.getBoolean("isOpen",false)
                if(isopen){
                    binding.setNoticeDot.visibility=View.VISIBLE
                }
            }else{
                Toast.makeText(requireActivity(),"네트워크 연결을 확인해주세요", Toast.LENGTH_SHORT).show()
            }
        })
    }

}