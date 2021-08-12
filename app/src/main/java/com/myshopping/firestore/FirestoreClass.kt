package com.myshopping.firestore

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.myshopping.models.Address
import com.myshopping.models.CartItem
import com.myshopping.models.Product
import com.myshopping.models.User
import com.myshopping.ui.activities.*
import com.myshopping.ui.fragments.DashboardFragment
import com.myshopping.ui.fragments.ProductsFragment
import com.myshopping.utils.Constants
import com.myshopping.utils.Constants.PRODUCTS
import com.myshopping.utils.UtilsFunctions


class FirestoreClass {


    private val mFireStore = FirebaseFirestore.getInstance()

    fun registerUser(activity: RegisterActivity, userInfo: User) {

        mFireStore.collection(Constants.USERS)
            .document(userInfo.id)
            .set(userInfo, SetOptions.merge())
            .addOnSuccessListener {
                activity.userRegistrationSuccess()
            }
            .addOnFailureListener { e ->
                activity.userRegistrationFailure(e)
            }
    }

    fun getCurrentUserID(): String {
        val currentUser = FirebaseAuth.getInstance().currentUser
        var currentUserID = ""
        if (currentUser != null) {
            currentUserID = currentUser.uid
        }

        return currentUserID
    }

    fun getUserDetails(activity: Activity) {

        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserID())
            .get()
            .addOnSuccessListener { document ->

                val user = document.toObject(User::class.java)!!

                //Todo(Shared Pref Change to singleTon)
                val sharedPreferences =
                    activity.getSharedPreferences(
                        Constants.MY_SHOPPING_PREF,
                        Context.MODE_PRIVATE
                    )

                val editor: SharedPreferences.Editor = sharedPreferences.edit()
                editor.putString(
                    Constants.LOGGED_IN_USERNAME,
                    "${user.firstName} ${user.lastName}"
                )
                editor.apply()

                when (activity) {
                    is LoginActivity -> {
                        activity.userLoggedInSuccess(user)
                    }
                    is SettingActivity -> {
                        activity.userLoggedInSuccess(user)
                    }
                }
            }
            .addOnFailureListener { e ->
                when (activity) {
                    is LoginActivity -> {
                        activity.userLoggedInFailure(e)
                    }
                    is SettingActivity -> {
                        activity.userLoggedInFailure(e)
                    }
                }

            }
    }

    fun updateUserProfileData(activity: Activity, userHashMap: HashMap<String, Any>) {
        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserID())
            .update(userHashMap)
            .addOnSuccessListener {
                when (activity) {
                    is UserProfileActivity -> {
                        activity.userProfileUpdateSuccess()
                    }
                }
            }
            .addOnFailureListener { e ->
                when (activity) {
                    is UserProfileActivity -> {
                        activity.userProfileUpdateFailure(e)
                    }
                }
            }
    }

    fun uploadProduct(activity: Activity, product: Product) {

        val newProduct = mFireStore.collection(PRODUCTS)
            .document()

        product.id = newProduct.id

        newProduct.set(product, SetOptions.merge())
            .addOnSuccessListener {
                when (activity) {
                    is AddProductActivity -> {
                        activity.productUploadedSuccessfully()
                    }
                }
            }
            .addOnFailureListener { e ->
                when (activity) {
                    is AddProductActivity -> {
                        activity.productFailureMessages(e)
                    }
                }
            }
    }

    fun uploadImageToCloudStorage(activity: Activity, photoUri: Uri, imageType: String) {
        val sRef = FirebaseStorage.getInstance().reference.child(
            "${imageType}${System.currentTimeMillis()}${
                UtilsFunctions.getFileExtensions(
                    activity,
                    photoUri
                )
            }"
        )

        sRef.putFile(photoUri).addOnSuccessListener { taskSnapShot ->

            taskSnapShot.metadata!!.reference!!.downloadUrl
                .addOnSuccessListener { uri ->
                    when (activity) {
                        is UserProfileActivity -> {
                            activity.imageUploadSuccess(uri.toString())
                        }
                        is AddProductActivity -> {
                            activity.productImageUploadOnSuccess(uri.toString())
                        }
                    }
                }
                .addOnFailureListener { e ->
                    when (activity) {
                        is UserProfileActivity -> {
                            activity.imageUploadFail(e)
                        }
                        is AddProductActivity -> {
                            activity.productFailureMessages(e)
                        }
                    }
                }


        }.addOnFailureListener { e ->
            when (activity) {
                is UserProfileActivity -> {
                    activity.imageUploadFail(e)
                }
                is AddProductActivity -> {
                    activity.productFailureMessages(e)
                }
            }
        }
    }

    fun getProductsList(fragment: Fragment) {
        mFireStore.collection(PRODUCTS)
            .whereEqualTo(Constants.USER_ID, getCurrentUserID())
            .get()
            .addOnSuccessListener { document ->

                val productList: MutableList<Product> = mutableListOf()

                for (productObject in document.documents) {
                    with(productObject)
                    {
                        val product = this.toObject(Product::class.java)
                        productList.add(product!!)
                    }
                }

                when (fragment) {
                    is ProductsFragment -> {
                        fragment.onSuccessProductList(productList)
                    }
                }
            }
    }

    fun getDashboardProductsList(dashboardFragment: DashboardFragment) {

        mFireStore.collection(Constants.PRODUCTS)
            .get()
            .addOnSuccessListener { document ->

                val productList: MutableList<Product> = mutableListOf()

                for (productObject in document.documents) {
                    with(productObject)
                    {
                        val product = this.toObject(Product::class.java)
                        productList.add(product!!)
                    }
                }
                dashboardFragment.onSuccessProductList(productList)
            }
            .addOnFailureListener { e ->
                dashboardFragment.onFailureProductList(e)
            }

    }

    fun deleteProduct(productId: String, fragment: ProductsFragment) {
        mFireStore.collection(Constants.PRODUCTS)
            .document(productId)
            .delete()
            .addOnSuccessListener {
                fragment.onProductSuccessfullyDeleted()
            }
            .addOnFailureListener { e ->
                fragment.onProductDeleteFailure(e)
            }
    }

    fun fetchProductDetails(activity: Activity, productId: String) {

        mFireStore.collection(PRODUCTS)
            .document(productId)
            .get()
            .addOnSuccessListener { document ->
                val product = document.toObject(Product::class.java)
                when (activity) {
                    is ProductDetailsActivity -> {
                        activity.onProductDetailFetchSuccess(product!!)
                    }
                    is AddProductActivity -> {
                        activity.showProductDetails(product!!)
                    }
                }

            }
            .addOnFailureListener { e ->
                when (activity) {
                    is ProductDetailsActivity -> {
                        activity.onProductDetailFetchFailure(e)
                    }
                    is AddProductActivity -> {
                        activity.productFailureMessages(e)
                    }
                }
            }
    }

    fun updateProductDetails(
        productId: String,
        productDetails: HashMap<String, Any>,
        activity: AddProductActivity
    ) {
        mFireStore.collection(PRODUCTS).document(productId)
            .update(productDetails)
            .addOnSuccessListener {
                activity.productUpdatedSuccessfully()
            }
            .addOnFailureListener { e ->
                activity.productFailureMessages(e)
            }
    }

    fun addCartItem(activity: ProductDetailsActivity, cartItem: CartItem) {
        val newCartItem = mFireStore.collection(Constants.CART_ITEMS)
            .document()
        cartItem.id = newCartItem.id

        newCartItem.set(cartItem, SetOptions.merge())
            .addOnSuccessListener {
                activity.addCartSuccessfully()
            }
            .addOnFailureListener { e ->
                activity.addCartFailure(e)
            }

    }


    fun checkIfItemExistInCart(activity: ProductDetailsActivity, productId: String) {
        mFireStore.collection(Constants.CART_ITEMS)
            .whereEqualTo(Constants.USER_ID, getCurrentUserID())
            .whereEqualTo(Constants.CART_PRODUCT_ID, productId)
            .get()
            .addOnSuccessListener { document ->
                activity.productExitstCartSuccess(document.documents.size > 0)
            }
            .addOnFailureListener { e ->
                activity.addCartFailure(e)
            }
    }


    fun getCartList(activity: Activity) {
        mFireStore.collection(Constants.CART_ITEMS)
            .whereEqualTo(Constants.USER_ID, getCurrentUserID())
            .get()
            .addOnSuccessListener {
                val items = mutableListOf<CartItem>()
                for (document in it.documents) {
                    items.add(document.toObject(CartItem::class.java)!!)
                }
                when (activity) {
                    is CartListActivity -> {
                        activity.getCartItemsSuccess(items)
                    }
                }
            }
            .addOnFailureListener { e ->
                when (activity) {
                    is CartListActivity -> {
                        activity.addFailureFireStore(e)
                    }
                }
            }
    }

    fun getProductList(activity: CartListActivity) {
        mFireStore.collection(Constants.PRODUCTS)
            .get()
            .addOnSuccessListener {
                val products = mutableListOf<Product>()
                for (document in it.documents) {
                    products.add(document.toObject(Product::class.java)!!)
                }
                activity.getAllProductSuccess(products)
            }
            .addOnFailureListener { e ->
                activity.addFailureFireStore(e)
            }

    }

    fun deleteCartItem(activity: Context, itemId: String) {
        mFireStore.collection(Constants.CART_ITEMS)
            .document(itemId)
            .delete()
            .addOnSuccessListener {
                when (activity) {
                    is CartListActivity -> {

                        activity.cartItemDeletedSuccessfully()
                    }
                }
            }
            .addOnFailureListener { e ->
                when (activity) {
                    is CartListActivity -> {

                        activity.addFailureFireStore(e)
                    }
                }
            }
    }


    fun updateMyCart(activity: Context, cartId: String, itemHashMap: HashMap<String, Any>) {
        mFireStore.collection(Constants.CART_ITEMS)
            .document(cartId)
            .update(itemHashMap)
            .addOnSuccessListener {
                when (activity) {
                    is CartListActivity -> {

                        activity.itemUpdatedSuccessfully()
                    }
                }
            }
            .addOnFailureListener { e ->
                when (activity) {
                    is CartListActivity -> {

                        activity.addFailureFireStore(e)
                    }
                }
            }
    }

    fun addAddress(addEditAddressActivity: AddEditAddressActivity, finalAddress: Address) {
        val newAddress = mFireStore.collection(Constants.ADDRESSES)
            .document()
        finalAddress.id = newAddress.id

        newAddress.set(finalAddress, SetOptions.merge())
            .addOnSuccessListener {
                addEditAddressActivity.addressAddedSuccessfully()
            }
            .addOnFailureListener { e ->
                addEditAddressActivity.addressAddFailure(e)
            }

    }

    fun getAddresses(addressListActivity: AddressListActivity) {
        mFireStore.collection(Constants.ADDRESSES)
            .whereEqualTo(Constants.USER_ID, getCurrentUserID())
            .get()
            .addOnSuccessListener {
                val mutableList = mutableListOf<Address>()
                for (document in it.documents) {
                    mutableList.add(document.toObject(Address::class.java)!!)
                }
                addressListActivity.getAddressesSuccessfully(mutableList)
            }
            .addOnFailureListener { e ->
                addressListActivity.firebaseFailure(e)
            }
    }


    fun getAddress(addEditAddressActivity: AddEditAddressActivity, address_id: String) {

        mFireStore.collection(Constants.ADDRESSES)
            .document(address_id)
            .get()
            .addOnSuccessListener {
                val address = it.toObject(Address::class.java)!!
                addEditAddressActivity.getAddressSuccessfully(address)
            }
            .addOnFailureListener { e ->
                addEditAddressActivity.addressAddFailure(e)
            }
    }

    fun editAddress(
        addEditAddressActivity: AddEditAddressActivity,
        address_id: String,
        hashMap: HashMap<String, Any>
    ) {
        mFireStore.collection(Constants.ADDRESSES)
            .document(address_id)
            .update(hashMap)
            .addOnSuccessListener {
                addEditAddressActivity.addressEditedSuccessfully()
            }
            .addOnFailureListener { e ->
                addEditAddressActivity.addressAddFailure(e)
            }
    }
}
