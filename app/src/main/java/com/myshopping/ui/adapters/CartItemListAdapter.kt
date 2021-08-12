package com.myshopping.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.myshopping.R
import com.myshopping.firestore.FirestoreClass
import com.myshopping.models.CartItem
import com.myshopping.ui.activities.CartListActivity
import com.myshopping.utils.Constants
import com.myshopping.utils.GlideLoader
import com.myshopping.utils.customWidgets.MSPTextView
import com.myshopping.utils.customWidgets.MSPTextViewBold

class CartItemListAdapter(private val context: Context, private val list: List<CartItem>) :
    RecyclerView.Adapter<CartItemListAdapter.ViewHolder>() {


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val image: ImageView = itemView.findViewById(R.id.iv_cart_item_image)
        val name: MSPTextView = itemView.findViewById(R.id.tv_cart_item_title)
        var price: MSPTextViewBold =
            itemView.findViewById(R.id.tv_cart_item_price)
        var quantity: MSPTextView = itemView.findViewById(R.id.tv_cart_quantity)
        val addButton = itemView.findViewById<ImageButton>(R.id.ib_add_cart_item)
        val removeButton = itemView.findViewById<ImageButton>(R.id.ib_remove_cart_item)

        fun bind(cartItem: CartItem) {
            GlideLoader(context).loadProductPicture(cartItem.image, image)
            name.text = cartItem.title
            price.text = context.getString(R.string.product_price, cartItem.price)
            quantity.text = cartItem.cart_quantity


            itemView.findViewById<ImageButton>(R.id.ib_delete_cart_item).setOnClickListener {
                deleteItem(cartItem.id)
            }

            if (cartItem.stock_quantity.toInt() == 0) {
                addButton.visibility = View.GONE
                removeButton.visibility = View.GONE
                quantity.text = context.getString(R.string.lbl_out_of_stock)
                quantity.setTextColor(ContextCompat.getColor(context, R.color.colorSnackBarError))

            } else {
                addButton.visibility = View.VISIBLE
                removeButton.visibility = View.VISIBLE
                quantity.setTextColor(ContextCompat.getColor(context, R.color.colorSecondaryText))
            }

            addButton.setOnClickListener {
                if (cartItem.cart_quantity.toInt() >= cartItem.stock_quantity.toInt()) {

                    if (context is CartListActivity) {
                        context.showErrorSnackBar(
                            context.getString(
                                R.string.msg_for_available_stock,
                                cartItem.stock_quantity
                            ), true
                        )

                    }
                } else {
                    val cartQuantity = (cartItem.cart_quantity.toInt() + 1).toString()
                    val hashMap = HashMap<String, Any>()

                    hashMap[Constants.CART_QUANTITY] = cartQuantity

                    if (context is CartListActivity) {
                        context.showProgressDialog(context.getString(R.string.please_wait))
                    }
                    FirestoreClass().updateMyCart(context, cartItem.id, hashMap)
                }
            }

            removeButton.setOnClickListener {
                if (cartItem.cart_quantity == "1") {
                    deleteItem(cartItem.id)
                } else {
                    val cartQuantity = (cartItem.cart_quantity.toInt() - 1).toString()
                    val hashMap = HashMap<String, Any>()
                    hashMap[Constants.CART_QUANTITY] = cartQuantity

                    if (context is CartListActivity) {
                        context.showProgressDialog(context.getString(R.string.please_wait))
                    }
                    FirestoreClass().updateMyCart(context, cartItem.id, hashMap)
                }
            }
        }

        private fun deleteItem(itemId: String) {
            if (context is CartListActivity) {
                context.showProgressDialog(context.getString(R.string.please_wait))
            }
            FirestoreClass().deleteCartItem(context, itemId)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_cart_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int = list.size
}