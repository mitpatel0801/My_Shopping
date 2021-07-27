package com.myshopping.firestore

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.myshopping.models.Product
import com.myshopping.models.User
import com.myshopping.ui.activities.*
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

    fun getCurrentUserID(): String {
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
                        Constants.MY_SHOPPING_PREF,
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

    fun uploadProduct(activity: Activity,product: Product)
    {
        mFireStore.collection(Constants.PRODUCTS)
            .document()
            .set(product, SetOptions.merge())
            .addOnSuccessListener {
                when (activity) {
                    is AddProductActivity -> {
                        activity.productUploadedSuccessfully()
                    }
                }
            }
            .addOnFailureListener { e ->
                when (activity) {
                    is AddProductActivity -> {
                        activity.productUploadOnFailure(e)
                    }
                }
            }
    }

    fun uploadImageToCloudStorage(activity: Activity, photoUri: Uri,imageType:String) {
        val sRef = FirebaseStorage.getInstance().reference.child(
            "${imageType}${System.currentTimeMillis()}${
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
                    is AddProductActivity->{
                        activity.productImageUploadOnSuccess(uri.toString())
                    }
                }
            }
                .addOnFailureListener { e ->
                    when (activity) {
                        is UserProfileActivity -> {
                            activity.imageUploadFail(e)
                        }
                        is AddProductActivity->{
                            activity.productUploadOnFailure(e)
                        }
                    }
                }


        }.addOnFailureListener { e ->
            when (activity) {
                is UserProfileActivity -> {
                    activity.imageUploadFail(e)
                }
                is AddProductActivity->{
                    activity.productUploadOnFailure(e)
                }
            }
        }
    }
}