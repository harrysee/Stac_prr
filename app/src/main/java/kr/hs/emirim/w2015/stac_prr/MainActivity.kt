package kr.hs.emirim.w2015.stac_prr

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
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.journal_dialog.*
import kr.hs.emirim.w2015.stac_prr.DataClass.Flowers
import kr.hs.emirim.w2015.stac_prr.Dialog.CustomDialog
import kr.hs.emirim.w2015.stac_prr.Fragment.CalenderFragment
import kr.hs.emirim.w2015.stac_prr.Fragment.HomeFragment
import kr.hs.emirim.w2015.stac_prr.Fragment.JournalFragment
import kr.hs.emirim.w2015.stac_prr.Fragment.SetFragment
import kr.hs.emirim.w2015.stac_prr.Receiver.BroadcastReceiver
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity() {
    private lateinit var context: Context
    private lateinit var bottomNaviLayout: View
    private val br: BroadcastReceiver = BroadcastReceiver()
    private lateinit var pref: SharedPreferences
    private lateinit var flower: SharedPreferences
    private var auth: FirebaseAuth =FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        pref = this.getSharedPreferences("pref", Context.MODE_PRIVATE)
        flower = this.getSharedPreferences("flower", Context.MODE_PRIVATE)
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
    }

    // 등록 삭제
    override fun onPause() {
        super.onPause()
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
        //setflower()
    }

    fun setflower(){
        // 꽃말 넣기
        val flowers = ArrayList<Flowers>()
        flowers.add(Flowers("행운목","#행운이#아닌#약속"))
        flowers.add(Flowers("스파티필름","#세심한#사랑"))
        flowers.add(Flowers("브로멜리아드","#미래#즐기며#만족"))
        flowers.add(Flowers("산세베리아","#관용"))
        flowers.add(Flowers("페페로미아","#행운#사랑"))
        flowers.add(Flowers("스킨답서스","#우아한#심성"))
        flowers.add(Flowers("아레카야자","#승리#부활"))
        flowers.add(Flowers("아디안텀","#애교"))
        flowers.add(Flowers("선인장","#불타는#마음"))
        //추가
        flowers.add(Flowers("목련","#숭고한#정신#고귀한"))
        flowers.add(Flowers("나팔꽃","#기쁨#결속#기쁜소식"))
        flowers.add(Flowers("데이지","#평화#순수#순진"))
        flowers.add(Flowers("라일락","#우애#아름다움"))
        flowers.add(Flowers("매화","#결백#정조#충실"))
        flowers.add(Flowers("이끼","#모성애#고독#쓸쓸한"))
        flowers.add(Flowers("시클라멘","#수줍음#시기#질투"))
        
        flower.edit(){
            for (n in 0..15){
                putString((n.toString()+"n"),flowers[n].name)
                putString((n.toString()+"s"),flowers[n].species)
            }
            commit()
        }
        
        //12시마다 업데이트
        val alarmMgr = this.getSystemService(ALARM_SERVICE) as AlarmManager
        val alarmIntent = Intent(this, BroadcastReceiver::class.java).let { intent ->
            PendingIntent.getBroadcast(this, 0, intent, 0)
        }
        val calendar: Calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, 24)
            set(Calendar.MINUTE,0)
        }

        Log.d("TAG", "setflower: 예약 시간 꽃말 : ${calendar.time}")
        // constants--in this case, AlarmManager.INTERVAL_DAY.
        alarmMgr?.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            alarmIntent
        )
    }
    fun isFirstCheck() {
        // isFirst가 처음 만들어지지않았을때 넣으면 null이다 - null이면 true로 가져오기
        val isCheck = pref.getBoolean("isFirst", true)
        setflower()
        if (isCheck) {
            // 최초실행 후에는 그냥 값을 false로 넣는다.
            with(pref.edit()) {
                putBoolean("isFirst", false)
                putInt("PlantCnt", 0)
                commit()
            }
            Toast.makeText(this, "푸르름에 오신걸 환영합니다", Toast.LENGTH_SHORT).show()
        }
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