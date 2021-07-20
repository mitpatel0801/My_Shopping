package com.myshopping.ui.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.myshopping.R
import com.myshopping.firestore.FirestoreClass
import com.myshopping.models.User
import com.myshopping.utils.GlideLoader
import kotlinx.android.synthetic.main.activity_setting.*

class SettingActivity : BaseActivity(), View.OnClickListener {

    private lateinit var mUser: User
    private val TAG = "AAA"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)
        setupActionBar()
        Log.d(TAG, "onCreate: ")

        btn_logout.setOnClickListener(this)
        tv_edit.setOnClickListener(this)
    }


    override fun onResume() {
        super.onResume()
        getUserData()
        Log.d(TAG, "onResume: ")

    }

    private fun getUserData() {
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getUserDetails(this)
    }

    fun userLoggedInSuccess(user: User) {
        hideProgressDialog()
        mUser = user
        GlideLoader(this).loadUserPicture(user.image, iv_user_photo)
        tv_name.text = "${user.firstName} ${user.lastName}"
        tv_email.text = user.email
        tv_mobile_number.text = user.mobile.toString()
        tv_gender.text = user.gender

    }

    fun userLoggedInFailure(e: Exception) {
        hideProgressDialog()
        Log.e(javaClass.simpleName, e.message, e)
    }

    private fun setupActionBar() {

        setSupportActionBar(toolbar_settings_activity)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)

        toolbar_settings_activity.setNavigationOnClickListener { onBackPressed() }
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart: ")
    }

    override fun onRestart() {
        super.onRestart()
        Log.d(TAG, "onRestart: ")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop: ")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause: ")
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_logout -> {
                FirebaseAuth.getInstance().signOut()

                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
            R.id.tv_edit -> {
                TODO("Make Intent for starting User Profile and make changes UserProfileActivity accordingly ")
            }
        }
    }

}