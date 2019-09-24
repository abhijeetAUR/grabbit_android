package com.example.grabbit.bnhome.bnhome


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.example.grabbit.R
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException

/**
 * A simple [Fragment] subclass.
 */
class HomeFragment : Fragment(), MenuAdapter.OnProductCategoryListener,
    ItemsAdapter.OnProductListClickListener {
    override fun onProductListClick(position: Int) {
        addItemToCart(items.elementAt(position))
    }

    override fun onProductCategoryClick(position: Int) {
        print(position)
        getProductListForSelectedCategory(position)
    }

    companion object{
        val service = HomeFactory.makeHomeService()
        val singletonProductDataHolder = SingletonProductDataHolder.instance
        var setFirstButtonSelected = true
        var menuAdapter : MenuAdapter? = null
        var itemsAdapter: ItemsAdapter? = null

        val btnNameAndStatus : ArrayList<String> = ArrayList()
        val items : ArrayList<HomeResponseList> = ArrayList()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        progressBar.visibility = View.VISIBLE
        rv_product_list.layoutManager = LinearLayoutManager(activity?.applicationContext, RecyclerView.HORIZONTAL, false)
        menuAdapter = MenuAdapter(categories = btnNameAndStatus)
        rv_product_list.adapter =  menuAdapter
        itemsAdapter = ItemsAdapter(items, activity!!.applicationContext)
        purchase_item_list.layoutManager = LinearLayoutManager(activity?.applicationContext)
        purchase_item_list.adapter = itemsAdapter
        menuAdapter!!.setOnItemClickListener(this)
        itemsAdapter!!.setOnItemClickListener(this)
        fetchProductListApi()
    }

    private fun getProductListForSelectedCategory(position: Int){
        var lst: ArrayList<BtnNameAndStatus> = arrayListOf()
        val categoryName = singletonProductDataHolder.lstBtnNameAndStatus.elementAt(position).name
        lst.clear()
        for (btnNameAndStatus in singletonProductDataHolder.lstBtnNameAndStatus) {
            if (btnNameAndStatus.name.contentEquals(categoryName))
                lst.add(BtnNameAndStatus(name = btnNameAndStatus.name, status = true))
            else
                lst.add(BtnNameAndStatus(name = btnNameAndStatus.name, status = false))
        }
        singletonProductDataHolder.lstBtnNameAndStatus.clear()
        singletonProductDataHolder.lstBtnNameAndStatus.addAll(lst)
        items.clear()
        items.addAll(getSelectedCategory())
        itemsAdapter!!.notifyDataSetChanged()
    }

    private fun addItemToCart(item: HomeResponseList){
        singletonProductDataHolder.lstProductsAddedToCart.add(item)
    }

    private fun fetchProductListApi() {
        CoroutineScope(Dispatchers.IO).launch {
            val response = service.getProductListService("00001")
            withContext(Dispatchers.Main) {
                try {
                    if (response.isSuccessful) {
                        //TODO: Update ui on response
                        //Add to singleton class
                        putDataInSingleton(response.body()!!.Table1)
                        progressBar.visibility = View.GONE
                    } else {
                        print("Error: ${response.code()}")
                    }
                } catch (e: HttpException) {
                    e.printStackTrace()
                } catch (e: Throwable) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun putDataInSingleton(lstResponseProducts : List<HomeResponseList>){
        addProductsToSingletonDictionary(lstResponseProducts)
        createBtnNameAndStatus(lstResponseProducts)
    }

    private fun createBtnNameAndStatus(lstResponseProducts: List<HomeResponseList>) {
        val categories = lstResponseProducts.map { it.TYPE }.toSet().toList()
        if(singletonProductDataHolder.lstBtnNameAndStatus.isEmpty()){
            for (category in categories) {
                if (setFirstButtonSelected){
                    setFirstButtonSelected = false
                    singletonProductDataHolder.lstBtnNameAndStatus.add(BtnNameAndStatus(name = category, status = true))
                } else{
                    singletonProductDataHolder.lstBtnNameAndStatus.add(BtnNameAndStatus(name = category, status = false))
                }
            }
        }
        btnNameAndStatus.clear()
        btnNameAndStatus.addAll(categories)
        menuAdapter!!.notifyDataSetChanged()

        items.clear()
        items.addAll(getSelectedCategory())
        itemsAdapter!!.notifyDataSetChanged()
    }

    private fun addProductsToSingletonDictionary(lstResponseProducts : List<HomeResponseList>) {
        val categories = lstResponseProducts.map { it.TYPE }.toSet().toList()
        for (category in categories) {
            val lstHomeProducts: ArrayList<HomeResponseList> = arrayListOf()
            for (product in lstResponseProducts) {
                if (product.TYPE.contentEquals(category)){
                    lstHomeProducts.add(product)
                }
            }
            singletonProductDataHolder.homeProductDictionary[category] = lstHomeProducts
        }
    }

    private fun getSelectedCategory(): ArrayList<HomeResponseList> {
        val selectedCategory = singletonProductDataHolder.lstBtnNameAndStatus.filter { it.status }
        val list = singletonProductDataHolder.homeProductDictionary[selectedCategory[0].name]
        return list!!
    }

}
