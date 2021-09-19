package kr.hs.emirim.w2015.stac_prr.Fragment

import android.animation.ObjectAnimator
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.fragment_plant_info.*
import kr.hs.emirim.w2015.stac_prr.Adapter.GalleryAdapter
import kr.hs.emirim.w2015.stac_prr.CustomDialog
import kr.hs.emirim.w2015.stac_prr.DataClass.JournalData
import kr.hs.emirim.w2015.stac_prr.MainActivity
import kr.hs.emirim.w2015.stac_prr.R
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class PlantInfoFragment : Fragment() {
    private var docId: String? = null
    private var imgUri: String? = null
    private var isFabOpen = false // Fab 버튼 default는 닫혀있음
    private val db = FirebaseFirestore.getInstance()
    private lateinit var galleryAdapter : GalleryAdapter
    private var name: String? = "해당식물"

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
        galleryAdapter = GalleryAdapter(requireContext())
        gallery_recyclerview.adapter = galleryAdapter
        getToday.setTime(Date()) //금일 날짜

        Log.d("TAG,", "onViewCreated: 식물정보 가져온 이미지 uri $imgUri")
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
                val d: Date = timestamp.toDate()
                pdate.time = d
                val diffSec: Long = (getToday.timeInMillis - pdate.timeInMillis) / 1000
                val diffDays = diffSec / (24 * 60 * 60)
                Log.d("TAG", "onViewCreated: 저장된 날짜 데이터 : $d / $diffSec / $diffDays")
                //설정하기
                info_day.text = diffDays.toString() + "일"
                info_date_text.text = sdf.format(d)
                info_water_icon_txt.text = it["water"] as String? + "주기"
                info_c_text.text = it["temperature"] as String?
                info_led_text.text = it["led"] as String?
                info_water_text.text = it["water"] as String?
                info_memo_text.text = it["memo"] as String?
                setGallery()    // 갤러리 설정하기
            }.addOnFailureListener {
                Log.d(it.toString(), "onViewCreated: 식물정보를 가져오기 못함")
            }
        setFabClick() // 수정 삭제 버튼
        info_pass_btn.setOnClickListener() {
            val activity = activity as MainActivity
            activity.fragmentChange_for_adapter(HomeFragment())
        }
    }

    //fab각자 클릭 리스너
    private fun setFabClick() {
        info_fab.setOnClickListener {   //제일밑에꺼 눌렀을때
            toggleFab()
        }
        info_fab_edit.setOnClickListener {      //수정버튼
            // 수정하는 레이아웃으로 트렌젝션 시키기
            val fragment = NewPlantFragment()
            val bundle = Bundle()
            bundle.putBoolean("isEdit", true)
            bundle.putString("docId", docId)
            bundle.putString("imgUri", imgUri)
            fragment.arguments = bundle
            activity?.supportFragmentManager?.beginTransaction()
                ?.replace(R.id.container, fragment)
                ?.commit()
        }

        info_fab_del.setOnClickListener {      // 삭제버튼
            //다이얼로그 띄우기
            val dir = CustomDialog(requireContext())
                .setMessage("$name 을/를 삭제하시겠습니까?")
                .setPositiveBtn("네") {
                    val auth = FirebaseAuth.getInstance().currentUser
                    // 현재 아이템 파이어베이스에서 삭제하기
                    //식물정보 삭제
                    db.collection("plant_info").document(docId!!).delete()
                        .addOnSuccessListener {
                            Log.d("TAG",
                                "setFabClick: 식물정보 - 해당식물 plantInfo에서 삭제함")
                        }
                        .addOnFailureListener {
                            Log.d("TAG",
                                "setFabClick: 식물정보 - 해당식물 plantInfo에서 삭제안됨")
                        }
                    // 캘린더 일정 다 삭제
                    if (auth != null) {
                        db.collection("schedule")
                            .document(auth.uid.toString())
                            .collection("plans")
                            .whereEqualTo("name", name)
                            .get()
                            .addOnSuccessListener {
                                Log.d("", "makeTestItems: 해당 날짜 데이터 가져오기 성공")
                                for (document in it) {
                                    // 각 일정 삭제
                                    db.collection("schedule").document(auth.uid)
                                        .collection("plans")
                                        .document(document.id)
                                        .delete()
                                        .addOnSuccessListener {
                                            Log.d("TAG", "일정 | 파이어스토어 해당 식물관련 일정 모두삭제")
                                        }
                                        .addOnFailureListener { e ->
                                            Log.w("TAG", "일정 | 파이어스토어 해당 식물관련 일지삭제안됨", e)
                                        }
                                }// schedule for end
                            }
                    }

                    //일지 쿼리에서 삭제하기
                    if (auth != null) {
                        db.collection("journals")
                            .document(auth.uid.toString())
                            .collection("journal")
                            .whereEqualTo("name", name)
                            .get()
                            .addOnSuccessListener {
                                //각 아이템 삭제하기
                                for (document in it) {
                                    db.collection("journals").document(auth.uid)
                                        .collection("journal")
                                        .document(document.id)
                                        .delete()
                                        .addOnSuccessListener {
                                            Log.d("TAG", "일지 파이어스토어 해당 식물관련 일지 모두삭제")
                                        }
                                        .addOnFailureListener { e ->
                                            Log.w("TAG", "일지 파이어스토어 해당 식물관련 일지삭제안됨", e)
                                        }
                                }// journal for end
                            }// journal success end
                    }//auth null
                    val pref = context?.getSharedPreferences("pref", Context.MODE_PRIVATE)!!
                    val pcnt = pref.getInt("PlantCnt",0)   // 처음 생성시 식물개수 0
                    with(pref.edit()) {
                        putInt("PlantCnt", pcnt -1)
                        commit()
                    }
                    val activity = activity as MainActivity
                    activity.fragmentChange_for_adapter(HomeFragment())
                }
                .setNegativeBtn("아니오") {}
                .show()
        }
    }

    private fun setGallery(){
        val auth = Firebase.auth
        val imgData = ArrayList<String?>()
        Log.d("TAG", "setGallery: 사진 데이터 가지러 옴 $name")

        db.collection("journals")
            .document(auth.uid.toString())
            .collection("journal")
            .whereEqualTo("name", name)
            .orderBy("date")
            .get()
            .addOnSuccessListener {
                Log.d("", "makeTestItems: 해당 이름 사진 가져오기 성공")
                for (document in it) {
                    if (document["imgUri"] as String? !=null){
                        imgData.add(document["imgUri"] as String?)
                    }
                }
                galleryAdapter.datas.clear()
                galleryAdapter.datas = imgData
                galleryAdapter.notifyDataSetChanged()
                Log.i("갤러리 이미지리소스 주기", galleryAdapter.datas.toString());
            }
    }
    //fab 나오게하는 애니메이션
    private fun toggleFab() {
        Log.d("TAG", "toggleFab: fab 애니메이션 실행")
        // 플로팅 액션 버튼 닫기 - 열려있는 플로팅 버튼 집어넣는 애니메이션
        if (isFabOpen) {
            ObjectAnimator.ofFloat(info_del_linear, "translationY", 0f).apply { start() }
            ObjectAnimator.ofFloat(info_edit_linear, "translationY", 0f).apply { start() }
            ObjectAnimator.ofFloat(info_fab, View.ROTATION, -85f, 0f).apply { start() }
            info_edit_txt.visibility = View.INVISIBLE
            info_del_txt.visibility = View.INVISIBLE
        } else { // 플로팅 액션 버튼 열기 - 닫혀있는 플로팅 버튼 꺼내는 애니메이션
            ObjectAnimator.ofFloat(info_del_linear, "translationY", -360f).apply { start() }
            ObjectAnimator.ofFloat(info_edit_linear, "translationY", -180f).apply { start() }
            ObjectAnimator.ofFloat(info_fab, View.ROTATION, 0f, -85f).apply { start() }
            info_edit_txt.visibility = View.VISIBLE
            info_del_txt.visibility = View.VISIBLE
        }

        isFabOpen = !isFabOpen

    }
}