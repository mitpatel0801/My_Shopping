package com.myshopping.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CartItem(
    val user_id: String = "",
    val product_id: String = "",
    val title: String = "",
    val price: String = "",
    val image: String = "",
    var cart_quantity: String = "1",
    var stock_quantity: String = "",
    var id: String = ""
) : Parcelable
