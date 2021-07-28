package com.myshopping.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.myshopping.R
import com.myshopping.models.Product
import com.myshopping.utils.GlideLoader

class MyProductsListAdapter(private val context: Context, private val list: List<Product>) :
    RecyclerView.Adapter<MyProductsListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyProductsListAdapter.ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_list_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int = list.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val imageView = itemView.findViewById<ImageView>(R.id.iv_item_image)
        private val name = itemView.findViewById<TextView>(R.id.tv_item_name)
        private val price = itemView.findViewById<TextView>(R.id.tv_item_price)

        fun bind(product: Product) {
            GlideLoader(context).loadProductPicture(product.image, imageView)
            name.text = product.title
            price.text = product.price
        }

    }
}
