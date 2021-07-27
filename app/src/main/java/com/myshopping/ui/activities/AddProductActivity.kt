package com.myshopping.ui.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.myshopping.R
import com.myshopping.firestore.FirestoreClass
import com.myshopping.models.Product
import com.myshopping.utils.Constants
import com.myshopping.utils.GlideLoader
import com.myshopping.utils.UtilsFunctions
import kotlinx.android.synthetic.main.activity_add_product.*
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.dialog_progress.*

class AddProductActivity : BaseActivity(), View.OnClickListener {

    private var productImageUri: Uri? = null
    private lateinit var productImageLink: String
    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                UtilsFunctions.showImageChooser(this)
            } else {
                showErrorSnackBar(getString(R.string.read_storage_permission_denied), true)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_product)

        setupActionBar()
    }

    private fun setupActionBar() {

        setSupportActionBar(toolbar_add_product_activity)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)

        toolbar_add_product_activity.setNavigationOnClickListener { onBackPressed() }

        iv_add_update_product.setOnClickListener(this)
        btn_submit.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.iv_add_update_product -> {
                loadImageFromGallery()
            }
            R.id.btn_submit -> {
                if (validateProductDetails()) {
                    uploadImage()
                }
            }
        }
    }

    private fun validateProductDetails(): Boolean {
        return when {
            (productImageUri == null) -> {
                showErrorSnackBar(getString(R.string.err_msg_select_product_image), true)
                false
            }
            TextUtils.isEmpty(et_product_description.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(getString(R.string.err_msg_enter_product_description), true)
                false
            }
            TextUtils.isEmpty(et_product_price.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(getString(R.string.err_msg_enter_product_price), true)
                false
            }
            TextUtils.isEmpty(et_product_quantity.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(getString(R.string.err_msg_enter_product_quantity), true)
                false
            }
            TextUtils.isEmpty(et_product_title.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(getString(R.string.err_msg_enter_product_title), true)
                false
            }
            else -> {
                true
            }
        }
    }

    private fun loadImageFromGallery() {

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            UtilsFunctions.showImageChooser(this)
        } else {
            requestPermissionLauncher.launch(
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {

            when (requestCode) {
                Constants.PICK_IMAGE_REQUEST_CODE -> {
                    productImageUri = data?.data!!
                    GlideLoader(this).loadUserPicture(productImageUri!!, iv_product_image)
                }
            }

        }
    }

    private fun uploadImage() {
        showProgressDialog(getString(R.string.please_wait))
        FirestoreClass().uploadImageToCloudStorage(this, productImageUri!!, Constants.PRODUCT_IMAGE)
    }

    fun productImageUploadOnSuccess(url: String) {
        productImageLink = url
        uploadProductDetails()
    }

    fun productUploadOnFailure(e: Exception) {
        hideProgressDialog()
        Log.e(javaClass.simpleName, e.message, e)
    }

    private fun uploadProductDetails() {
        val sharedPref = getSharedPreferences(Constants.MY_SHOPPING_PREF, MODE_PRIVATE)
        val userName = sharedPref.getString(Constants.LOGGED_IN_USERNAME, "")!!
        val productTitle = et_product_title.text.toString().trim { it <= ' ' }
        val productPrice = et_product_price.text.toString().trim { it <= ' ' }
        val productDescription = et_product_description.text.toString().trim { it <= ' ' }
        val quantity = et_product_quantity.text.toString().trim { it <= ' ' }

        val product = Product(
            FirestoreClass().getCurrentUserID(),
            userName,
            productTitle,
            productPrice,
            productDescription,
            quantity,
            productImageLink
        )
        FirestoreClass().uploadProduct(this, product)
    }

    fun productUploadedSuccessfully() {
        hideProgressDialog()
        Toast.makeText(
            this,
            getString(R.string.product_uploaded_success_message),
            Toast.LENGTH_SHORT
        ).show()
        finish()
    }

}
