package com.myshopping.ui.adapters

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.myshopping.R
import com.myshopping.firestore.FirestoreClass
import com.myshopping.models.Address
import com.myshopping.ui.activities.AddEditAddressActivity
import com.myshopping.ui.activities.CheckoutActivity
import com.myshopping.utils.Constants
import com.myshopping.utils.customWidgets.MSPTextView
import com.myshopping.utils.customWidgets.MSPTextViewBold

class AddressListAdapter(
    private val mContext: Context,
    private val list: List<Address>,
    private val isAddressSelected: Boolean
) :
    RecyclerView.Adapter<AddressListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_address_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int = list.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val fullName = itemView.findViewById<MSPTextViewBold>(R.id.tv_address_full_name)
        private val addressDetails = itemView.findViewById<MSPTextView>(R.id.tv_address_details)
        private val phoneNumber = itemView.findViewById<MSPTextView>(R.id.tv_address_mobile_number)
        private val addressType = itemView.findViewById<MSPTextView>(R.id.tv_address_type)

        fun bind(address: Address) {
            fullName.text = address.name
            addressDetails.text = address.address
            phoneNumber.text = address.mobileNumber
            addressType.text = address.type

            if (isAddressSelected) {
                itemView.setOnClickListener {
                    val intent = Intent(mContext, CheckoutActivity::class.java)
                    intent.putExtra(Constants.EXTRA_SELECT_ADDRESS, address)
                    mContext.startActivity(intent)
                }
            }
        }
    }

    fun editItem(activity: Activity, adapterPosition: Int) {
        val intent = Intent(mContext, AddEditAddressActivity::class.java)
        intent.putExtra(Constants.EXTRA_ADDRESS_ID, list[adapterPosition].id)
        activity.startActivityForResult(intent, Constants.ADD_ADDRESS_SELECT_REQUEST_CODE)
        notifyItemChanged(adapterPosition)
    }

    fun deleteItem(adapterPosition: Int) {
        FirestoreClass().deleteAddress(mContext, list[adapterPosition].id)
    }

}