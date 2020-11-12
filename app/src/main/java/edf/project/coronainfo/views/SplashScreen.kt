package edf.project.coronainfo.views

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import edf.project.coronainfo.MainActivity
import edf.project.coronainfo.R


class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        Handler().postDelayed(Runnable { //setelah loading maka akan langsung berpindah ke home activity
            val home = Intent(this@SplashScreen, MainActivity::class.java)
            startActivity(home)
            finish()
        }, 2000)
    }
}