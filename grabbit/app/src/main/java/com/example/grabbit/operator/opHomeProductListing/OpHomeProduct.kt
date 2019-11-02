package com.example.grabbit.operator.opHomeProductListing


import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.example.grabbit.R
import com.example.grabbit.operator.opHomeProductListing.adapter.OPProductListAdapter
import com.example.grabbit.operator.opHomeProductListing.adapter.TrayListAdapter
import com.example.grabbit.operator.opHomeProductListing.contract.OperatorProductListFactory
import com.example.grabbit.operator.opHomeProductListing.model.OPProductList
import com.example.grabbit.operator.opProductDetailPage.OpProductDetailPage
import com.example.grabbit.utils.BtnNameAndStatus
import com.example.grabbit.utils.ConnectionDetector
import com.example.grabbit.utils.SingletonProductDataHolder
import kotlinx.android.synthetic.main.fragment_op_home_product.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * A simple [Fragment] subclass.
 */
class OpHomeProduct : Fragment(), TrayListAdapter.OnProductCategoryListener,
    OPProductListAdapter.OnProductListClickListener {



    private val singletonProductDataHolder = SingletonProductDataHolder.instance
    private var trayListAdapter: TrayListAdapter? = null
    private var opProductListAdapter: OPProductListAdapter? = null
    private val service = OperatorProductListFactory.makeOperatorProductList()
    private val btnNameAndStatus: ArrayList<String> = ArrayList()
    private var setFirstButtonSelected = true
    private val items: ArrayList<OPProductList> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_op_home_product, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setup()
    }

    private fun setup() {
        pb_op_product_listing.visibility = View.VISIBLE
        rv_op_tray_list.layoutManager = LinearLayoutManager(activity, RecyclerView.HORIZONTAL, false)
        trayListAdapter = TrayListAdapter(btnNameAndStatus)
        rv_op_tray_list.adapter = trayListAdapter

        rv_op_purchase_item_list.layoutManager =
            LinearLayoutManager(activity, RecyclerView.VERTICAL, false)

        opProductListAdapter = OPProductListAdapter(
            items,
            activity!!
        )

        rv_op_purchase_item_list.adapter = opProductListAdapter
        setOnClickForAdapter()
        checkInternetConnection()
    }

    private fun setOnClickForAdapter() {
        trayListAdapter!!.setOnItemClickListener(this@OpHomeProduct)
        opProductListAdapter!!.setOnItemClickListener(this@OpHomeProduct)
    }

    private fun checkInternetConnection() {
        ConnectionDetector(object : ConnectionDetector.Consumer {
            override fun accept(internet: Boolean?) {
                if (internet != null) {
                    if (internet) {
                        fetchProductListApi()
                    } else {
                        pb_op_product_listing.visibility = View.GONE
                        ConnectionDetector.showNoInternetConnectionDialog(context = activity!!)
                    }
                }
            }
        })
    }

    private fun fetchProductListApi() {
        CoroutineScope(Dispatchers.IO).launch {
            val response = service.getOperatorProductList("9890698284", "12345")
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    putDataInSingleton(response.body()!!.Table1)
                    pb_op_product_listing.visibility = View.GONE
                }
            }
        }

    }

    private fun putDataInSingleton(lstResponseProducts: List<OPProductList>) {
        addProductsToSingletonDictionary(lstResponseProducts)
        createBtnNameAndStatus(lstResponseProducts)
    }

    private fun addProductsToSingletonDictionary(lstResponseProducts: List<OPProductList>) {
        val categories = lstResponseProducts.map { it.TYPE }.toSet().toList()
        for (category in categories) {
            val lstHomeProducts: ArrayList<OPProductList> = arrayListOf()
            for (product in lstResponseProducts) {
                if (product.TYPE.contentEquals(category)) {
                    lstHomeProducts.add(product)
                }
            }
            singletonProductDataHolder.opProductDictionary[category] = lstHomeProducts
        }
    }

    private fun createBtnNameAndStatus(lstResponseProducts: List<OPProductList>) {
        val categories = lstResponseProducts.map { it.TYPE }.toSet().toList()
        if (singletonProductDataHolder.lstBtnNameAndStatus.isEmpty()) {
            for (category in categories) {
                if (setFirstButtonSelected) {
                    setFirstButtonSelected = false
                    singletonProductDataHolder.lstBtnNameAndStatus.add(
                        BtnNameAndStatus(
                            name = category,
                            status = true
                        )
                    )
                } else {
                    singletonProductDataHolder.lstBtnNameAndStatus.add(
                        BtnNameAndStatus(
                            name = category,
                            status = false
                        )
                    )
                }
            }
        }
        btnNameAndStatus.clear()
        btnNameAndStatus.addAll(categories)
        trayListAdapter!!.notifyDataSetChanged()

        items.clear()
        items.addAll(getSelectedCategory())
        opProductListAdapter!!.notifyDataSetChanged()
    }

    private fun getSelectedCategory(): ArrayList<OPProductList> {
        val selectedCategory = singletonProductDataHolder.lstBtnNameAndStatus.filter { it.status }
        val list = singletonProductDataHolder.opProductDictionary[selectedCategory[0].name]
        return list!!
    }


    private fun getProductListForSelectedCategory(position: Int) {
        var lst: ArrayList<BtnNameAndStatus> = arrayListOf()
        val categoryName = singletonProductDataHolder.lstBtnNameAndStatus.elementAt(position).name
        lst.clear()
        for (btnNameAndStatus in singletonProductDataHolder.lstBtnNameAndStatus) {
            if (btnNameAndStatus.name.contentEquals(categoryName))
                lst.add(
                    BtnNameAndStatus(
                        name = btnNameAndStatus.name,
                        status = true
                    )
                )
            else
                lst.add(
                    BtnNameAndStatus(
                        name = btnNameAndStatus.name,
                        status = false
                    )
                )
        }
        singletonProductDataHolder.lstBtnNameAndStatus.clear()
        singletonProductDataHolder.lstBtnNameAndStatus.addAll(lst)
        items.clear()
        items.addAll(getSelectedCategory())
        trayListAdapter!!.notifyDataSetChanged()
        opProductListAdapter!!.notifyDataSetChanged()
    }


    //Delegate functions
    override fun onProductCategoryClick(position: Int) {
        getProductListForSelectedCategory(position)
    }

    override fun onProductListClick(position: Int) {
        val intent = Intent(activity, OpProductDetailPage::class.java)
        val product = items[position]
        intent.putExtra("HomeProduct", product)
        startActivity(intent)
    }


}
