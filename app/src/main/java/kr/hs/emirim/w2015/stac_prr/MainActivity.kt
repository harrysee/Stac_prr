package kr.hs.emirim.w2015.stac_prr

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*
import kr.hs.emirim.w2015.stac_prr.Fragment.CalenderFragment
import kr.hs.emirim.w2015.stac_prr.Fragment.HomeFragment
import kr.hs.emirim.w2015.stac_prr.Fragment.JournalFragment
import kr.hs.emirim.w2015.stac_prr.Fragment.SetFragment


class MainActivity : AppCompatActivity() {
    private lateinit var context: Context
    private lateinit var bottomNaviLayout : View
    val br : BroadcastReceiver = BroadcastReceiver()

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        onTabs()
    }
    fun onTabs(){
        val view1: View = layoutInflater.inflate(R.layout.bottom_navigation_tab, null)
        view1.findViewById<View>(R.id.icon).setBackgroundResource(R.drawable.selector_tab_home)
        tabs.addTab(tabs.newTab().setCustomView(view1))

        val view2: View = layoutInflater.inflate(R.layout.bottom_navigation_tab, null)
        view2.findViewById<View>(R.id.icon).setBackgroundResource(R.drawable.selector_tab_calender)
        tabs.addTab(tabs.newTab().setCustomView(view2))

        val view3: View = layoutInflater.inflate(R.layout.bottom_navigation_tab, null)
        view3.findViewById<View>(R.id.icon).setBackgroundResource(R.drawable.selector_tab_journal)
        tabs.addTab(tabs.newTab().setCustomView(view3))

        val view4: View = layoutInflater.inflate(R.layout.bottom_navigation_tab, null)
        view4.findViewById<View>(R.id.icon).setBackgroundResource(R.drawable.selector_tab_setting)
        tabs.addTab(tabs.newTab().setCustomView(view4))

        supportFragmentManager.beginTransaction().replace(R.id.container, HomeFragment()).commit()

        tabs.setOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                val position = tab.position
                var selected: Fragment? = null

                if (position == 0) selected = HomeFragment()
                else if (position == 1) selected = CalenderFragment()
                else if (position == 2) selected = JournalFragment()
                else if(position==3) selected = SetFragment()
                else selected= HomeFragment()

                supportFragmentManager.beginTransaction().replace(R.id.container, selected!!).commit()
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
    }

    //이 함수를 통해 다른 fragment로 이동한다.생성자가 아닌 불러오는 형식
    fun fragmentChange_for_adapter(frag: Fragment){
        Log.d("frag", "main으로 옴")
        supportFragmentManager.beginTransaction().replace(R.id.container, frag).commit()
    }
    // 브로드캐스트리시버 필터 추가 & 등록
    override fun onResume() {
        super.onResume()
        var filter = IntentFilter()
        filter.addAction(Intent.ACTION_DATE_CHANGED)
        registerReceiver(br, filter)
    }

    // 등록 삭제
    override fun onPause() {
        super.onPause()
        unregisterReceiver(br)
    }
}