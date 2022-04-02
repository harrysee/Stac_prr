package kr.hs.emirim.w2015.stac_prr.Receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import androidx.core.content.edit
import java.util.*

open class BroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("", "onReceive: 리시버 실행됨")
        val flower: SharedPreferences =
            context?.getSharedPreferences("flower", Context.MODE_PRIVATE)!!
        flower.getString("keyname", "푸르름")
        flower.getString("keytag", "#새로운#시작#준비")

        val rd = Random()
        val num = (rd.nextInt(16)).toString()

        val kname: String = num.toString() + "n"
        val kspece: String = num.toString() + "s"

        flower.edit {
            putString("keyname", flower.getString(kname, "푸르름"))
            putString("keytag", flower.getString(kspece, "#새로운#시작#준비"))
            commit()
        }
    }
}