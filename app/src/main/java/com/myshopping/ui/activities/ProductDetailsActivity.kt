package com.myshopping.ui.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import com.myshopping.R
import com.myshopping.firestore.FirestoreClass
import com.myshopping.models.Product
import com.myshopping.utils.Constants
import com.myshopping.utils.GlideLoader
import kotlinx.android.synthetic.main.activity_product_details.*

class ProductDetailsActivity : BaseActivity() {

    private lateinit var productId: String
    private var showMenu: Boolean = false

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

        getProductDetails()
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

        hideProgressDialog()

        GlideLoader(this).loadProductPicture(product.image, iv_product_detail_image)
        tv_product_details_title.text = product.title
        tv_product_details_price.text = getString(R.string.product_price, product.price)
        tv_product_details_description.text = product.description
        tv_product_details_available_quantity.text = product.stock_quantity
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
}