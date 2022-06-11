package kr.hs.emirim.w2015.stac_prr

import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*
import kr.hs.emirim.w2015.stac_prr.View.Dialog.CustomDialog
import kr.hs.emirim.w2015.stac_prr.View.Fragment.CalenderFragment
import kr.hs.emirim.w2015.stac_prr.View.Fragment.HomeFragment
import kr.hs.emirim.w2015.stac_prr.View.Fragment.JournalFragment
import kr.hs.emirim.w2015.stac_prr.View.Fragment.SetFragment
import kr.hs.emirim.w2015.stac_prr.Receiver.BroadcastReceiver
import kr.hs.emirim.w2015.stac_prr.Repository.FlowerRepository
import kr.hs.emirim.w2015.stac_prr.viewModel.MainViewModel
import java.util.*


class MainActivity : AppCompatActivity() {
    private lateinit var context: Context
    private lateinit var bottomNaviLayout: View
    private val br: BroadcastReceiver = BroadcastReceiver()
    private lateinit var pref: SharedPreferences
    private lateinit var flower: SharedPreferences
    private var auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val model by lazy { ViewModelProvider(this).get(MainViewModel::class.java) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        onTabs()

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

                this@MainActivity.supportFragmentManager.beginTransaction().addToBackStack("back").replace(R.id.container, selected!!)
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

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onBackPressed() {
        val dial = CustomDialog(this)
            .setMessage("앱을 종료하시겠습니까?")
            .setPositiveBtn("네"){
                moveTaskToBack(true) // 태스크를 백그라운드로 이동
                finishAndRemoveTask() // 액티비티 종료 + 태스크 리스트에서 지우기
                System.exit(0)
            }
            .setNegativeBtn("아니오"){ }
            .show()
    }

    //앱 실행시 로그인 하기
    public override fun onStart() {
        super.onStart()
        if(auth.currentUser?.uid == null){
            isFirstCheck()
        }
        val currentUser = auth.currentUser
        Log.d("TAG", "onStart 유저 아이디 : ${currentUser?.uid} ")
        // Check if user is signed in (non-null) and update UI accordingly.

    }

    fun isFirstCheck() {
        // isFirst가 처음 만들어지지않았을때 넣으면 null이다 - null이면 true로 가져오기
        pref = this.getSharedPreferences("pref", Context.MODE_PRIVATE)!!
        val alarmMgr = this.getSystemService(ALARM_SERVICE) as AlarmManager
        val alarmIntent = Intent(this, BroadcastReceiver::class.java).let { intent ->
            PendingIntent.getBroadcast(this, 0, intent, 0)
        }
        val isCheck = pref.getBoolean("isFirst", true)
        if (isCheck) {
            val flowerPref = this.getSharedPreferences("flower", Context.MODE_PRIVATE)
            Toast.makeText(this, "푸르름에 오신걸 환영합니다", Toast.LENGTH_SHORT).show()
            auth.signInAnonymously()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d("TAG", "signInAnonymously:success - 사용자등록 완료")
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("TAG", "signInAnonymously:failure", task.exception)
                    }
                }

            model.setFlower(flowerPref)  // 꽃말들 디비넣기
            model.updateFlower(alarmMgr,alarmIntent)    // 정각에 꽃말바뀌는거설정
            // 최초실행 후에는 그냥 값을 false로 넣는다.
            with(pref.edit()) {
                putBoolean("isFirst", false)
                putInt("PlantCnt", 0)
                commit()
            }
        }
    }

}