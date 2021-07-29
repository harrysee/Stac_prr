package kr.hs.emirim.w2015.stac_prr

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kr.hs.emirim.w2015.stac_prr.Adapter.MainFragmentStatePagerAdapter
import java.util.*


class MainActivity : AppCompatActivity() {
    private lateinit var context: Context

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        configureBottomNavigation()
    }
    private fun configureBottomNavigation(){
        //main_activity의 viewpager를 adapter와 연결     ...프레그먼트매니저 , 프레그먼트갯수
        vp_ac_main_frag_pager.adapter = MainFragmentStatePagerAdapter(supportFragmentManager, 4)

        tl_ac_main_bottom_menu.setupWithViewPager(vp_ac_main_frag_pager)    // main_activity의 뷰를 tablayout과 연결
        
        //activity_main.xml에 있는 탭 레이아웃에 커스텀한 하단탭바 뷰를 적용
        val bottomNaviLayout: View = this.layoutInflater.inflate(R.layout.bottom_navigation_tab, null, false)

        //각 탭에 맞는 버튼들을 일일히 다 연결시켜준다.
        tl_ac_main_bottom_menu.getTabAt(0)!!.customView = bottomNaviLayout.findViewById(R.id.btn_bottom_navi_home_tab) as RelativeLayout
        tl_ac_main_bottom_menu.getTabAt(1)!!.customView = bottomNaviLayout.findViewById(R.id.btn_bottom_navi_calender_tab) as RelativeLayout
        tl_ac_main_bottom_menu.getTabAt(2)!!.customView = bottomNaviLayout.findViewById(R.id.btn_bottom_navi_jour_tab) as RelativeLayout
        tl_ac_main_bottom_menu.getTabAt(3)!!.customView = bottomNaviLayout.findViewById(R.id.btn_bottom_navi_set_tab) as RelativeLayout

    }

}