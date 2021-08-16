package com.myshopping.ui.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.myshopping.R
import com.myshopping.firestore.FirestoreClass
import com.myshopping.models.Address
import com.myshopping.models.CartItem
import com.myshopping.models.Order
import com.myshopping.models.Product
import com.myshopping.ui.adapters.CartItemListAdapter
import com.myshopping.utils.Constants
import kotlinx.android.synthetic.main.activity_checkout.*


class CheckoutActivity : BaseActivity() {
    private var mAddress: Address? = null
    private lateinit var mProducts: List<Product>
    private lateinit var mCartItems: List<CartItem>
    private var mSubtotal: Double = 0.0
    private var mShippingCharge: Double = 0.0
    private var mTotal: Double = 0.0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkout)

        setupActionBar()
        setAddress()
        getProductList()

        btn_place_order.setOnClickListener { placeOrder() }

    }

    private fun setAddress() {

        if (intent.hasExtra(Constants.EXTRA_SELECT_ADDRESS)) {
            mAddress = intent.getParcelableExtra(Constants.EXTRA_SELECT_ADDRESS)
        }

        mAddress?.run {
            tv_checkout_address_type.text = type
            tv_checkout_full_name.text = name
            tv_checkout_address.text = address
            tv_checkout_additional_note.text = additionalNote
            tv_checkout_other_details.text = otherDetails
            tv_checkout_mobile_number.text = mobileNumber
        }
    }

    private fun setupActionBar() {
        setSupportActionBar(toolbar_checkout_activity)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        toolbar_checkout_activity.setNavigationOnClickListener { onBackPressed() }
    }

    private fun getProductList() {
        showProgressDialog(getString(R.string.please_wait))
        FirestoreClass().getProductList(this)
    }


    fun getAllProductSuccess(products: MutableList<Product>) {
        mProducts = products
        getCartItemsList()
    }

    private fun getCartItemsList() {
        FirestoreClass().getCartList(this)
    }

    fun getCartItemsSuccessfully(items: MutableList<CartItem>) {
        hideProgressDialog()
        for (product in mProducts) {
            for (cartItem in items) {
                if (product.id == cartItem.id) {
                    cartItem.stock_quantity = product.stock_quantity
                }
            }
        }

        mCartItems = items
        rv_cart_list_items.layoutManager = LinearLayoutManager(this)
        rv_cart_list_items.setHasFixedSize(true)
        val itemListAdapter = CartItemListAdapter(this, items, false)
        rv_cart_list_items.adapter = itemListAdapter

        var subTotal = 0.0

        for (cartItem in mCartItems) {
            subTotal += (cartItem.price.toDouble() * cartItem.cart_quantity.toInt())
        }

        mSubtotal = subTotal
        mShippingCharge = (subTotal * 0.02)
        tv_checkout_sub_total.text = getString(R.string.product_price, mSubtotal.toString())
        tv_checkout_shipping_charge.text =
            getString(R.string.product_price, mShippingCharge.toString())


        if (subTotal > 0) {
            mTotal = subTotal + mShippingCharge
            ll_checkout_place_order.visibility = View.VISIBLE
            tv_checkout_total_amount.text =
                getString(R.string.product_price, mTotal.toString())
        } else {
            ll_checkout_place_order.visibility = View.GONE

        }
    }


    fun addFailureFireStore(e: Exception) {
        hideProgressDialog()
        Log.e(javaClass.simpleName, e.message, e)
    }


    private fun placeOrder() {
        showProgressDialog(getString(R.string.please_wait))
        if (mAddress != null) {
            val order = Order(
                FirestoreClass().getCurrentUserID(),
                mCartItems,
                mAddress!!,
                "My Order ${System.currentTimeMillis()}",
                mCartItems[0].image,
                mSubtotal.toString(),
                mShippingCharge.toString(),
                mTotal.toString()
            )
            FirestoreClass().placeOrder(this, order)
        }

    }

    fun orderPlacedSuccessfully() {
        hideProgressDialog()
        Toast.makeText(this, "Your order was placed Successfully", Toast.LENGTH_LONG)
            .show()
        val intent = Intent(this, DashboardActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }


}