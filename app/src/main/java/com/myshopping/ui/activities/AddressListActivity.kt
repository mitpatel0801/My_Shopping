package com.myshopping.ui.activities

import EditSwiper
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.myshopping.R
import com.myshopping.firestore.FirestoreClass
import com.myshopping.models.Address
import com.myshopping.ui.adapters.AddressListAdapter
import com.myshopping.utils.Constants.TAG
import kotlinx.android.synthetic.main.activity_address_list.*

class AddressListActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_address_list)
        setupActionBar()

        tv_add_address.setOnClickListener {
            val intent = Intent(this, AddEditAddressActivity::class.java)
            startActivity(intent)
        }


    }

    private fun setupActionBar() {

        setSupportActionBar(toolbar_address_list_activity)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)

        toolbar_address_list_activity.setNavigationOnClickListener { onBackPressed() }
    }

    override fun onResume() {
        super.onResume()

        showProgressDialog(getString(R.string.please_wait))
        FirestoreClass().getAddresses(this)
    }


    fun getAddressesSuccessfully(mutableList: MutableList<Address>) {
        hideProgressDialog()
        if (mutableList.isEmpty()) {
            tv_no_address_found.visibility = View.VISIBLE
            rv_address_list.visibility = View.GONE
        } else {

            tv_no_address_found.visibility = View.GONE
            rv_address_list.visibility = View.VISIBLE

            //Setting the Adapter
            val addressAdapter = AddressListAdapter(this, mutableList)
            rv_address_list.layoutManager = LinearLayoutManager(this)
            rv_address_list.setHasFixedSize(true)
            rv_address_list.adapter = addressAdapter

            //Setting the swiper
            val editSwiper = object : EditSwiper(this) {

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    Log.d(TAG, "onSwiped: ")
                    addressAdapter.editItem(viewHolder.adapterPosition)
                }
            }
            val itemTouchHelper = ItemTouchHelper(editSwiper)
            itemTouchHelper.attachToRecyclerView(rv_address_list)
        }
    }

    fun firebaseFailure(e: Exception) {
        hideProgressDialog()
        Log.e(javaClass.simpleName, e.message, e)
    }


}