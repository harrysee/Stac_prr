package kr.hs.emirim.w2015.stac_prr.Fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import kotlinx.android.synthetic.main.fragment_home.*
import kr.hs.emirim.w2015.stac_prr.Adapter.FhViewAdapter
import kr.hs.emirim.w2015.stac_prr.AlarmActivity
import kr.hs.emirim.w2015.stac_prr.R

class HomeFragment : Fragment() {
    private val MIN_SCALE = 0.90f // 뷰가 몇퍼센트로 줄어들 것인지
    private val MIN_ALPHA = 0.5f // 어두워지는 정도를 나타낸 듯 하다.

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

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
        val viewAdapter = FhViewAdapter(getimgList(),gettxtList(),activity) // 어댑터 생성
        viewPager.adapter = viewAdapter
        Log.i("어댑터소환", "어댑터 실행완료 ")
        viewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL // 방향을 가로로
        viewPager.setPageTransformer(ZoomOutPageTransformer()) // 애니메이션 적용

        view_dots_indicator.setViewPager2(viewPager) // indicator 설정

        btn_img_alram.setOnClickListener{
            val i = Intent(activity, AlarmActivity::class.java)
            activity?.startActivity(i)
        }

    }


    private fun getimgList(): ArrayList<Int> {
        return arrayListOf<Int>(
            R.drawable.ic_add_img_box,
            R.drawable.test_plant1,
            R.drawable.test_plant2,
            R.drawable.test_plant3,
            R.drawable.test_plant4,
            R.drawable.test_plant5
        )
    }
    private fun gettxtList(): ArrayList<String> {
        return arrayListOf<String>(
            "새 식물 등록하기",
            "식물이름1",
            "식물이름2",
            "식물이름3",
            "식물이름4",
            "식물이름5"
        )
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