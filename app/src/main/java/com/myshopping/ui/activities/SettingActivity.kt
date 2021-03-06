package com.myshopping.ui.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.myshopping.R
import com.myshopping.firestore.FirestoreClass
import com.myshopping.models.User
import com.myshopping.utils.Constants
import com.myshopping.utils.GlideLoader
import kotlinx.android.synthetic.main.activity_setting.*

class SettingActivity : BaseActivity(), View.OnClickListener {

    private lateinit var mUser: User


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)
        setupActionBar()

        btn_logout.setOnClickListener(this)
        tv_edit.setOnClickListener(this)
        ll_address.setOnClickListener(this)
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
                val intent = Intent(this, UserProfileActivity::class.java)
                intent.putExtra(Constants.EXTRA_USER_DETAILS, mUser)
                startActivity(intent)
            }
            R.id.ll_address -> {
                val intent = Intent(this, AddressListActivity::class.java)
                startActivity(intent)
            }
        }
    }

}