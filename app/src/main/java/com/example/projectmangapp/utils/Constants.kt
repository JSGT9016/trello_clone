package com.example.projectmangapp.utils

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import com.example.projectmangapp.activities.CreateBoardActivity
import com.example.projectmangapp.activities.MyProfileActivity

object Constants {

    const val FCM_TOKEN_UPDATED = "fcmTokenUpdated"
    const val FCM_TOKEN = "fcmToken"

    const val FCM_BASE_URL = "https://fcm.googleapis.com/fcm/send"
    const val FCM_AUTHORIZATION = "authorization"
    const val FCM_KEY = "key"
    const val FCM_SERVER_KEY = "AAAAqoyGnBA:APA91bHGDRs5p1HiSBA34QKF58IDdT3BRHIoMurlV17qcYByKYY3MY8BDXp9RQxNyki_7OCm-gQ2f8-BOiwaRatpkhYW_-PRr9WhuUsoO0UT79hnfA3hYnGzZegLRzJL3HDG6blQCf8T"
    const val FCM_KEY_TITLE = "title"
    const val FCM_KEY_MESSAGE= "message"
    const val FCM_KEY_DATA = "data"
    const val FCM_KEY_TO = "to"

    const val BOARDS :String = "Boards"
    const val USERS : String = "Users"

    const val  IMAGE : String = "image"
    const val  NAME : String = "name"
    const val  MOBILE : String = "mobile"

    const val ASSIGNED_TO : String = "assignedTo"

    const val DOCUMENT_ID : String ="documentId"

    const val TASK_LIST : String = "taskList"

    const val BOARD_DETAIL : String = "board_detail"

    const val ID : String  = "id"
    const val EMAIL : String  = "email"

    const val TASK_LIST_ITEM_POSITION:String = "task_list_item_position"
    const val CARD_LIST_ITEM_POSITION:String = "card_list_item_position"
    const val BOARD_MEMBERS_LIST : String= "board_members_list"

    const val SELECT:String = "Seleccionar"
    const val UN_SELECT:String = "Deshacer"

    const val READ_STORAGE_PERMISSION_CODE = 1
    const val PICK_IMAGE_REQUEST_CODE = 2

    const val MYAPP_PREFERENCES = "MyApp_preferences"

    fun showImageChooser(activity:Activity){
        var galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        activity.startActivityForResult(galleryIntent, Constants.PICK_IMAGE_REQUEST_CODE)
    }


    fun getFileExtension(activity:Activity, uri : Uri?):String?{
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(activity.contentResolver.getType(uri!!))
    }



}