package com.example.projectmangapp.activities

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.WindowManager
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.example.projectmangapp.R
import com.example.projectmangapp.firebase.FirestoreClass
import com.example.projectmangapp.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_sign_up.*

class SignUpActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        setupActionBar()
    }

    fun userRegisteredSuccess(){
        Toast.makeText(
            this,
            "Te has registrado correctamente!",
            Toast.LENGTH_LONG
        ).show()
        hideProgressDialog()

        FirebaseAuth.getInstance().signOut()
        finish()
    }

    private fun setupActionBar(){

        var toolBar : Toolbar= findViewById(R.id.toolbar_sign_up_activity)
        var btnSignUp : Button = findViewById(R.id.btn_sign_up)

        setSupportActionBar(toolBar)

        val actionBar = supportActionBar
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_white_24dp)
        }

        toolBar.setNavigationOnClickListener{
            onBackPressed()
        }
        btnSignUp.setOnClickListener {
            registerUser()
        }
    }

    private fun registerUser(){
        val name     : String =et_name.text.toString().trim{it<=' '}
        val email    : String =et_email.text.toString().trim{it<=' '}
        val password : String =et_password.text.toString().trim{it<=' '}

        if(validateForm(name, email,password)){
            showProgressDialog(resources.getString(R.string.please_wait))
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val firebaseUser: FirebaseUser = task.result!!.user!!
                        val registeredEmail = firebaseUser.email!!
                        Log.d("emailRegistered", registeredEmail)
                        val user = User(firebaseUser.uid,name,registeredEmail)
                        Log.d("UserRegistered", user.email)
                        
                        FirestoreClass().registerUser(this, user)
                    } else {
                        hideProgressDialog()
                        Toast.makeText(
                            this,
                            "Hubo un error, por favor intenta de nuevo",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
        }

    }

    private fun validateForm(name:String, email:String, password: String):Boolean{
        return when{
            TextUtils.isEmpty(name)->{
                et_name.requestFocus()
                showErrorSnackBar("Por favor ingresa un nombre")
                false
            }
            TextUtils.isEmpty(email)->{
                et_email.requestFocus()
                showErrorSnackBar("Por favor ingresa un correo valido")
                false
            }
            TextUtils.isEmpty(password)->{
                et_password.requestFocus()
                showErrorSnackBar("Por favor ingresa una contraseña")
                false
            }
            else->{
                true
            }
        }
    }

}