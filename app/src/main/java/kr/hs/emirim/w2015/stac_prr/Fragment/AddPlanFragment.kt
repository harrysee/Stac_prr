package kr.hs.emirim.w2015.stac_prr.Fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_add_plan.*
import kr.hs.emirim.w2015.stac_prr.CustomDialog
import kr.hs.emirim.w2015.stac_prr.MainActivity
import kr.hs.emirim.w2015.stac_prr.R

class AddPlanFragment : Fragment() {
    var date_str :String? = null

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
        addplan_pass_btn.setOnClickListener(){
            val dir = CustomDialog(requireContext())
                .setMessage("작성중인 내용이 사라집니다\n취소하시겠습니까?")
                .setPositiveBtn("네"){
                    activity.fragmentChange_for_adapter(CalenderFragment())
                }
                .setNegativeBtn("아니오"){}
                .show()
        }

        // 완료 눌렀을 때
        addplan_complate_btn.setOnClickListener{
            activity.fragmentChange_for_adapter(CalenderFragment())
        }

        // title 부분
        val title = resources.getStringArray(R.array.title_arr)
        var adapter = ArrayAdapter<String>(
            requireContext(),
            R.layout.spinner_custom,
            title
        )
        Log.d("TAG", "onViewCreated: 어댑터 완성")
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        planets_spinner.adapter= adapter

        // 알람 부분
        val alarm = resources.getStringArray(R.array.alram_arr)
        adapter = ArrayAdapter<String>(
            requireContext(),
            R.layout.spinner_custom,
            alarm
        )
        Log.d("TAG", "onViewCreated: 어댑터 완성")
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        alarm_spinner.adapter= adapter


    }// onViewcreated end
    
}
