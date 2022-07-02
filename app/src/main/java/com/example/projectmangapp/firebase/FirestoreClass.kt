package com.example.projectmangapp.firebase

import android.app.Activity
import android.util.Log
import android.widget.Toast
import com.example.projectmangapp.activities.*
import com.example.projectmangapp.models.Board
import com.example.projectmangapp.models.User
import com.example.projectmangapp.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class FirestoreClass {

    private val mFireStore = FirebaseFirestore.getInstance()

    fun registerUser(activity: SignUpActivity, userInfo:User){
        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserId()).set(userInfo, SetOptions.merge())
            .addOnSuccessListener {
                activity.userRegisteredSuccess()
            }.addOnFailureListener {
                e->
                Log.e(activity.javaClass.simpleName, "Error writing document")
            }
    }

    fun updateUserProfileData(activity:Activity, userHashMap : HashMap<String, Any>){
        mFireStore.collection(Constants.USERS).document(getCurrentUserId())
            .update(userHashMap)
            .addOnSuccessListener {
                Log.i(activity.javaClass.simpleName, "Profile data updated successfully")
                Toast.makeText(activity, "La información se ha actualizado exitosamente!", Toast.LENGTH_LONG)
                when(activity) {
                    is MainActivity -> {
                        activity.tokenUpdateSuccess()
                    }
                    is MyProfileActivity -> {
                        activity.profileUpdateSuccess()
                    }
                }
            }.addOnFailureListener {
                e ->
                when(activity) {
                    is MainActivity -> {
                        activity.hideProgressDialog()
                    }
                    is MyProfileActivity -> {
                        activity.hideProgressDialog()
                    }
                }
                Log.e(activity.javaClass.simpleName, "Error ", e)
                Toast.makeText(activity,"Error, no se puedo actualizar la información",Toast.LENGTH_LONG)
            }
    }

    fun loadUserData(activity:Activity, readBoardsList:Boolean = false){
        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserId()).get()
            .addOnSuccessListener {document->
                val loggedInUser = document.toObject(User::class.java)!!
                when(activity){
                    is SignInActivity ->{  activity.signInSuccess(loggedInUser) }
                    is MainActivity -> { activity.updateNavigationUserDetails(loggedInUser, readBoardsList)}
                    is MyProfileActivity -> { activity.setUserDataInUI(loggedInUser)}
                }
            }.addOnFailureListener {
                    e->
                when(activity){
                    is SignInActivity ->{  activity.hideProgressDialog() }
                    is MainActivity -> { activity.hideProgressDialog() }
                }
                   Log.e("SignInUser", "Error writing document")
            }
    }

    fun getCurrentUserId():String {

        var currentUser = FirebaseAuth.getInstance().currentUser
        var currentUserID = ""

        if (currentUser != null) {
            return currentUser.uid
        }
        return currentUserID
    }

    fun createBoard(activity:CreateBoardActivity, board:Board){
        mFireStore.collection(Constants.BOARDS).document().set(board, SetOptions.merge())
            .addOnSuccessListener {
                Log.i(activity.javaClass.simpleName, "Board created successfully")
                Toast.makeText(activity, "Se ha creado el tablero exitosamente!", Toast.LENGTH_LONG).show()
                activity.boardCreatedSuccessfully()
            }.addOnFailureListener {
                exception ->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Board created UNsuccessfully", exception)
            }
    }

    fun getBoardsList(activity:MainActivity){
        mFireStore.collection(Constants.BOARDS)
            .whereArrayContains(Constants.ASSIGNED_TO, getCurrentUserId())
            .get()
            .addOnSuccessListener {
                document ->
                Log.i(activity.javaClass.simpleName, document.documents.toString())
                val boardsList : ArrayList<Board> = ArrayList()
                for(i in document.documents){
                    val board = i.toObject(Board::class.java)!!
                    board.documentId = i.id
                    boardsList.add(board)
                }

                activity.populateBoardsListToUI(boardsList)
            }.addOnFailureListener {
                e->
                Log.e(activity.javaClass.simpleName, "Error cargando los tableros")
            }
    }

    fun addUpdateTaskList(activity:Activity, board:Board){
        val taskListHashMap = HashMap<String, Any>()
        taskListHashMap[Constants.TASK_LIST] = board.taskList

        mFireStore.collection(Constants.BOARDS)
            .document(board.documentId)
            .update(taskListHashMap)
            .addOnSuccessListener {
                Log.e(activity.javaClass.simpleName, "Task List updated successfully")
                if(activity is TaskListActivity) {
                    activity.addUpdateTaskListSuccess()
                }else if(activity is CardDetailsActivity){
                    activity.addUpdateTaskListSuccess()
                }
            }.addOnFailureListener {
                exception ->
                if(activity is TaskListActivity) {
                    activity.hideProgressDialog()
                }else if(activity is CardDetailsActivity){
                    activity.hideProgressDialog()
                }
                Log.e(activity.javaClass.simpleName, "Task List updated Unsuccessfully", exception)
            }
    }

    fun updateUserBoardData(activity:MyProfileActivity, boardsHashMap : HashMap<String, Any>){
        mFireStore.collection(Constants.BOARDS).document(getCurrentUserId())
            .update(boardsHashMap)
            .addOnSuccessListener {
                Log.i(activity.javaClass.simpleName, "Board data updated successfully")
                Toast.makeText(activity, "La información del tablero se ha actualizado exitosamente!", Toast.LENGTH_LONG)
                activity.profileUpdateSuccess()
            }.addOnFailureListener {
                    e ->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error ", e)
                Toast.makeText(activity,"Error, no se puedo actualizar la información",Toast.LENGTH_LONG)
            }
    }

    fun getBoardsDetails(activity: TaskListActivity, boardDocumentId: String) {
        mFireStore.collection(Constants.BOARDS)
            .document(boardDocumentId)
            .get()
            .addOnSuccessListener {
                    document ->
                Log.i(activity.javaClass.simpleName, document.toString())
                val board = document.toObject(Board::class.java)!!
                board.documentId = document.id
                activity.boardDetails(board)

            }.addOnFailureListener {
                    e->
                Log.e(activity.javaClass.simpleName, "Error cargando los tableros")
            }
    }

    fun getAssignedMembersListDetails(activity: Activity, assignedTo:ArrayList<String>){
        mFireStore.collection(Constants.USERS).whereIn(Constants.ID, assignedTo).get().addOnSuccessListener{
            document ->
            Log.e(activity.javaClass.simpleName, document.documents.toString())

            val usersList :ArrayList<User> = ArrayList()
            for (i in document.documents){
                val user = i.toObject(User::class.java)!!
                usersList.add(user)
            }

            if(activity is MembersActivity) {
                activity.setUpMembersList(usersList)
            }else if(activity is TaskListActivity) {
                activity.boardMembersDetailsList(usersList)
            }

        }.addOnFailureListener {
            exception ->

            if(activity is MembersActivity ) {
                activity.hideProgressDialog()
            }else if(activity is TaskListActivity) {
                activity.hideProgressDialog()
            }

            Log.e(activity.javaClass.simpleName, "Error loading board's members list", exception)
        }

    }

    fun getMemberDetails(activity: MembersActivity, email:String){
        mFireStore.collection(Constants.USERS)
            .whereEqualTo(Constants.EMAIL,email).get()
            .addOnSuccessListener {
                document ->
                if(document.documents.size>0){
                    val user =document.documents[0].toObject(User::class.java)!!
                    activity.memberDetails(user)
                }else{
                    activity.hideProgressDialog()
                    activity.showErrorSnackBar("Ningun usuario encontrado con ese E-mail...")
                }
            }.addOnFailureListener {  e->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while trying to get user details",
                    e
                )
            }
    }

    fun assignMemberToBoard(activity: MembersActivity, board:Board, user:User){
        val assignedToHashMap = HashMap<String,Any>()
        assignedToHashMap[Constants.ASSIGNED_TO] = board.assignedTo
        mFireStore.collection(Constants.BOARDS).document(board.documentId)
            .update(assignedToHashMap).addOnSuccessListener {
                activity.memberAssignedSuccess(user)
            }.addOnFailureListener {
                e->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error assigning user into board",  e)
            }
    }

}