package kr.hs.emirim.w2015.stac_prr.Fragment

import android.app.TimePickerDialog
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_add_plan.*
import kr.hs.emirim.w2015.stac_prr.CustomDialog
import kr.hs.emirim.w2015.stac_prr.MainActivity
import kr.hs.emirim.w2015.stac_prr.R
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class AddPlanFragment : Fragment(){
    var date_str: String? = null
    val cal = Calendar.getInstance()
    val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("TAG", "argument: $arguments")
        date_str = arguments?.getString("date") // 전달한 key 값

        // Inflate the layout for this fragment
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
        addplan_complate_btn.setOnClickListener {
            activity.fragmentChange_for_adapter(CalenderFragment())
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
            if (hourOfDay >12){
                cal.set(Calendar.HOUR_OF_DAY, hourOfDay-12)
                format = SimpleDateFormat("오후 HH:mm").format(cal.time)
            }
            addplan_time.text = format
        }

        addplan_time.setOnClickListener {
            val dialog = TimePickerDialog(
                requireContext(),
                R.style.TimePicker,
                timepickerlistener,
                15,
                24,
                false
            )
            //dialog.setTitle("알림 시간 선택")
            dialog.window!!.setBackgroundDrawableResource(R.color.back_gray)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.show()

        }
    }

    fun setSpinner(){
        val names_arr = ArrayList<String>()

        // title 부분
        val title = resources.getStringArray(R.array.title_arr)
        var adapter = ArrayAdapter<String>(
            requireContext(),
            R.layout.spinner_custom,
            title
        )
        Log.d("TAG", "onViewCreated: 어댑터 완성")
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        planets_spinner.adapter = adapter

        // 대상 부분
        val names = getNames()
        var nadapter = ArrayAdapter<String>(
            requireContext(),
            R.layout.spinner_custom,
            names_arr
        )
        Log.d("TAG", "onViewCreated: 어댑터 완성")
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        planets_spinner.adapter = adapter

        // 알람 부분
        val alarm = resources.getStringArray(R.array.alram_arr)
        adapter = ArrayAdapter<String>(
            requireContext(),
            R.layout.spinner_custom_alarm,
            alarm
        )
        Log.d("TAG", "onViewCreated: 어댑터 완성")
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        alarm_spinner.adapter = adapter
    }

    fun getNames() : ArrayList<String?>{
        val auth = Firebase.auth.currentUser
        val names = ArrayList<String?>()

        db.collection("plant_info")
            .whereEqualTo("userId",auth?.uid)
            .get()
            .addOnSuccessListener { 
                for (doc in it){
                    names.add(doc["name"] as String?)
                }
            }.addOnFailureListener {
                Log.d("TAG", "getNames: spinner 식물 이름들 보여주기 실패")
            }
        return names
    }
}
