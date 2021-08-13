package com.myshopping.ui.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.myshopping.R
import com.myshopping.models.Address
import com.myshopping.utils.Constants
import kotlinx.android.synthetic.main.activity_checkout.*


class CheckoutActivity : AppCompatActivity() {
    private var mAddress: Address? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkout)

        setupActionBar()

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

}