package kr.hs.emirim.w2015.stac_prr

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class AlarmActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarm)
    }
    fun back_home(v: View){
        finish()
    }
}