package com.myshopping.ui.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.myshoppal.utils.EditSwiper
import com.myshoppal.utils.RemoveSwiper
import com.myshopping.R
import com.myshopping.firestore.FirestoreClass
import com.myshopping.models.Address
import com.myshopping.ui.adapters.AddressListAdapter
import com.myshopping.utils.Constants
import kotlinx.android.synthetic.main.activity_add_edit_address.*
import kotlinx.android.synthetic.main.activity_address_list.*
import kotlinx.android.synthetic.main.activity_cart_list.*

class AddressListActivity : BaseActivity() {

    private var mSelectedAddress: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_address_list)
        setupActionBar()

        showAddress()


        tv_add_address.setOnClickListener {
            val intent = Intent(this, AddEditAddressActivity::class.java)
            startActivityForResult(intent, Constants.ADD_ADDRESS_SELECT_REQUEST_CODE)
        }

        if (intent.hasExtra(Constants.EXTRA_SELECT_ADDRESS)) {
            mSelectedAddress = intent.getBooleanExtra(Constants.EXTRA_SELECT_ADDRESS, false)
        }

        if (mSelectedAddress) {
            tv_title_address_list.text = getString(R.string.title_select_address)
        }
    }

    private fun setupActionBar() {

        setSupportActionBar(toolbar_address_list_activity)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)

        toolbar_address_list_activity.setNavigationOnClickListener { onBackPressed() }
    }


    private fun showAddress() {
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
            val addressAdapter = AddressListAdapter(this, mutableList, mSelectedAddress)
            rv_address_list.layoutManager = LinearLayoutManager(this)
            rv_address_list.setHasFixedSize(true)
            rv_address_list.adapter = addressAdapter
            if (!mSelectedAddress) {
                addSwipeFunctionality(addressAdapter)
            }
        }
    }

    private fun addSwipeFunctionality(addressAdapter: AddressListAdapter) {

        //Setting the swiper
        //Swipe left for edit
        val editSwiper = object : EditSwiper(this) {

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                addressAdapter.editItem(this@AddressListActivity, viewHolder.adapterPosition)
            }
        }

        val itemTouchHelper1 = ItemTouchHelper(editSwiper)
        itemTouchHelper1.attachToRecyclerView(rv_address_list)

        //Swipe right for Delete
        val removeSwipe = object : RemoveSwiper(this) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                addressAdapter.deleteItem(viewHolder.adapterPosition)
            }

        }

        val itemTouchHelper2 = ItemTouchHelper(removeSwipe)
        itemTouchHelper2.attachToRecyclerView(rv_address_list)
    }

    fun firebaseFailure(e: Exception) {
        hideProgressDialog()
        Log.e(javaClass.simpleName, e.message, e)
    }

    fun addressDeletedSuccessfully() {
        showAddress()
        Toast.makeText(
            this,
            getString(R.string.err_your_address_deleted_successfully),
            Toast.LENGTH_LONG
        ).show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            showAddress()
        }
    }


}