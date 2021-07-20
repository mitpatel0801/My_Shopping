package com.myshopping.firestore

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.myshopping.models.User
import com.myshopping.ui.activities.LoginActivity
import com.myshopping.ui.activities.RegisterActivity
import com.myshopping.ui.activities.SettingActivity
import com.myshopping.ui.activities.UserProfileActivity
import com.myshopping.utils.Constants
import com.myshopping.utils.UtilsFunctions


class FirestoreClass {


    private val mFireStore = FirebaseFirestore.getInstance()


    fun registerUser(activity: RegisterActivity, userInfo: User) {

        mFireStore.collection(Constants.USERS)
            .document(userInfo.id)
            .set(userInfo, SetOptions.merge())
            .addOnSuccessListener {
                activity.userRegistrationSuccess()
            }
            .addOnFailureListener { e ->
                activity.userRegistrationFailure(e)
            }
    }


    private fun getCurrentUserID(): String {
        val currentUser = FirebaseAuth.getInstance().currentUser
        var currentUserID = ""
        if (currentUser != null) {
            currentUserID = currentUser.uid
        }

        return currentUserID
    }


    fun getUserDetails(activity: Activity) {

        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserID())
            .get()
            .addOnSuccessListener { document ->

                val user = document.toObject(User::class.java)!!

                //Todo(Shared Pref Change to singleTon)
                val sharedPreferences =
                    activity.getSharedPreferences(
                        Constants.MY_SHOPPING,
                        Context.MODE_PRIVATE
                    )

                val editor: SharedPreferences.Editor = sharedPreferences.edit()
                editor.putString(
                    Constants.LOGGED_IN_USERNAME,
                    "${user.firstName} ${user.lastName}"
                )
                editor.apply()

                when (activity) {
                    is LoginActivity -> {
                        activity.userLoggedInSuccess(user)
                    }
                    is SettingActivity -> {
                        activity.userLoggedInSuccess(user)
                    }
                }
            }
            .addOnFailureListener { e ->
                when (activity) {
                    is LoginActivity -> {
                        activity.userLoggedInFailure(e)
                    }
                    is SettingActivity -> {
                        activity.userLoggedInFailure(e)
                    }
                }

            }
    }


    fun updateUserProfileData(activity: Activity, userHashMap: HashMap<String, Any>) {
        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserID())
            .update(userHashMap)
            .addOnSuccessListener {
                when (activity) {
                    is UserProfileActivity -> {
                        activity.userProfileUpdateSuccess()
                    }
                }
            }
            .addOnFailureListener { e ->
                when (activity) {
                    is UserProfileActivity -> {
                        activity.userProfileUpdateFailure(e)
                    }
                }

            }
    }

    fun uploadImageToCloudStorage(activity: Activity, photoUri: Uri) {
        val sRef = FirebaseStorage.getInstance().reference.child(
            "${Constants.USER_PROFILE_IMAGE}${System.currentTimeMillis()}${
                UtilsFunctions.getFileExtensions(
                    activity,
                    photoUri
                )
            }"
        )

        sRef.putFile(photoUri).addOnSuccessListener { taskSnapShot ->

            taskSnapShot.metadata!!.reference!!.downloadUrl.addOnSuccessListener { uri ->
                when (activity) {
                    is UserProfileActivity -> {
                        activity.imageUploadSuccess(uri.toString())
                    }
                }
            }
                .addOnFailureListener { e ->
                    when (activity) {
                        is UserProfileActivity -> {
                            activity.imageUploadFail(e)
                        }
                    }
                }


        }.addOnFailureListener { e ->
            when (activity) {
                is UserProfileActivity -> {

                }
            }
        }
    }
}