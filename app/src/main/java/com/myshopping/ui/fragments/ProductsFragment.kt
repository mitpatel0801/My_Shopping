package com.myshopping.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.myshopping.R
import com.myshopping.databinding.FragmentProductsBinding
import com.myshopping.firestore.FirestoreClass
import com.myshopping.models.Product
import com.myshopping.ui.activities.AddProductActivity
import com.myshopping.ui.adapters.MyProductsListAdapter
import com.myshopping.ui.viewmodels.ProductsViewModel
import kotlinx.android.synthetic.main.fragment_products.*

class ProductsFragment : BaseFragment() {

    private lateinit var productsViewModel: ProductsViewModel
    private var _binding: FragmentProductsBinding? = null


    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        productsViewModel =
            ViewModelProvider(this).get(ProductsViewModel::class.java)

        _binding = FragmentProductsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.add_product, menu)
        super.onCreateOptionsMenu(menu, inflater)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_add_product -> {
                val intent = Intent(this.activity, AddProductActivity::class.java)
                startActivity(intent)
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun onSuccessProductList(productList: MutableList<Product>) {
        hideProgressDialog()
        if (productList.isNotEmpty()) {

            tv_no_products_found.visibility = View.GONE
            rv_my_product_items.visibility = View.VISIBLE

            rv_my_product_items.layoutManager = LinearLayoutManager(requireContext())
            rv_my_product_items.setHasFixedSize(true)

            val productAdapter = MyProductsListAdapter(requireContext(), productList)
            rv_my_product_items.adapter = productAdapter


        } else {
            tv_no_products_found.visibility = View.VISIBLE
            rv_my_product_items.visibility = View.GONE
        }
    }

    override fun onResume() {
        super.onResume()
        showProducts()
    }

    private fun showProducts() {
        showProgressDialog(getString(R.string.please_wait))
        FirestoreClass().getProductsList(this)
    }
}