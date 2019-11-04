package com.example.grabbit.bnhome.bnhome


import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.grabbit.R
import com.example.grabbit.bnhome.bnhome.adapter.ItemsAdapter
import com.example.grabbit.bnhome.bnhome.adapter.MenuAdapter
import com.example.grabbit.bnhome.bnhome.contract.HomeFactory
import com.example.grabbit.bnhome.bnhome.contract.UpdateInvoiceService
import com.example.grabbit.bnhome.bnhome.model.HomeResponseList
import com.example.grabbit.utils.*
import com.example.grabbit.utils.ConnectionDetector
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.coroutines.*
import retrofit2.HttpException

/**
 * A simple [Fragment] subclass.
 */
class HomeFragment : Fragment(), MenuAdapter.OnProductCategoryListener,
    ItemsAdapter.OnProductListClickListener {

    var itemInvoiceUpdatedCount = 0

    companion object {
        val service = HomeFactory.makeHomeService()
        val singletonProductDataHolder = SingletonProductDataHolder.instance
        var setFirstButtonSelected = true
        var menuAdapter: MenuAdapter? = null
        var itemsAdapter: ItemsAdapter? = null
        val requestUpdateInvoice = UpdateInvoiceService.makeInvoiceService()
        var sharedPreferences: SharedPreferences? = null
        val btnNameAndStatus: ArrayList<String> = ArrayList()
        val items: ArrayList<HomeResponseList> = ArrayList()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        sharedPreferences = activity!!.getSharedPreferences(PREF_NAME, PRIVATE_MODE)
        progressBar.visibility = View.VISIBLE
        rv_product_list.layoutManager =
            LinearLayoutManager(activity?.applicationContext, RecyclerView.HORIZONTAL, false)
        menuAdapter =
            MenuAdapter(categories = btnNameAndStatus)
        rv_product_list.adapter = menuAdapter
        itemsAdapter = ItemsAdapter(
            items,
            activity!!.applicationContext
        )
        purchase_item_list.layoutManager = LinearLayoutManager(activity?.applicationContext)
        purchase_item_list.adapter = itemsAdapter
        menuAdapter!!.setOnItemClickListener(this)
        itemsAdapter!!.setOnItemClickListener(this)
//        addItems()
        sendUpdateInvoiceDataInRecursiveCall()
    }


    fun addItems() {
        var obj1 = HomeResponseList(
            COLNUMBER = 1,
            TRAYID = 1,
            MAXCAPACITY = 10,
            ISBLOCKED = false,
            CHILLED = false,
            SERIALDATA = "aiq",
            ITEMID = 1,
            KioskID = "00001",
            TRAYCOLID = 1,
            ITEMNAME = "Bingo Masala",
            TYPE = "ELITE",
            ITEMDESC = "ELITE",
            ITEMRATE = 10,
            ITEMENABLED = true,
            ITEMIMAGE = "https://grabbit.myvend.in/items/Bingo Masala.jpeg",
            CLIENTID = 1
        )

        var obj2 = HomeResponseList(
            COLNUMBER = 1,
            TRAYID = 1,
            MAXCAPACITY = 10,
            ISBLOCKED = false,
            CHILLED = false,
            SERIALDATA = "aiq",
            ITEMID = 1,
            KioskID = "00001",
            TRAYCOLID = 1,
            ITEMNAME = "Bingo Salted",
            TYPE = "ELITE",
            ITEMDESC = "ELITE",
            ITEMRATE = 10,
            ITEMENABLED = true,
            ITEMIMAGE = "https://grabbit.myvend.in/items/Bingo Masala.jpeg",
            CLIENTID = 1
        )

        singletonProductDataHolder.lstOfProductDispensed.add(
            DispensedItemData(
                obj1,
                true,
                invoiceId = "1000010000105"
            )
        )
        singletonProductDataHolder.lstOfProductDispensed.add(
            DispensedItemData(
                obj2,
                true,
                invoiceId = "1000010000106"
            )
        )
    }

    private fun sendUpdateInvoiceDataInRecursiveCall() {
        val dispensedItems = singletonProductDataHolder.lstOfProductDispensed.filter { it.status }
        if (dispensedItems.count() > 0) {
            var dispensedStatus = when (dispensedItems[itemInvoiceUpdatedCount].status) {
                true -> "0"
                false -> "1"
            }
            CoroutineScope(Dispatchers.IO).async {
                val response = requestUpdateInvoice.updateCreateInvoice(
                    invoiceIdNew = dispensedItems[itemInvoiceUpdatedCount].invoiceId,
                    itemName = dispensedItems[itemInvoiceUpdatedCount].data.ITEMNAME,
                    itemId = dispensedItems[itemInvoiceUpdatedCount].data.ITEMID.toString(),
                    qty = "1",//TODO:Change this
                    amount = dispensedItems[itemInvoiceUpdatedCount].data.ITEMRATE.toString(),
                    trayId = dispensedItems[itemInvoiceUpdatedCount].data.TRAYID.toString(),
                    colnumber = dispensedItems[itemInvoiceUpdatedCount].data.COLNUMBER.toString(),
                    dispensed = dispensedStatus
                )
                withContext(Dispatchers.Main) {
                    try {
                        if (response.isSuccessful) {
                            itemInvoiceUpdatedCount += 1
                            if (itemInvoiceUpdatedCount == singletonProductDataHolder!!.lstOfProductDispensed.count()) {
                                checkInternetConnection()
                            } else {
                                sendUpdateInvoiceDataInRecursiveCall()
                            }
                        } else {
//                            AlertDialogBox.showDialog(activity!!.applicationContext, "Error", "", "O", progressBar = progressBar)
                        }
                    } catch (e: HttpException) {
                        e.printStackTrace()
                    } catch (e: Throwable) {
                        e.printStackTrace()
                    }
                }
            }
        } else {
            checkInternetConnection()
        }
    }


    private fun checkInternetConnection() {
        ConnectionDetector(object : ConnectionDetector.Consumer {
            override fun accept(internet: Boolean?) {
                if (internet != null) {
                    if (internet) {
                        fetchProductListApi()
                    } else {
                        progressBar.visibility = View.GONE
                        ConnectionDetector.showNoInternetConnectionDialog(context = activity!!)
                    }
                }
            }
        })
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
        itemsAdapter!!.notifyDataSetChanged()
        menuAdapter!!.notifyDataSetChanged()
    }

    private fun addItemToCart(item: HomeResponseList) {
        singletonProductDataHolder.lstProductsAddedToCart.add(item)
    }

    private fun fetchProductListApi() {
        CoroutineScope(Dispatchers.IO).launch {
            val response = service.getProductListService("00001")
            withContext(Dispatchers.Main) {
                try {
                    if (response.isSuccessful) {
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

    private fun putDataInSingleton(lstResponseProducts: List<HomeResponseList>) {
        addProductsToSingletonDictionary(lstResponseProducts)
        createBtnNameAndStatus(lstResponseProducts)
    }

    private fun createBtnNameAndStatus(lstResponseProducts: List<HomeResponseList>) {
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
        menuAdapter!!.notifyDataSetChanged()

        items.clear()
        items.addAll(getSelectedCategory())
        itemsAdapter!!.notifyDataSetChanged()
    }

    private fun addProductsToSingletonDictionary(lstResponseProducts: List<HomeResponseList>) {
        val categories = lstResponseProducts.map { it.TYPE }.toSet().toList()
        for (category in categories) {
            val lstHomeProducts: ArrayList<HomeResponseList> = arrayListOf()
            for (product in lstResponseProducts) {
                if (product.TYPE.contentEquals(category)) {
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

    //Delegate functions
    override fun onProductListClick(position: Int) {
        if (!singletonProductDataHolder.lstProductsAddedToCart.contains(items.elementAt(position)) && (singletonProductDataHolder.lstProductsAddedToCart.count() + 1) <= 5) {
            addItemToCart(items.elementAt(position))
            Toast.makeText(
                activity?.applicationContext,
                "Item added to cart",
                Toast.LENGTH_SHORT
            ).show();
        } else if (singletonProductDataHolder.lstProductsAddedToCart.count() == 5) {
            Toast.makeText(
                activity?.applicationContext,
                "Maximum 5 items can be added to cart",
                Toast.LENGTH_LONG
            ).show();
        } else {
            Toast.makeText(
                activity?.applicationContext,
                "${items.elementAt(position).ITEMNAME} already added to cart",
                Toast.LENGTH_LONG
            ).show();
        }

    }

    override fun onProductCategoryClick(position: Int) {
        getProductListForSelectedCategory(position)
    }


}
