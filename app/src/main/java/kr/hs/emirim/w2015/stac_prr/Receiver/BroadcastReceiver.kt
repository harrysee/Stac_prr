package kr.hs.emirim.w2015.stac_prr.Receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import kr.hs.emirim.w2015.stac_prr.Fragment.HomeFragment

open class BroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if(Intent.ACTION_DATE_CHANGED == intent!!.action) {
            Log.d("", "onReceive: 실행됨")
            HomeFragment().setflower()
        }
    }
}