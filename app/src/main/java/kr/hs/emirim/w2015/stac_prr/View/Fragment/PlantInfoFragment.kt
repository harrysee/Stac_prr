package kr.hs.emirim.w2015.stac_prr.View.Fragment

import android.animation.ObjectAnimator
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_plant_info.*
import kr.hs.emirim.w2015.stac_prr.View.Adapter.GalleryAdapter
import kr.hs.emirim.w2015.stac_prr.View.Dialog.CustomDialog
import kr.hs.emirim.w2015.stac_prr.MainActivity
import kr.hs.emirim.w2015.stac_prr.R
import kr.hs.emirim.w2015.stac_prr.ViewModel.PlantViewModel
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class PlantInfoFragment : Fragment() {
    private var docId: String? = null
    private var imgUri: String? = null
    private var isFabOpen = false // Fab 버튼 default는 닫혀있음
    private lateinit var galleryAdapter : GalleryAdapter
    private val model = ViewModelProvider(requireActivity()).get(PlantViewModel::class.java)
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

        // 배경 이미지 설정
        Log.d("TAG,", "onViewCreated: 식물정보 가져온 이미지 uri $imgUri")
        val backgound = view.findViewById<ImageView>(R.id.info_background_img)
        backgound.setImageResource(R.drawable.ic_home_emty_item)
        if (imgUri != null){
            Glide.with(requireContext()) //쓸곳
                .load(imgUri)  //이미지 받아올 경로
                .into(backgound)    // 받아온 이미지를 받을 공간
        }
        
        // 정보 설정
        var waterday = "0일"
        model.getPlant(docId!!).observe(requireActivity(), androidx.lifecycle.Observer {
            // 날짜 설정
            val sdf = SimpleDateFormat("yyyy-MM-dd")
            pdate.time = it.date
            val diffSec: Long = (getToday.timeInMillis - pdate.timeInMillis) / 1000
            val diffDays = diffSec / (24 * 60 * 60)
            Log.d("TAG", "onViewCreated: 저장된 날짜 데이터 : ${it.date} / $diffSec / $diffDays")
            
            // 화면 식물정보 세팅
            name = it.name
            info_plant_name.text = name // 식물 이름 재설정
            info_plant_spacies.text = it.specise as String?  // 식물 종류 재설정]
            info_day.text = diffDays.toString() + "일"
            info_date_text.text = sdf.format(it.date)
            if (it.water != "") waterday = it.water.toString() + "일"
            info_water_icon_txt.text = waterday
            info_c_text.text = it.temperate as String?
            info_led_text.text = it.led as String?
            info_water_text.text = it.water.toString()
            info_memo_text.text = it.memo as String?
            setGallery()    // 갤러리 설정하기
        })
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
                    name?.let { it1 -> model.deleteAll(it1,docId) } // 모두 삭제
                    // 개수 -1
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

    // 사진데이터 넣기
    private fun setGallery(){
        var imgData = ArrayList<String?>()
        Log.d("TAG", "setGallery: 사진 데이터 가지러 옴 $name")

        model.getJournalImgs(name).observe(requireActivity(), androidx.lifecycle.Observer {
            imgData = it
            galleryAdapter.datas.clear()
            galleryAdapter.datas = imgData
            galleryAdapter.notifyDataSetChanged()
            Log.i("갤러리 이미지리소스 주기", galleryAdapter.datas.toString());
        })
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