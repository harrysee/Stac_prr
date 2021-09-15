package kr.hs.emirim.w2015.stac_prr.Fragment

import android.animation.ObjectAnimator
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.fragment_plant_info.*
import kr.hs.emirim.w2015.stac_prr.CustomDialog
import kr.hs.emirim.w2015.stac_prr.MainActivity
import kr.hs.emirim.w2015.stac_prr.R
import java.text.SimpleDateFormat
import java.util.*

class PlantInfoFragment : Fragment(){
    private var docId : String? = null
    private var imgUri :String? = null
    private var isFabOpen = false // Fab 버튼 default는 닫혀있음
    private val db = FirebaseFirestore.getInstance()
    private var name : String? = "해당식물"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        docId = arguments?.getString("docId")
        imgUri = arguments?.getString("imgUri")
        val view: View = inflater.inflate(R.layout.fragment_plant_info, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //실행하면 데이터 가져와서 보여주기
        val getToday: Calendar = Calendar.getInstance()
        val pdate: Calendar = Calendar.getInstance()
        getToday.setTime(Date()) //금일 날짜

        Log.d("TAG,", "onViewCreated: 식물정보 가져온 이미지 uri $imgUri")
        val storage = FirebaseStorage.getInstance()
        val backgound = view.findViewById<ImageView>(R.id.info_background_img)
        Glide.with(requireContext()) //쓸곳
            .load(imgUri)  //이미지 받아올 경로
            .into(backgound)    // 받아온 이미지를 받을 공간

        db.collection("plant_info").document(docId!!)
            .get()
            .addOnSuccessListener {
                Log.d("TAG", "onViewCreated: 식물 정보 가져오기 성!공!")
                name = it["name"] as String?
                info_plant_name.text = name // 식물 이름 재설정
                info_plant_spacies.text = it["specise"] as String?  // 식물 종류 재설정
                val sdf = SimpleDateFormat("yyyy-MM-dd")
                //일자수 차이 구하기
                val timestamp = it["date"] as Timestamp
                val d : Date= timestamp.toDate()
                pdate.time = d
                val diffSec: Long = (getToday.timeInMillis - pdate.timeInMillis) / 1000
                val diffDays = diffSec / (24 * 60 * 60)
                Log.d("TAG", "onViewCreated: 저장된 날짜 데이터 : $d / $diffSec / $diffDays")
                //설정하기
                info_day.text = diffDays.toString() +"일"
                info_date_text.text = sdf.format(d)
                info_water_icon_txt.text = it["water"] as String? + "주기"
                info_c_text.text = it["temperature"] as String?
                info_led_text.text = it["led"] as String?
                info_water_text.text = it["water"] as String?
                info_memo_text.text = it["memo"] as String?

            }.addOnFailureListener {
                Log.d(it.toString(), "onViewCreated: 식물정보를 가져오기 못함")
            }

        setFabClick() // 수정 삭제 버튼
        info_pass_btn.setOnClickListener(){
            val activity = activity as MainActivity
            activity.fragmentChange_for_adapter(HomeFragment())
        }
    }

    //fab각자 클릭 리스너
    private fun setFabClick(){
        info_fab.setOnClickListener {   //제일밑에꺼 눌렀을때
            toggleFab()
        }
        info_fab_edit.setOnClickListener {      //수정버튼
            // 수정하는 레이아웃으로 트렌젝션 시키기
            val fragment = NewPlantFragment()
            val bundle = Bundle()
            bundle.putBoolean("isEdit",true)
            bundle.putString("docId",docId)
            bundle.putString("imgUri",imgUri)
            fragment.arguments = bundle
            activity?.supportFragmentManager?.beginTransaction()
                ?.replace(R.id.container,fragment)
                ?.commit()
        }

        info_fab_del.setOnClickListener {      // 삭제버튼
            //다이얼로그 띄우기
            val dir = CustomDialog(requireContext())
                .setMessage("$name 을/를 삭제하시겠습니까?")
                .setPositiveBtn("네") {
                    // 현재 아이템 파이어베이스에서 삭제하기

                        //plant_info에서 삭제
                        //스케쥴, 일지에서도 삭제
                }
                .setNegativeBtn("아니오") {}
                .show()
        }
    }

    //fab 나오게하는 애니메이션
    private fun toggleFab() {
        Log.d("TAG", "toggleFab: fab 애니메이션 실행")
        // 플로팅 액션 버튼 닫기 - 열려있는 플로팅 버튼 집어넣는 애니메이션
        if (isFabOpen) {
            ObjectAnimator.ofFloat(info_fab_del, "translationY", 0f).apply { start() }
            ObjectAnimator.ofFloat(info_fab_edit, "translationY", 0f).apply { start() }
            ObjectAnimator.ofFloat(info_fab, View.ROTATION, 45f, 0f).apply { start() }
        } else { // 플로팅 액션 버튼 열기 - 닫혀있는 플로팅 버튼 꺼내는 애니메이션
            ObjectAnimator.ofFloat(info_fab_del, "translationY", -360f).apply { start() }
            ObjectAnimator.ofFloat(info_fab_edit, "translationY", -180f).apply { start() }
            ObjectAnimator.ofFloat(info_fab, View.ROTATION, 0f, 45f).apply { start() }
        }

        isFabOpen = !isFabOpen

    }
}