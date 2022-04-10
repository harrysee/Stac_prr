package kr.hs.emirim.w2015.stac_prr.View.Fragment

import android.app.AlarmManager
import android.content.ContentValues.TAG
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_home.*
import kr.hs.emirim.w2015.stac_prr.View.Adapter.FhViewAdapter
import kr.hs.emirim.w2015.stac_prr.Model.HomeModel
import kr.hs.emirim.w2015.stac_prr.Model.PlantModel
import kr.hs.emirim.w2015.stac_prr.R
import kr.hs.emirim.w2015.stac_prr.viewModel.HomeViewModel
import kotlin.collections.ArrayList


class HomeFragment : Fragment() {
    private val MIN_SCALE = 0.90f // 뷰가 몇퍼센트로 줄어들 것인지
    private val MIN_ALPHA = 0.5f // 어두워지는 정도를 나타낸 듯 하다.
    val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    val auth = Firebase.auth
    var homedatas: ArrayList<HomeModel>? = ArrayList<HomeModel>()
    private lateinit var pref : SharedPreferences
    private lateinit var flower : SharedPreferences
    val model by lazy { ViewModelProvider(requireActivity()).get(HomeViewModel::class.java) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        pref = context?.getSharedPreferences("pref",Context.MODE_PRIVATE)!! // 식물
        flower = context?.getSharedPreferences("flower",Context.MODE_PRIVATE)!! // 꽃
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val pageMarginPx = resources.getDimensionPixelOffset(R.dimen.pageMargin) // dimen 파일 안에 크기를 정의해두었다.
        val pagerWidth = resources.getDimensionPixelOffset(R.dimen.pageWidth) // dimen 파일이 없으면 생성해야함
        val screenWidth = resources.displayMetrics.widthPixels // 스마트폰의 너비 길이를 가져옴
        val offsetPx = screenWidth - pageMarginPx - pagerWidth
        val pCnt = pref.getInt("PlantCnt",0)
        Log.i(TAG, "onViewCreated: 현재 식물 개수 : "+pCnt)
//        with(pref.edit()){
//            this.putInt("PlantCnt",3)
//            commit()
//        }
        viewPager.setPageTransformer { page, position ->
            page.translationX = position * -offsetPx
        }
        viewPager.offscreenPageLimit = 1
        val viewAdapter = homedatas?.let { FhViewAdapter(it, activity, requireContext()) }  // 어댑터 생성

        // 데이터세팅
        model.getAllPlant().observe(viewLifecycleOwner, Observer{ // 뷰모델 데이터가져오기
            Log.d("TAG", "onViewCreated: 식물정보 데이터리스트 null인지 : ${it}")
            // 뷰페이저 어댑터 생성
            viewAdapter?.datas = it
            viewPager.adapter = viewAdapter
            viewAdapter?.notifyDataSetChanged()
            Log.i("어댑터소환", "어댑터 실행완료 ")
            viewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL // 방향을 가로로
            viewPager.setPageTransformer(ZoomOutPageTransformer()) // 애니메이션 적용
            view_dots_indicator.setViewPager2(viewPager) // indicator 설정
        })
        getDataList()

        // 추가버튼
        home_fab.setOnClickListener {
            val pcnt = pref.getInt("PlantCnt",0)   // 처음 생성시 식물개수 0
            if (4 > pcnt){   // 식물개수 제한. 4개 안넘으면 추가시키기
                val fragment = NewPlantFragment()
                val bundle = Bundle()
                bundle.putBoolean("isEdit",false)
                bundle.putString("docId", "docId")
                bundle.putString("imgUri", "imgUri")
                fragment.arguments = bundle
                activity?.supportFragmentManager?.beginTransaction()
                    ?.replace(R.id.container,fragment)
                    ?.commit()
                Log.d("TAG", "onViewCreated: 식물 개수 현재 : $pcnt")
            }else{
                Toast.makeText(requireContext(),"식물은 4개까지만 추가할 수 있습니다",Toast.LENGTH_SHORT).show()
            }
        }
        setflower()
    }   // OnViewCreate end

    // 파이어스토어에서 데이터 가져와서 어댑터로 보내기 준비
    private fun getDataList() {
        Log.i("TAG", "homedatas: "+homedatas?.size)
        // 식물 개수 등록 안내 띄우기
        val pcnt = pref.getInt("PlantCnt", 0)
        if (pcnt <= 0){     // 식물등록 안됐을때 등록시키기
            Toast.makeText(requireContext(),"식물을 등록하세요", Toast.LENGTH_SHORT ).show()
        }

    }

    // 식물이름 업데이트
    fun setflower(){
        val name = flower.getString("keyname","푸르름")
        val tag = flower.getString("keytag","#성장#시작#푸르른")

        text_flower_today.text = name + "의 꽃말은?"
        text_flower_today_tag.text = tag
    }

    /* 공식문서 코드 : 카드뷰 양쪽으로 움직이기 */
    inner class ZoomOutPageTransformer : ViewPager2.PageTransformer {
        override fun transformPage(view: View, position: Float) {
            view.apply {
                val pageWidth = width
                val pageHeight = height
                when {
                    position < -1 -> { // [-Infinity,-1)
                        // This page is way off-screen to the left.
                        //alpha = 0f
                    }
                    position <= 1 -> { // [-1,1]
                        // Modify the default slide transition to shrink the page as well
                        val scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position))
                        // 왼쪽으로 갔을 때 마진 삭제
                        /*val vertMargin = pageHeight * (1 - scaleFactor) / 2
                        val horzMargin = pageWidth * (1 - scaleFactor) / 2
                        translationX = if (position < 0) {
                            horzMargin - vertMargin / 2
                        } else {
                            horzMargin + vertMargin / 2
                        }*/

                        // Scale the page down (between MIN_SCALE and 1)
                        scaleX = scaleFactor
                        scaleY = scaleFactor

                        // 투명도 삭제
                        /*alpha = (MIN_ALPHA +
                                (((scaleFactor - MIN_SCALE) / (1 - MIN_SCALE)) * (1 - MIN_ALPHA)))*/
                    }
                    else -> { // (1,+Infinity]
                        // 투명도 삭제
                        //alpha = 0f
                    }
                }
            }
        }
    } // 공식문서 코드 끝

}