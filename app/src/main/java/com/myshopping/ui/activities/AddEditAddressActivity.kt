package com.myshopping.ui.activities

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import com.myshopping.R
import com.myshopping.firestore.FirestoreClass
import com.myshopping.models.AdderssType
import com.myshopping.models.Address
import kotlinx.android.synthetic.main.activity_add_edit_address.*

class AddEditAddressActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit_address)

        setupActionBar()
        setOtherRBFunctionality()

        btn_submit_address.setOnClickListener {
            if (validInfo()) {
                saveAddress()
            }
        }


    }

    private fun setOtherRBFunctionality() {
        rg_type.setOnCheckedChangeListener { _, checkedId ->
            if (rb_other.isChecked) {
                til_other_details.visibility = View.VISIBLE
            } else {
                til_other_details.visibility = View.GONE
            }
        }

    }

    private fun saveAddress() {
        val fullName = et_full_name.text.toString().trim { it <= ' ' }
        val phoneNumber = et_phone_number.text.toString().trim { it <= ' ' }
        val address = et_address.text.toString().trim { it <= ' ' }
        val zipCode = et_zip_code.text.toString().trim { it <= ' ' }
        val additionalNote = et_additional_note.text.toString().trim { it <= ' ' }
        val otherDetails = et_other_details.text.toString().trim { it <= ' ' }

        val type = when (rg_type.checkedRadioButtonId) {
            R.id.rb_office -> {
                AdderssType.OFFICE.name
            }
            R.id.rb_home -> {
                AdderssType.HOME.name
            }
            R.id.rb_other -> {
                AdderssType.OTHER.name
            }
            else -> {
                ""
            }
        }

        val finalAddress = Address(
            FirestoreClass().getCurrentUserID(),
            fullName,
            phoneNumber,
            address,
            zipCode,
            additionalNote,
            type,
            otherDetails
        )
        showProgressDialog(getString(R.string.please_wait))
        FirestoreClass().addAddress(this, finalAddress)

    }

    private fun validInfo(): Boolean {

        return when {
            TextUtils.isEmpty(et_full_name.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(
                    resources.getString(R.string.err_msg_please_enter_full_name),
                    true
                )
                false
            }
            TextUtils.isEmpty(et_phone_number.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(
                    resources.getString(R.string.err_msg_please_enter_phone_number),
                    true
                )
                false
            }
            TextUtils.isEmpty(et_address.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_please_enter_address), true)
                false
            }
            TextUtils.isEmpty(et_zip_code.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_please_enter_zip_code), true)
                false
            }
            TextUtils.isEmpty(et_additional_note.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(
                    resources.getString(R.string.err_msg_please_enter_other_details),
                    true
                )
                false
            }
            et_additional_note.text.toString().trim { it <= ' ' } == AdderssType.OTHER.name &&
                    TextUtils.isEmpty(et_other_details.text.toString().trim { it <= ' ' })
            -> {
                showErrorSnackBar(
                    resources.getString(R.string.err_msg_please_enter_other_details),
                    true
                )
                false
            }
            else -> true
        }

    }

    private fun setupActionBar() {

        setSupportActionBar(toolbar_add_edit_address_activity)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)

        toolbar_add_edit_address_activity.setNavigationOnClickListener { onBackPressed() }
    }

    fun addressAddedSuccessfully() {
        hideProgressDialog()
        Toast.makeText(
            this,
            getString(R.string.err_your_address_added_successfully),
            Toast.LENGTH_LONG
        ).show()
        finish()
    }

    fun addressAddFailure(e: Exception) {
        Log.e(javaClass.simpleName, e.message, e)
    }

}