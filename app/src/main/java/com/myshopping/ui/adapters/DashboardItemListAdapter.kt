package com.myshopping.ui.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.myshopping.R
import com.myshopping.models.Product
import com.myshopping.ui.activities.ProductDetailsActivity
import com.myshopping.utils.Constants
import com.myshopping.utils.GlideLoader

class DashboardItemListAdapter(private val context: Context, private val list: List<Product>) :
    RecyclerView.Adapter<DashboardItemListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DashboardItemListAdapter.ViewHolder {
        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_dashboard_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int = list.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val imageView = itemView.findViewById<ImageView>(R.id.iv_dashboard_item_image)
        private val name = itemView.findViewById<TextView>(R.id.tv_dashboard_item_title)
        private val price = itemView.findViewById<TextView>(R.id.tv_dashboard_item_price)

        fun bind(product: Product) {
            GlideLoader(context).loadProductPicture(product.image, imageView)
            name.text = product.title
            price.text = context.getString(R.string.product_price, product.price)

            itemView.setOnClickListener {
                val intent = Intent(context, ProductDetailsActivity::class.java)
                intent.putExtra(Constants.EXTRA_PRODUCT_ID, product.id)
                context.startActivity(intent)
            }
        }


    }

}
