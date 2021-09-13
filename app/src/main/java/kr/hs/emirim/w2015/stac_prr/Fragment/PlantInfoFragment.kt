package kr.hs.emirim.w2015.stac_prr.Fragment

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
import kr.hs.emirim.w2015.stac_prr.MainActivity
import kr.hs.emirim.w2015.stac_prr.R
import java.text.SimpleDateFormat
import java.util.*

class PlantInfoFragment : Fragment(){
    private var docId : String? = null
    private var imgUri :String? = null
    private val db = FirebaseFirestore.getInstance()
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
        val httpsReference = imgUri?.let { storage.getReferenceFromUrl(it) }
        val backgound = view.findViewById<ImageView>(R.id.info_background_img)
        Glide.with(requireContext()) //쓸곳
            .load(httpsReference)  //이미지 받아올 경로
            .into(backgound)    // 받아온 이미지를 받을 공간

        db.collection("plant_info").document(docId!!)
            .get()
            .addOnSuccessListener {
                Log.d("TAG", "onViewCreated: 식물 정보 가져오기 성!공!")
                info_plant_name.text = it["name"] as String? // 식물 이름 재설정
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

        info_pass_btn.setOnClickListener(){
            val activity = activity as MainActivity
            activity.fragmentChange_for_adapter(HomeFragment())
        }
    }
}