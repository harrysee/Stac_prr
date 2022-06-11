package kr.hs.emirim.w2015.stac_prr.Receiver

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import kr.hs.emirim.w2015.stac_prr.MainActivity
import kr.hs.emirim.w2015.stac_prr.R

class AlarmReceiver : BroadcastReceiver() {
    @SuppressLint("ResourceAsColor")
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onReceive(context: Context, intent: Intent) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationIntent = Intent(context, MainActivity::class.java)
        notificationIntent.flags = (Intent.FLAG_ACTIVITY_CLEAR_TOP
                or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        val pendingI = PendingIntent.getActivity(context, 0,
            notificationIntent, 0)
        val builder = NotificationCompat.Builder(context, "default")


        //OREO API 26 이상에서는 채널 필요
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setSmallIcon(R.drawable.ic_launcher_foreground) //mipmap 사용시 Oreo 이상에서 시스템 UI 에러남
            val channelName = "일정 알림 채널"
            val description = "해당 식물의 일정 알림을 발송합니다"
            val importance = NotificationManager.IMPORTANCE_HIGH //소리와 알림메시지를 같이 보여줌
            val channel = NotificationChannel("default", channelName, importance)
            channel.description = description
            notificationManager.createNotificationChannel(channel)
        } else builder.setSmallIcon(R.mipmap.ic_launcher) // Oreo 이하에서 mipmap 사용하지 않으면 Couldn't create icon: StatusBarIcon 에러남

        val title = intent.getStringExtra("title")
        val name = intent.getStringExtra("name")
        val content = intent.getStringExtra("content")
        val id = intent.getIntExtra("id",0)
        val img = when(title){
            "급수"-> R.drawable.ic_alarm_icon_water
            "분갈이"-> R.drawable.ic_alarm_icon_blown
            "영양제투여"-> R.drawable.ic_alarm_icon_pen
            "심은날"-> R.drawable.ic_alarm_icon_prr
            "수분"-> R.drawable.ic_alarm_icon_flower
            "수확"-> R.drawable.ic_alarm_icon_orange
            else -> R.drawable.ic_home_emty_item
        }

//        <item>제목</item>
//        <item>급수</item>
//        <item>분갈이</item>
//        <item>영양제투여</item>
//        <item>심은날</item>
//        <item>수분</item>
//        <item>수확</item>

        builder.setAutoCancel(true)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setWhen(System.currentTimeMillis())
            .setSmallIcon(img)
            .setColor(R.color.dot_gray)
            .setDefaults(Notification.DEFAULT_SOUND)
            .setTicker("{Time to watch some cool stuff!}")
            .setContentTitle(name + " : "+ title)
            .setContentText(content)
            .setContentInfo("INFO")
            .setContentIntent(pendingI)

        //알람 중복되지않게 카운트
        val alarmPref = context.getSharedPreferences("alarms", Context.MODE_PRIVATE)!!
        val alarmcheck = context.getSharedPreferences("push", Context.MODE_PRIVATE)!!
        val isAlarm = alarmPref.getBoolean(id.toString(), true) // 모든알림 설정확인
        val check = alarmcheck.getBoolean("isAlarm", true) // 해당알림 설정확인

        // 노티피케이션 동작시킴
        if (isAlarm && check){
            notificationManager.notify(id, builder.build())
        }
        Log.d("TAG", "onReceive: 알림 : $id")
    }
}