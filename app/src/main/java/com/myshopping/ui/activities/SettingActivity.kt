package com.myshopping.ui.activities

import android.os.Bundle
import android.util.Log
import com.myshopping.R
import com.myshopping.firestore.FirestoreClass
import com.myshopping.models.User
import com.myshopping.utils.GlideLoader
import kotlinx.android.synthetic.main.activity_setting.*

class SettingActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)
        setupActionBar()

    }


    override fun onResume() {
        super.onResume()
        getUserData()

    }

    private fun getUserData() {
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getUserDetails(this)
    }

    fun userLoggedInSuccess(user: User) {
        hideProgressDialog()
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


}