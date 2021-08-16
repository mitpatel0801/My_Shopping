package com.myshopping.ui.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.myshopping.R
import com.myshopping.firestore.FirestoreClass
import com.myshopping.models.CartItem
import com.myshopping.models.Product
import com.myshopping.ui.adapters.CartItemListAdapter
import com.myshopping.utils.Constants
import kotlinx.android.synthetic.main.activity_cart_list.*

class CartListActivity : BaseActivity() {

    private lateinit var mProducts: MutableList<Product>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart_list)
        setupActionBar()

        btn_checkout.setOnClickListener {
            val intent = Intent(this, AddressListActivity::class.java)
            intent.putExtra(Constants.EXTRA_SELECT_ADDRESS, true)
            startActivity(intent)
        }
    }

    private fun setupActionBar() {

        setSupportActionBar(toolbar_cart_list_activity)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)

        toolbar_cart_list_activity.setNavigationOnClickListener { onBackPressed() }
    }

    override fun onResume() {
        super.onResume()
        showProgressDialog(getString(R.string.please_wait))
        FirestoreClass().getProductList(this)
    }

    fun getCartItemsSuccess(items: MutableList<CartItem>) {
        hideProgressDialog()

        for (product in mProducts) {
            for (item in items) {
                if (product.id == item.product_id) {
                    item.stock_quantity = product.stock_quantity

                    if (product.stock_quantity.toInt() == 0) {
                        item.cart_quantity = product.stock_quantity
                    }
                }
            }
        }


        if (items.isNotEmpty()) {
            rv_cart_items_list.visibility = View.VISIBLE
            tv_no_cart_item_found.visibility = View.GONE
            ll_checkout.visibility = View.VISIBLE

            rv_cart_items_list.layoutManager = LinearLayoutManager(this)
            rv_cart_items_list.hasFixedSize()
            val itemListAdapter = CartItemListAdapter(this, items, true)
            rv_cart_items_list.adapter = itemListAdapter

            var subTotal = 0.0
            for (item in items) {
                val itemPrice = item.price.toDouble()
                val itemQuantity = item.cart_quantity.toInt()
                subTotal += (itemPrice * itemQuantity)
            }

            tv_sub_total.text = getString(R.string.product_price, subTotal.toString())
            val shippingCost = subTotal * 0.02
            tv_shipping_charge.text = getString(R.string.product_price, shippingCost.toString())
            tv_total_amount.text =
                getString(R.string.product_price, (shippingCost + subTotal).toString())
        } else {
            rv_cart_items_list.visibility = View.GONE
            tv_no_cart_item_found.visibility = View.VISIBLE
            ll_checkout.visibility = View.GONE
        }
    }

    fun addFailureFireStore(e: Exception) {
        hideProgressDialog()
        Log.e(javaClass.simpleName, e.message, e)
    }

    fun getAllProductSuccess(products: MutableList<Product>) {
        mProducts = products
        getCartList()
    }

    private fun getCartList() {
        FirestoreClass().getCartList(this)
    }

    fun cartItemDeletedSuccessfully() {
        getCartList()
    }

    fun itemUpdatedSuccessfully() {
        hideProgressDialog()
        getCartList()
    }
}