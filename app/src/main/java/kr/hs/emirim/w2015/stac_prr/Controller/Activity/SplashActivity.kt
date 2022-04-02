package kr.hs.emirim.w2015.stac_prr.Controller.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import kr.hs.emirim.w2015.stac_prr.R

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val handler = Handler()
        handler.postDelayed({
            val intent = Intent(getApplication(), MainActivity::class.java)
            startActivity(intent)
            finish()
        },1000)
    }
}