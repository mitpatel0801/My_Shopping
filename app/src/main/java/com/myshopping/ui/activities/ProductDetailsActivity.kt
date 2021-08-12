package com.myshopping.ui.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.myshopping.R
import com.myshopping.firestore.FirestoreClass
import com.myshopping.models.CartItem
import com.myshopping.models.Product
import com.myshopping.utils.Constants
import com.myshopping.utils.GlideLoader
import kotlinx.android.synthetic.main.activity_product_details.*

class ProductDetailsActivity : BaseActivity(), View.OnClickListener {

    private lateinit var productId: String
    private var showMenu: Boolean = false
    private lateinit var mProduct: Product

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_details)

        setupActionBar()

        if (intent.hasExtra(Constants.EXTRA_FRAGMENT_PRODUCTS)) {
            showMenu = true
        }
        if (intent.hasExtra(Constants.EXTRA_PRODUCT_ID)) {
            productId = intent.getStringExtra(Constants.EXTRA_PRODUCT_ID)!!
        }
        var userId = ""
        if (intent.hasExtra(Constants.EXTRA_USER_ID)) {
            userId = intent.getStringExtra(Constants.EXTRA_USER_ID)!!
        }

        if (userId == FirestoreClass().getCurrentUserID()) {
            btn_add_to_cart.visibility = View.GONE
        } else {
            btn_add_to_cart.visibility = View.VISIBLE
        }

        getProductDetails()


        btn_add_to_cart.setOnClickListener(this)
        btn_go_to_cart.setOnClickListener(this)
    }

    private fun getProductDetails() {
        showProgressDialog(getString(R.string.please_wait))
        FirestoreClass().fetchProductDetails(this, productId)
    }

    private fun setupActionBar() {

        setSupportActionBar(toolbar_product_details_activity)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)

        toolbar_product_details_activity.setNavigationOnClickListener { onBackPressed() }
    }

    fun onProductDetailFetchSuccess(product: Product) {

        mProduct = product
        GlideLoader(this).loadProductPicture(product.image, iv_product_detail_image)
        tv_product_details_title.text = product.title
        tv_product_details_price.text = getString(R.string.product_price, product.price)
        tv_product_details_description.text = product.description
        tv_product_details_available_quantity.text = product.stock_quantity

        if (product.stock_quantity.toInt() == 0) {
            hideProgressDialog()

            btn_add_to_cart.visibility = View.GONE
            tv_product_details_available_quantity.text = getString(R.string.lbl_out_of_stock)
            tv_product_details_available_quantity.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.colorSnackBarError
                )
            )
        } else {
            if (mProduct.user_id == FirestoreClass().getCurrentUserID()) {
                hideProgressDialog()
            } else {
                FirestoreClass().checkIfItemExistInCart(this, mProduct.id)
            }
        }

    }


    fun onProductDetailFetchFailure(e: Exception) {
        hideProgressDialog()
        Log.e(javaClass.simpleName, e.message, e)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        if (showMenu) {
            val inflater: MenuInflater = menuInflater
            inflater.inflate(R.menu.edit_product, menu)
        } else {
            super.onCreateOptionsMenu(menu)
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.edit_product -> {
                val intent = Intent(this, AddProductActivity::class.java)
                intent.putExtra(Constants.EXTRA_PRODUCT_ID, productId)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_add_to_cart -> {
                addToCart()
            }
            R.id.btn_go_to_cart -> {
                startActivity(Intent(this, CartListActivity::class.java))
            }
        }
    }

    private fun addToCart() {
        val cartItem = CartItem(
            FirestoreClass().getCurrentUserID(),
            mProduct.id,
            mProduct.title,
            mProduct.price,
            mProduct.image
        )
        showProgressDialog(getString(R.string.please_wait))
        FirestoreClass().addCartItem(this, cartItem)
    }

    fun addCartFailure(e: Exception) {
        hideProgressDialog()
        Log.e(javaClass.simpleName, e.message, e)
    }

    fun addCartSuccessfully() {
        hideProgressDialog()
        Toast.makeText(
            this,
            getString(R.string.success_message_item_added_to_cart),
            Toast.LENGTH_LONG
        ).show()
        btn_add_to_cart.visibility = View.GONE
        btn_go_to_cart.visibility = View.VISIBLE
    }

    fun productExitstCartSuccess(isExist: Boolean) {
        hideProgressDialog()
        if (isExist) {
            btn_add_to_cart.visibility = View.GONE
            btn_go_to_cart.visibility = View.VISIBLE
        }
    }
}