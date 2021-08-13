package com.myshopping.ui.activities

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import com.myshopping.R
import com.myshopping.firestore.FirestoreClass
import com.myshopping.models.Address
import com.myshopping.models.AddressType
import com.myshopping.utils.Constants
import kotlinx.android.synthetic.main.activity_add_edit_address.*

class AddEditAddressActivity : BaseActivity() {

    private var mAddress: Address? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit_address)

        setupActionBar()
        setOtherRBFunctionality()

        if (intent.hasExtra(Constants.EXTRA_ADDRESS_ID)) {
            tv_title.text = getString(R.string.title_edit_address)
            val addressId = intent.getStringExtra(Constants.EXTRA_ADDRESS_ID)!!
            FirestoreClass().getAddress(this, addressId)
        }

        btn_submit_address.setOnClickListener {
            if (validInfo()) {
                if (this.mAddress == null) {
                    addAddress()
                } else {
                    editAddress()
                }
            }
        }


    }

    private fun editAddress() {

        val fullName = et_full_name.text.toString().trim { it <= ' ' }
        val phoneNumber = et_phone_number.text.toString().trim { it <= ' ' }
        val address = et_address.text.toString().trim { it <= ' ' }
        val zipCode = et_zip_code.text.toString().trim { it <= ' ' }
        val additionalNote = et_additional_note.text.toString().trim { it <= ' ' }
        val otherDetails = et_other_details.text.toString().trim { it <= ' ' }

        val type = when (rg_type.checkedRadioButtonId) {
            R.id.rb_office -> {
                AddressType.OFFICE.name
            }
            R.id.rb_home -> {
                AddressType.HOME.name
            }
            R.id.rb_other -> {
                AddressType.OTHER.name
            }
            else -> {
                ""
            }
        }

        val hashMap = HashMap<String, Any>()

        mAddress?.let {
            if (fullName != it.name) {
                hashMap[Constants.ADDRESS_NAME] = fullName
            }
            if (phoneNumber != it.mobileNumber) {
                hashMap[Constants.ADDRESS_PHONE_NUMBER] = phoneNumber
            }
            if (address != it.address) {
                hashMap[Constants.ADDRESS_ADDRESS] = address
            }
            if (zipCode != it.zipCode) {
                hashMap[Constants.ADDRESS_ZIPCODE] = zipCode
            }
            if (additionalNote != it.additionalNote) {
                hashMap[Constants.ADDRESS_ADDITIONAL_NOTE] = additionalNote
            }
            if (otherDetails != it.otherDetails) {
                hashMap[Constants.ADDRESS_OTHER_DETAILS] = otherDetails
            }
            if (type != it.type) {
                hashMap[Constants.TYPE] = type
            }
            showProgressDialog(getString(R.string.please_wait))
            FirestoreClass().editAddress(this, it.id, hashMap)
        }
    }

    private fun setOtherRBFunctionality() {

        rg_type.setOnCheckedChangeListener { _, _ ->
            if (rb_other.isChecked) {
                til_other_details.visibility = View.VISIBLE
            } else {
                til_other_details.visibility = View.GONE
            }
        }

    }

    private fun addAddress() {
        val fullName = et_full_name.text.toString().trim { it <= ' ' }
        val phoneNumber = et_phone_number.text.toString().trim { it <= ' ' }
        val address = et_address.text.toString().trim { it <= ' ' }
        val zipCode = et_zip_code.text.toString().trim { it <= ' ' }
        val additionalNote = et_additional_note.text.toString().trim { it <= ' ' }
        val otherDetails = et_other_details.text.toString().trim { it <= ' ' }

        val type = when (rg_type.checkedRadioButtonId) {
            R.id.rb_office -> {
                AddressType.OFFICE.name
            }
            R.id.rb_home -> {
                AddressType.HOME.name
            }
            R.id.rb_other -> {
                AddressType.OTHER.name
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
            et_additional_note.text.toString().trim { it <= ' ' } == AddressType.OTHER.name &&
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
        setResult(RESULT_OK)
        finish()
    }

    fun addressEditedSuccessfully() {
        hideProgressDialog()
        Toast.makeText(
            this,
            getString(R.string.err_your_address_edited_successfully),
            Toast.LENGTH_LONG
        ).show()
        setResult(RESULT_OK)
        finish()
    }

    fun addressAddFailure(e: Exception) {
        Log.e(javaClass.simpleName, e.message, e)
    }

    fun getAddressSuccessfully(firebaseAddress: Address) {
        this.mAddress = firebaseAddress

        et_full_name.setText(firebaseAddress.name)
        et_phone_number.setText(firebaseAddress.mobileNumber)
        et_address.setText(firebaseAddress.address)
        et_zip_code.setText(firebaseAddress.zipCode)
        et_additional_note.setText(firebaseAddress.additionalNote)
        et_other_details.setText(firebaseAddress.otherDetails)

        when (firebaseAddress.type) {
            AddressType.OFFICE.name -> {
                rg_type.check(R.id.rb_office)
            }
            AddressType.HOME.name -> {
                rg_type.check(R.id.rb_home)
            }
            AddressType.OTHER.name -> {
                rg_type.check(R.id.rb_other)
            }
        }
    }

}