package com.example.projectmangapp.activities

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import android.os.Handler
import android.widget.Toast
import androidx.core.content.ContextCompat

import com.example.projectmangapp.R
import kotlinx.android.synthetic.main.dialog_progress.*


open class BaseActivity : AppCompatActivity() {

    private var dobleBackToExitPressedOnce = false

    private lateinit var mProgressDialog : Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    fun showProgressDialog(text: String){
        mProgressDialog = Dialog(this)
        mProgressDialog.setContentView(R.layout.dialog_progress)
        mProgressDialog.progress_text.text = text
        mProgressDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        mProgressDialog.show()
    }

    fun hideProgressDialog(){
        mProgressDialog.dismiss()
    }

    fun getCurrentUserId():String{
        return FirebaseAuth.getInstance().currentUser!!.uid
    }

    fun doubleBackToExit(){
        if(dobleBackToExitPressedOnce){
            super.onBackPressed()
            return
        }
        this.dobleBackToExitPressedOnce = true
        Toast.makeText(this, "Por favor presiona atras nuevamente para salir", Toast.LENGTH_SHORT).show()

        Handler().postDelayed({dobleBackToExitPressedOnce = false}, 2000)
    }

    fun showErrorSnackBar(message:String){
        val snackBar = Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG)
        val snackBarView = snackBar.view
        snackBarView.setBackgroundColor(ContextCompat.getColor(this, R.color.snackbar_error_color))
        snackBar.show()
    }

}