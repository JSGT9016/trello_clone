package com.example.projectmangapp.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.Button
import com.example.projectmangapp.R
import com.google.firebase.auth.FirebaseAuth


class IntroActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        val user = FirebaseAuth.getInstance().currentUser

        if (user != null) {
            Log.i("a user is logged in: ", user.toString())
        } else {
            Log.i("Username", "there is no user")
        }

        var signUpButton : Button = findViewById(R.id.button_sign_up)
        var signInButton : Button = findViewById(R.id.button_sign_in)

        signUpButton.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }
        signInButton.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
        }


    }

}