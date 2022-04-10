package kr.hs.emirim.w2015.stac_prr.viewModel

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kr.hs.emirim.w2015.stac_prr.Receiver.BroadcastReceiver
import kr.hs.emirim.w2015.stac_prr.Repository.FlowerRepository
import java.util.*

class MainViewModel :ViewModel() {
    private val br: BroadcastReceiver = BroadcastReceiver()
    private val flowerRep = FlowerRepository

    // 꽃 디비 넣기
    fun setFlower(flowers: SharedPreferences){
        viewModelScope.launch {
            flowerRep.insertFlow(flowers)
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