package com.myshopping.ui.fragments

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
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

            val productAdapter = MyProductsListAdapter(requireContext(), productList, this)
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

    fun onProductDelete(id: String) {
        showAlertDialogToDeleteProduct(id)
    }

    fun onProductDeleteFailure(e: Exception) {
        hideProgressDialog()
        Log.e(javaClass.simpleName, e.message, e)
    }

    fun onProductSuccessfullyDeleted() {
        hideProgressDialog()
        Toast.makeText(
            requireContext(),
            resources.getString(R.string.product_delete_success_message),
            Toast.LENGTH_SHORT
        ).show()

        showProducts()
    }

    private fun showAlertDialogToDeleteProduct(productId: String) {
        AlertDialog.Builder(requireContext())

            .setTitle(getString(R.string.delete_dialog_title))
            .setMessage(getString(R.string.delete_dialog_message))
            .setIcon(android.R.drawable.ic_dialog_alert)

            .setPositiveButton(getString(R.string.yes)) { dialogInterface, _ ->
                showProgressDialog(resources.getString(R.string.please_wait))
                FirestoreClass().deleteProduct(productId, this)
                dialogInterface.dismiss()
            }

            .setNegativeButton(getString(R.string.no)) { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
            .setCancelable(false)
            .create()
            .show()
    }
}