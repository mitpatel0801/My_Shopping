package com.myshopping.ui.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.myshopping.R
import kotlinx.android.synthetic.main.activity_add_edit_address.*

class AddEditAddressActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit_address)

        setupActionBar()

    }

    private fun setupActionBar() {

        setSupportActionBar(toolbar_add_edit_address_activity)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)

        toolbar_add_edit_address_activity.setNavigationOnClickListener { onBackPressed() }
    }

}