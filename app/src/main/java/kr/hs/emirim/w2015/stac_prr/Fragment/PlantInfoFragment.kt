package kr.hs.emirim.w2015.stac_prr.Fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_plant_info.*
import kr.hs.emirim.w2015.stac_prr.MainActivity
import kr.hs.emirim.w2015.stac_prr.R
import java.text.SimpleDateFormat

class PlantInfoFragment : Fragment(){
    private var docId : String? = null
    private val db = FirebaseFirestore.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        docId = arguments?.getString("docId")
        val view: View = inflater.inflate(R.layout.fragment_plant_info, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //실행하면 데이터 가져와서 보여주기
        db.collection("plant_info").document(docId!!)
            .get()
            .addOnSuccessListener {
                Log.d("TAG", "onViewCreated: 식물 정보 가져오기 성!공!")
                info_plant_name.text = it["name"] as String // 식물 이름 재설정
                info_plant_spacies.text = it["species"] as String   // 식물 종류 재설정
                val sdf = SimpleDateFormat("yyyy-MM-dd-hh-mm")
                info_date_text.text = sdf.format(it["date"])
                info_c_text.text = it["temperature"] as String
                info_led_text.text = it["led"] as String
                info_water_text.text = it["water"] as String
                info_memo_text.text = it["memo"] as String

            }.addOnFailureListener {
                Log.d(it.toString(), "onViewCreated: 식물정보를 가져오기 못함")
            }

        info_pass_btn.setOnClickListener(){
            val activity = activity as MainActivity
            activity.fragmentChange_for_adapter(HomeFragment())
        }
    }
}