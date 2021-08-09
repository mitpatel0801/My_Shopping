package com.myshopping.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.myshopping.R
import com.myshopping.databinding.FragmentDashboardBinding
import com.myshopping.firestore.FirestoreClass
import com.myshopping.models.Product
import com.myshopping.ui.activities.CartListActivity
import com.myshopping.ui.activities.SettingActivity
import com.myshopping.ui.adapters.DashboardItemListAdapter
import com.myshopping.ui.viewmodels.DashboardViewModel
import kotlinx.android.synthetic.main.fragment_dashboard.*

class DashboardFragment : BaseFragment() {

    private lateinit var dashboardViewModel: DashboardViewModel
    private var _binding: FragmentDashboardBinding? = null
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
        dashboardViewModel =
            ViewModelProvider(this).get(DashboardViewModel::class.java)

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.dashboard_fragment_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.dashboard_setting -> {
                startActivity(Intent(activity, SettingActivity::class.java))
                true
            }
            R.id.action_go_to_cart -> {
                startActivity(Intent(activity, CartListActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun onSuccessProductList(productList: MutableList<Product>) {
        hideProgressDialog()
        if (productList.isNotEmpty()) {
            tv_no_dashboard_items_found.visibility = View.GONE
            rv_dashboard_items.visibility = View.VISIBLE

            rv_dashboard_items.layoutManager = GridLayoutManager(requireContext(), 2)
            rv_dashboard_items.setHasFixedSize(true)

            val productAdapter = DashboardItemListAdapter(requireContext(), productList)
            rv_dashboard_items.adapter = productAdapter

        } else {
            tv_no_dashboard_items_found.visibility = View.VISIBLE
            rv_dashboard_items.visibility = View.GONE
        }
    }

    override fun onResume() {
        super.onResume()
        showProducts()
    }

    private fun showProducts() {
        showProgressDialog(getString(R.string.please_wait))
        FirestoreClass().getDashboardProductsList(this)
    }

    fun onFailureProductList(e: Exception) {
        Log.e(javaClass.simpleName, e.message, e)
    }

}