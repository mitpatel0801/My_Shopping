package com.myshopping.utils


object Constants {



    //Shared Pref
    const val MY_SHOPPING_PREF = "MyShoppingPrefs"
    const val LOGGED_IN_USERNAME = "logged_in_username"

    // Intent extra constants.
    const val EXTRA_USER_DETAILS = "extra_user_details"
    const val EXTRA_PRODUCT_ID = "product_id"
    const val EXTRA_FRAGMENT_PRODUCTS = "open_from_products_fragment"
    const val EXTRA_USER_ID = "user_id"


    //Permission
    const val READ_STORAGE_PERMISSION_CODE = 2

    // Image intent result
    const val PICK_IMAGE_REQUEST_CODE = 2

    //FireBase Constants.
    //Users
    const val USERS = "users"
    const val USER_MALE = "Male"
    const val USER_FEMALE = "Female"
    const val USER_MOBILE = "mobile"
    const val USER_GENDER = "gender"
    const val USER_IMAGE = "image"
    const val USER_COMPLETE_PROFILE = "profileCompleted"
    const val USER_FIRST_NAME = "firstName"
    const val USER_LAST_NAME = "lastName"

    //Products
    const val PRODUCTS = "products"
    const val USER_ID: String = "user_id"
    const val PRODUCT_DESCRIPTION = "description"
    const val PRODUCT_ID = "id"
    const val PRODUCT_IMAGE = "image"
    const val PRODUCT_PRICE = "price"
    const val PRODUCT_STOCK_QUANTITY = "stock_quantity"
    const val PRODUCT_TITLE = "title"

    //Cart
    const val CART_ITEMS = "cart_items"
    const val CART_PRODUCT_ID = "product_id"
    const val CART_QUANTITY = "cart_quantity"

    //Address
    const val ADDRESSES: String = "addresses"


    //Firebase Storage prefix
    const val PREFIX_USER_PROFILE_IMAGE = "user_profile_image"
    const val PREFIX_PRODUCT_IMAGE = "product_image"

    //App Testing Constant
    const val TAG = "my_testing"

}