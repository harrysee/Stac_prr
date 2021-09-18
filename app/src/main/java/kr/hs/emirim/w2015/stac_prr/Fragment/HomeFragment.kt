package kr.hs.emirim.w2015.stac_prr.Fragment

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_home.*
import kr.hs.emirim.w2015.stac_prr.Adapter.FhViewAdapter
import kr.hs.emirim.w2015.stac_prr.DataClass.HomeData
import kr.hs.emirim.w2015.stac_prr.MainActivity
import kr.hs.emirim.w2015.stac_prr.R
import java.util.*
import kotlin.collections.ArrayList


class HomeFragment : Fragment() {
    private val MIN_SCALE = 0.90f // 뷰가 몇퍼센트로 줄어들 것인지
    private val MIN_ALPHA = 0.5f // 어두워지는 정도를 나타낸 듯 하다.
    val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    val auth = Firebase.auth
    var homedatas: ArrayList<HomeData>? = ArrayList<HomeData>()
    private var alarmMgr: AlarmManager? = null
    private lateinit var pref : SharedPreferences
    private lateinit var flower : SharedPreferences

    private lateinit var alarmIntent: PendingIntent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        pref = context?.getSharedPreferences("pref",Context.MODE_PRIVATE)!!
        flower = context?.getSharedPreferences("flower",Context.MODE_PRIVATE)!!
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val pageMarginPx = resources.getDimensionPixelOffset(R.dimen.pageMargin) // dimen 파일 안에 크기를 정의해두었다.
        val pagerWidth = resources.getDimensionPixelOffset(R.dimen.pageWidth) // dimen 파일이 없으면 생성해야함
        val screenWidth = resources.displayMetrics.widthPixels // 스마트폰의 너비 길이를 가져옴
        val offsetPx = screenWidth - pageMarginPx - pagerWidth

        viewPager.setPageTransformer { page, position ->
            page.translationX = position * -offsetPx
            /*val myOffset = position * - offsetPx

            if (position < -1) {
                page.translationX=-myOffset
            } else if (position <= 1) {
                var scaleFactor = Math.max(0.7f, 1-Math.abs(position-0.14285715f))
                page.translationX=myOffset
                page.scaleY=scaleFactor
            } else {
                page.translationX=myOffset
profileAdapter.setOnItemClickListener(object : ProfileAdapter.OnItemClickListener{
            override fun onItemClick(v: View, data: ProfileData, pos : Int) {
                Intent(this@MainActivity, ProfileDetailActivity::class.java).apply {
                    putExtra("data", data)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }.run { startActivity(this) }
            }

})
            }*/
        }
        /* 여백, 너비에 대한 정의 */
        viewPager.offscreenPageLimit = 1 // 몇 개의 페이지를 미리 로드 해둘것인지
        // 어댑터 넣기
        getDataList()
        if (homedatas != null) {
            Log.d("TAG", "onViewCreated: 식물정보 데이터리스트 null인지 : ${homedatas}")
            val viewAdapter = homedatas?.let { FhViewAdapter(it, activity, requireContext()) }  // 어댑터 생성
            viewPager.adapter = viewAdapter
        }
        Log.i("어댑터소환", "어댑터 실행완료 ")
        viewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL // 방향을 가로로
        viewPager.setPageTransformer(ZoomOutPageTransformer()) // 애니메이션 적용

        view_dots_indicator.setViewPager2(viewPager) // indicator 설정

        home_fab.setOnClickListener {       //플러스 버튼 눌렀을때
            val pcnt = pref.getInt("PlantCnt",0)   // 처음 생성시 식물개수 0
            if (4>pcnt){   // 식물개수 제한. 4개까지
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

        setflower()     // 꽃말 업데이트
    }   // OnViewCreate end

    // 파이어스토어에서 데이터 가져와서 어댑터로 보내기 준비
    private fun getDataList() {
        homedatas.let {
            db.collection("plant_info")
                .whereEqualTo("userId", auth.uid)
                .get()
                .addOnSuccessListener {
                    for (document in it) {
                        Log.i("TAG", "getDataList: 식물 데이터 보여주기 ${document.data}")
                        homedatas?.add(HomeData(document["name"] as String,
                            document["specise"] as String,
                            document["imgUri"] as String,
                            document.id))
                        Log.d("TAG", "getDataList: 홈데이터 담기 : $homedatas")
                        //this.homedatas = homedatas
                    }
                    getHomeData(homedatas)
                    Log.d(it.toString(), "setflower: 식물 데이터 홈에서 불러오기 성공 $homedatas")
                }.addOnFailureListener {
                    Log.d(it.toString(), "setflower: 식물 데이터 홈에서 불러오기 실패")
                }
        }

    }

    // 식물이름 업데이트
    fun setflower(){
        val name = flower.getString("keyname","푸르름")
        val tag = flower.getString("keytag","#성장#시작#푸르른")

        text_flower_today.text = name + "의 꽃말은?"
        text_flower_today_tag.text = tag
    }

    fun getHomeData(homeData: ArrayList<HomeData>?){
        Log.d("TAG", "getData: 홈데이터 가져와보기 : $homeData" )
        homedatas = homeData
        viewPager.adapter?.notifyDataSetChanged()
    }

    /* 공식문서에 있는 코드 긁어온거임 */
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