package com.myshopping.ui.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.myshopping.R
import com.myshopping.firestore.FirestoreClass
import com.myshopping.models.User
import com.myshopping.utils.Constants
import com.myshopping.utils.GlideLoader
import com.myshopping.utils.UtilsFunctions
import kotlinx.android.synthetic.main.activity_setting.*
import kotlinx.android.synthetic.main.activity_user_profile.*
import kotlinx.android.synthetic.main.activity_user_profile.iv_user_photo
import java.io.IOException


class UserProfileActivity : BaseActivity(), View.OnClickListener {

    private lateinit var mUserDetails: User
    private var mPhotoUri: Uri? = null
    private var mImageUrlLink: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)

        if (intent.hasExtra(Constants.EXTRA_USER_DETAILS)) {
            mUserDetails = intent.getParcelableExtra(Constants.EXTRA_USER_DETAILS)!!
        }


        //First Name
        et_first_name.setText(mUserDetails.firstName)
        //Last Name
        et_last_name.setText(mUserDetails.lastName)
        //Email
        et_email.setText(mUserDetails.email)

        et_email.isEnabled = false
        if (mUserDetails.profileCompleted == 0) {
            //toolbar title
            tv_title_profile.text = resources.getString(R.string.title_complete_profile)

            et_first_name.isEnabled = false
            et_last_name.isEnabled = false
        } else {
            tv_title_profile.text = resources.getString(R.string.title_edit_profile)
            setupActionBar()

            //Mobile Number
            et_mobile_number.setText(mUserDetails.mobile.toString())

            //Image
            GlideLoader(this).loadUserPicture(mUserDetails.image, iv_user_photo)

            //Radio button
            if (mUserDetails.gender == Constants.USER_MALE) {
                rg_gender.check(R.id.rb_male)
            } else {
                rg_gender.check(R.id.rb_female)
            }
        }

        iv_user_photo.setOnClickListener(this@UserProfileActivity)
        btn_submit.setOnClickListener(this@UserProfileActivity)

    }

    private fun setupActionBar() {

        setSupportActionBar(toolbar_user_profile_activity)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)

        toolbar_user_profile_activity.setNavigationOnClickListener { onBackPressed() }
    }


    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {

                R.id.iv_user_photo -> {
                    askPermissionIfRequired()
                }

                R.id.btn_submit -> {
                    if (validateUserProfileDetails()) {

                        showProgressDialog(resources.getString(R.string.please_wait))

                        if (mPhotoUri != null) {
                            FirestoreClass().uploadImageToCloudStorage(
                                this,
                                mPhotoUri!!,
                                Constants.PREFIX_USER_PROFILE_IMAGE
                            )
                        } else {
                            updateUserProfile()
                        }

                    }
                }
            }
        }
    }


    private fun updateUserProfile() {
        val userHashMap = HashMap<String, Any>()


        val firstName = et_first_name.text.toString().trim { it <= ' ' }
        val lastName = et_last_name.text.toString().trim { it <= ' ' }
        val mobileNumber = et_mobile_number.text.toString().trim { it <= ' ' }
        val gender = if (rb_male.isChecked) {
            Constants.USER_MALE
        } else {
            Constants.USER_FEMALE
        }

        if (firstName != mUserDetails.firstName) {
            userHashMap[Constants.USER_FIRST_NAME] = firstName
        }

        if (lastName != mUserDetails.lastName) {
            userHashMap[Constants.USER_LAST_NAME] = lastName
        }

        if (mobileNumber != mUserDetails.mobile.toString()) {
            userHashMap[Constants.USER_MOBILE] = mobileNumber.toLong()
        }

        if (gender != mUserDetails.gender) {
            userHashMap[Constants.USER_GENDER] = gender
        }

        if (mImageUrlLink != null && mPhotoUri != null) {
            userHashMap[Constants.USER_IMAGE] = mImageUrlLink!!
        }

        userHashMap[Constants.USER_COMPLETE_PROFILE] = 1

        showProgressDialog(resources.getString(R.string.please_wait))

        FirestoreClass().updateUserProfileData(
            this@UserProfileActivity,
            userHashMap
        )
    }

    private fun askPermissionIfRequired() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
            == PackageManager.PERMISSION_GRANTED
        ) {
            UtilsFunctions.showImageChooser(this@UserProfileActivity)
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                Constants.READ_STORAGE_PERMISSION_CODE
            )
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constants.READ_STORAGE_PERMISSION_CODE) {

            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                UtilsFunctions.showImageChooser(this@UserProfileActivity)
            } else {
                Toast.makeText(
                    this,
                    resources.getString(R.string.read_storage_permission_denied),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }


    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constants.PICK_IMAGE_REQUEST_CODE) {
                if (data != null) {
                    try {
                        mPhotoUri = data.data!!

                        GlideLoader(this@UserProfileActivity).loadUserPicture(
                            mPhotoUri!!,
                            iv_user_photo
                        )
                    } catch (e: IOException) {
                        e.printStackTrace()
                        Toast.makeText(
                            this@UserProfileActivity,
                            resources.getString(R.string.image_selection_failed),
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                }
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            Log.e("Request Cancelled", "Image selection cancelled")
        }
    }

    private fun validateUserProfileDetails(): Boolean {
        return when {
            TextUtils.isEmpty(et_mobile_number.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_mobile_number), true)
                false
            }
            TextUtils.isEmpty(et_first_name.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_first_name), true)
                false
            }
            TextUtils.isEmpty(et_last_name.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_last_name), true)
                false
            }
            else -> {
                true
            }
        }
    }

    fun userProfileUpdateSuccess() {

        hideProgressDialog()

        Toast.makeText(
            this@UserProfileActivity,
            resources.getString(R.string.msg_profile_update_success),
            Toast.LENGTH_SHORT
        ).show()

        val intent = Intent(this@UserProfileActivity, DashboardActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    fun userProfileUpdateFailure(e: Exception) {
        hideProgressDialog()
        Log.e(
            javaClass.simpleName,
            "Error while updating the user details.",
        )
    }

    fun imageUploadSuccess(imageUrl: String) {
        hideProgressDialog()
        mImageUrlLink = imageUrl
        updateUserProfile()
    }

    fun imageUploadFail(e: Exception) {
        hideProgressDialog()
        Log.e(javaClass.simpleName, e.message, e)
    }
}