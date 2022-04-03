package kr.hs.emirim.w2015.stac_prr.Receiver

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import kr.hs.emirim.w2015.stac_prr.MainActivity
import kr.hs.emirim.w2015.stac_prr.R


class AlarmReceiver : BroadcastReceiver() {
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
            notificationManager?.createNotificationChannel(channel)
        } else builder.setSmallIcon(R.mipmap.ic_launcher) // Oreo 이하에서 mipmap 사용하지 않으면 Couldn't create icon: StatusBarIcon 에러남

        val title = intent.getStringExtra("title")
        val name = intent.getStringExtra("name")
        val content = intent.getStringExtra("content")

        builder.setAutoCancel(true)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setWhen(System.currentTimeMillis())
            .setSmallIcon(R.drawable.ic_alram_icon)
            .setTicker("{Time to watch some cool stuff!}")
            .setContentTitle(title + "알림")
            .setContentText(name+" 일정 : "+content)
            .setContentInfo("INFO")
            .setContentIntent(pendingI)

        if (notificationManager != null) {
            //알람 중복되지않게 카운트
            val push = context?.getSharedPreferences("push", Context.MODE_PRIVATE)!!
            val idcnt = push.getInt("notifyid", 0)
            val isAlarm = push.getBoolean("isAlarm", false)

            // 노티피케이션 동작시킴
            if (isAlarm){
                notificationManager.notify(idcnt, builder.build())
            }
            Log.d("TAG", "onReceive: 알림 : $idcnt")
        }
    }
}