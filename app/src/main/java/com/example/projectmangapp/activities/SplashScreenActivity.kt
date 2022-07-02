package com.example.projectmangapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import com.example.projectmangapp.R
import com.example.projectmangapp.firebase.FirestoreClass

class SplashScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        Handler().postDelayed({
            var currenUserID = FirestoreClass().getCurrentUserId()
            if(currenUserID!=""){
                startActivity(Intent(this, MainActivity::class.java))
            }
            else {
                startActivity(Intent(this, IntroActivity::class.java))
            }
            finish()
        },3000)

    }
}