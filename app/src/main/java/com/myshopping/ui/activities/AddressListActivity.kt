package com.myshopping.ui.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.myshopping.R
import kotlinx.android.synthetic.main.activity_address_list.*

class AddressListActivity : AppCompatActivity() {
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
}