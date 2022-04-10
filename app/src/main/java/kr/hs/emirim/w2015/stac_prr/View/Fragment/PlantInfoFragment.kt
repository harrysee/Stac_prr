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
import kotlinx.android.synthetic.main.fragment_plant_info.*
import kr.hs.emirim.w2015.stac_prr.View.Adapter.GalleryAdapter
import kr.hs.emirim.w2015.stac_prr.View.Dialog.CustomDialog
import kr.hs.emirim.w2015.stac_prr.MainActivity
import kr.hs.emirim.w2015.stac_prr.R
import kr.hs.emirim.w2015.stac_prr.viewModel.PlantViewModel
import kr.hs.emirim.w2015.stac_prr.databinding.FragmentPlantInfoBinding
import java.text.SimpleDateFormat
import java.util.*

class PlantInfoFragment : Fragment() {
    private var docId: String? = null
    private var imgUri: String? = null
    private var _binding: FragmentPlantInfoBinding? = null//<layout></layout>으로 감싼 것만 바인딩가능
    private val binding get() = _binding!!
    private var isFabOpen = false // Fab 버튼 default는 닫혀있음
    private lateinit var galleryAdapter : GalleryAdapter
    private val model by lazy{ ViewModelProvider(requireActivity()).get(PlantViewModel::class.java)}
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
        _binding = FragmentPlantInfoBinding.inflate(inflater,container,false)
        binding.mainViewModel = model
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //실행하면 데이터 가져와서 보여주기
        val getToday: Calendar = Calendar.getInstance()
        val pdate: Calendar = Calendar.getInstance()
        galleryAdapter = GalleryAdapter(requireContext())
        binding.galleryRecyclerview.adapter = galleryAdapter
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
            Log.i("TAG", "onViewCreated: 가져온 식물 정보 "+it)
            val sdf = SimpleDateFormat("yyyy-MM-dd")

            // 화면 식물정보 세팅
            name = it.name?:""
            if (it.water != "") waterday = (it.water?:"") + "일"
            binding.infoDay.text = getDate(it.date) + "일"
            binding.infoDateText.text = sdf.format(it.date)?:""
            binding.infoWaterIconTxt.text = waterday?:""
            binding.infoPlantName.text = name?:"" // 식물 이름 재설정
            binding.infoPlantSpacies.text = it.specise?:"" as String?  // 식물 종류 재설정]
            binding.infoCText.text = it.temperate?:"" as String?
            binding.infoLedText.text = it.led?:"" as String?
            binding.infoWaterText.text = it.water?:"".toString()
            binding.infoMemoText.text = it.memo?:"" as String?
            setGallery()    // 갤러리 설정하기
        })

        binding.infoPassBtn.setOnClickListener{
            val activity = activity as MainActivity
            activity.fragmentChange_for_adapter(HomeFragment())
        }
        setFabClick() // 수정 삭제 버튼
    }
    fun getDate(d:Date): String {
        val pdate: Calendar = Calendar.getInstance()
        val getToday: Calendar = Calendar.getInstance()
        pdate.time = d
        val diffSec: Long = (getToday.timeInMillis - pdate.timeInMillis) / 1000
        val diffDays = diffSec / (24 * 60 * 60)
        return diffDays.toString()
    }

    //fab각자 클릭 리스너
    private fun setFabClick() {
        binding.infoFab.setOnClickListener {   //제일밑에꺼 눌렀을때
            toggleFab()
        }
        binding.infoFabEdit.setOnClickListener {      //수정버튼
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

        binding.infoFabDel.setOnClickListener {      // 삭제버튼
            //다이얼로그 띄우기
            val dir = CustomDialog(requireContext())
                .setMessage("$name 을/를 삭제하시겠습니까?")
                .setPositiveBtn("네") {
                    name?.let { it1 -> model.deleteAll(it1,docId,imgUri) } // 모두 삭제
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
        Log.d("TAG", "setGallery: 사진 데이터 가지러 옴 $name")
        val activity = requireActivity()
        name?.let {
            model.getJournalImgs(it).observe(requireActivity(),  androidx.lifecycle.Observer{
                it?.let {
                    galleryAdapter = GalleryAdapter(activity)
                    galleryAdapter.datas = it
                    binding.galleryRecyclerview.adapter = galleryAdapter
                    Log.i("갤러리 이미지리소스 주기", galleryAdapter.datas.toString() + " / " + it);
                }
            })
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
            binding.infoEditTxt.visibility = View.INVISIBLE
            binding.infoDelTxt.visibility = View.INVISIBLE
        } else { // 플로팅 액션 버튼 열기 - 닫혀있는 플로팅 버튼 꺼내는 애니메이션
            ObjectAnimator.ofFloat(info_del_linear, "translationY", -360f).apply { start() }
            ObjectAnimator.ofFloat(info_edit_linear, "translationY", -180f).apply { start() }
            ObjectAnimator.ofFloat(info_fab, View.ROTATION, 0f, -85f).apply { start() }
            binding.infoEditTxt.visibility = View.VISIBLE
            binding.infoDelTxt.visibility = View.VISIBLE
        }
        isFabOpen = !isFabOpen
    }
}