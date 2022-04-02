package kr.hs.emirim.w2015.stac_prr

import android.app.Application
import com.chibatching.kotpref.Kotpref
import com.chibatching.kotpref.gsonpref.gson
import com.google.gson.Gson

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // kotpref 세팅하기
        Kotpref.init(this)
        Kotpref.gson = Gson()
    }
}