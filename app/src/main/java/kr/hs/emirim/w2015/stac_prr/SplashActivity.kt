package kr.hs.emirim.w2015.stac_prr

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.ImageView
import androidx.core.os.postDelayed
import com.bumptech.glide.Glide

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        val splashlogo = findViewById<ImageView>(R.id.imageView)
        Glide.with(this).load(R.raw.prr_logo).into(splashlogo)

        val handler = Handler()
        handler.postDelayed({
            val intent = Intent(getApplication(), MainActivity::class.java)
            startActivity(intent)
            finish()
        },2000)
    }
}