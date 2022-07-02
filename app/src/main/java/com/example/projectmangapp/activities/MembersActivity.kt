package com.example.projectmangapp.activities

import android.app.Activity
import android.app.Dialog
import android.os.AsyncTask
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.projectmangapp.R
import com.example.projectmangapp.adapters.MemberListItemAdapter
import com.example.projectmangapp.firebase.FirestoreClass
import com.example.projectmangapp.models.Board
import com.example.projectmangapp.models.User
import com.example.projectmangapp.utils.Constants
import kotlinx.android.synthetic.main.activity_members.*
import kotlinx.android.synthetic.main.dialog_search_member.*
import org.json.JSONObject
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.IOException
import java.io.InputStreamReader
import java.lang.Exception
import java.lang.StringBuilder
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.URL

class MembersActivity : BaseActivity() {

    private lateinit var mBoardDetails : Board
    private lateinit var mAssignedMembersList : ArrayList<User>
    private var anyChangesMade : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_members)
        if(intent.hasExtra(Constants.BOARD_DETAIL)){
            mBoardDetails = intent.getParcelableExtra<Board>(Constants.BOARD_DETAIL)!!
            showProgressDialog(resources.getString(R.string.please_wait))
            FirestoreClass().getAssignedMembersListDetails(this, mBoardDetails.assignedTo)
        }
        setUpActionBar()
    }

    fun setUpMembersList(list:ArrayList<User>){
        mAssignedMembersList = list
        hideProgressDialog()
        rv_members_list.layoutManager = LinearLayoutManager(this)
        rv_members_list.setHasFixedSize(true)
        val adapter = MemberListItemAdapter(this, list)
        rv_members_list.adapter = adapter
    }

    fun memberDetails(user: User){
        mBoardDetails.assignedTo.add(user.id)
        FirestoreClass().assignMemberToBoard(this, mBoardDetails, user)

    }

    private fun setUpActionBar(){
        setSupportActionBar(toolbar_members_activity)
        val actionBar = supportActionBar
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_white_24dp)
            actionBar.title= getString(R.string.members)
        }

        toolbar_members_activity.setNavigationOnClickListener{
            onBackPressed()
        }
    }

    fun memberAssignedSuccess(user:User){
        hideProgressDialog()
        mAssignedMembersList.add(user)

        anyChangesMade = true
        setUpMembersList(mAssignedMembersList)

        SendNotificationToUserASyncTask(mBoardDetails.name, user.fcmToken ).execute()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_add_member, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_add_member->{
                dialogSearchMember()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun dialogSearchMember(){
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_search_member)
        dialog.tv_add.setOnClickListener {
            val email = dialog.et_email_search_member.text.toString()

            if(email.isNotEmpty()){
                dialog.dismiss()
                showProgressDialog(resources.getString(R.string.please_wait))
                FirestoreClass().getMemberDetails(this, email)
            }
            else{
                Toast.makeText(this@MembersActivity, "Por favor ingresa un correo electronico...", Toast.LENGTH_LONG).show()
            }
        }
        dialog.tv_cancel.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    override fun onBackPressed() {
        if(anyChangesMade){
            setResult(Activity.RESULT_OK)
        }
        super.onBackPressed()
    }

    private inner class SendNotificationToUserASyncTask(val board : String, val token:String) : AsyncTask<Any, Void, String>(){

        override fun onPreExecute() {
            super.onPreExecute()
            showProgressDialog(resources.getString(R.string.please_wait))
        }

        override fun doInBackground(vararg params: Any?): String {
            var result:String
            var connection : HttpURLConnection?=null
            try{
                val url = URL(Constants.FCM_BASE_URL)
                connection = url.openConnection() as HttpURLConnection
                connection.doOutput = true
                connection.doInput = true
                connection.instanceFollowRedirects = false
                connection.requestMethod = "POST"

                connection.setRequestProperty("Content-Type", "application/json")
                connection.setRequestProperty("charset","uft-8")
                connection.setRequestProperty("Accept","application/json")

                connection.setRequestProperty(
                    Constants.FCM_AUTHORIZATION, "${Constants.FCM_KEY}=${Constants.FCM_SERVER_KEY}"
                )

                connection.useCaches = false
                val wr = DataOutputStream(connection.outputStream)
                var jsonRequest = JSONObject()
                var dataObject = JSONObject()

                dataObject.put(Constants.FCM_KEY_TITLE, "Asignado al tablero $board")
                dataObject.put(Constants.FCM_KEY_MESSAGE, "Has sido asignado al tablero creado por ${mAssignedMembersList[0].name}")

                jsonRequest.put(Constants.FCM_KEY_DATA, dataObject)
                jsonRequest.put(Constants.FCM_KEY_TO, token)

                wr.writeBytes(jsonRequest.toString())
                wr.flush()
                wr.close()

                val httpResult : Int = connection.responseCode

                if(httpResult == HttpURLConnection.HTTP_OK){
                    val inputStream = connection.inputStream
                    val reader = BufferedReader(InputStreamReader(inputStream))
                    val sb = StringBuilder()
                    var line : String?

                    try {
                        while(reader.readLine().also { line = it }!=null){
                            sb.append(line+"\n")
                        }
                     }
                    catch(e:IOException){
                        e.printStackTrace()
                     }
                    finally{
                    try{
                        inputStream.close()
                    }
                    catch(e:IOException){
                        e.printStackTrace()
                    }
                }
                    result = sb.toString()
            }else{
                result = connection.responseMessage
            }


        }catch(e:SocketTimeoutException){
            result="Connection timeout"

        }catch (e:Exception){
            result="Error : " + e.stackTrace
        }finally {
            connection?.disconnect()
        }


            return result
        }
    }
}