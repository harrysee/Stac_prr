package kr.hs.emirim.w2015.stac_prr

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*
import kr.hs.emirim.w2015.stac_prr.Fragment.CalenderFragment
import kr.hs.emirim.w2015.stac_prr.Fragment.HomeFragment
import kr.hs.emirim.w2015.stac_prr.Fragment.JournalFragment
import kr.hs.emirim.w2015.stac_prr.Fragment.SetFragment
import java.util.jar.Manifest


class MainActivity : AppCompatActivity() {
    private lateinit var context: Context
    private lateinit var bottomNaviLayout: View
    private val br: BroadcastReceiver = BroadcastReceiver()
    private lateinit var pref: SharedPreferences
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        auth = Firebase.auth
        pref = this.getSharedPreferences("pref", Context.MODE_PRIVATE)
        onTabs()

        ActivityCompat.requestPermissions(this,arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),1)
        //최초실행 확인
    }

    fun onTabs() {
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
                else if (position == 3) selected = SetFragment()
                else selected = HomeFragment()

                supportFragmentManager.beginTransaction().replace(R.id.container, selected!!)
                    .commit()
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
    }

    //이 함수를 통해 다른 fragment로 이동한다.생성자가 아닌 불러오는 형식
    fun fragmentChange_for_adapter(frag: Fragment) {
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

    //앱 실행시 로그인 하기
    public override fun onStart() {
        super.onStart()
        isFirstCheck()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        Log.d("TAG", "onStart 유저 아이디 : ${currentUser?.uid} ")
    }

    fun isFirstCheck() {
        // isFirst가 처음 만들어지지않았을때 넣으면 null이다 - null이면 true로 가져오기
        val isCheck = pref.getBoolean("isFirst", true)
        if (isCheck) {
            // 최초실행 후에는 그냥 값을 false로 넣는다.
            with(pref.edit()) {
                putBoolean("isFirst", false)
                commit()
            }
            Toast.makeText(this, "메뚜기월드에 오신걸 환영합니다", Toast.LENGTH_SHORT).show()
            // 첫 실행 시 가입 : signInAnonymously
            auth.signInAnonymously()
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d("TAG", "signInAnonymously:success - 사용자등록 완료")

                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("TAG", "signInAnonymously:failure", task.exception)
                        Toast.makeText(
                            baseContext, "Authentication failed.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

        }
    }

}