package kr.hs.emirim.w2015.stac_prr.viewModel

import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import kr.hs.emirim.w2015.stac_prr.Receiver.BroadcastReceiver
import kr.hs.emirim.w2015.stac_prr.Repository.FlowerRepository
import kr.hs.emirim.w2015.stac_prr.Repository.LoginRepository
import java.util.*

class MainViewModel :ViewModel() {
    private val br: BroadcastReceiver = BroadcastReceiver()
    private lateinit var pref: SharedPreferences
    private var auth: FirebaseAuth = FirebaseAuth.getInstance()
    private var loginRep = LoginRepository
    private val flowerRep = FlowerRepository

    // 가입하기
    fun signUp(context: Activity){
        viewModelScope.launch {
            loginRep.signUp(context)
        }
    }

    // 꽃 디비 넣기
    fun setFlower(context: Activity){
        viewModelScope.launch {
            flowerRep.insertFlow(context)
        }
    }
    
    // 꽃말 업데이트 설정하기
    fun updateFlower(alarmMgr:AlarmManager, alarmIntent:PendingIntent ){
        //12시마다 업데이트
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

}