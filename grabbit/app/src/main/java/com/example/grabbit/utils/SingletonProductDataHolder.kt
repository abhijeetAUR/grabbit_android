package com.example.grabbit.utils

import com.example.grabbit.bnhome.bnhome.model.HomeResponseList
import com.example.grabbit.operator.model.OPProductList

class SingletonProductDataHolder{
    companion object{
        val instance = SingletonProductDataHolder()
    }
    var lstProductsAddedToCart: ArrayList<HomeResponseList> = arrayListOf()
    var homeProductDictionary: HashMap<String, ArrayList<HomeResponseList>> = hashMapOf()
    var lstBtnNameAndStatus: ArrayList<BtnNameAndStatus> = arrayListOf()
    var lstOfProductDispensed: ArrayList<DispensedItemData> = arrayListOf()
    var opProductDictionary: HashMap<String, ArrayList<OPProductList>> = hashMapOf()
}

data class DispensedItemData(val data : HomeResponseList, var status: Boolean, val invoiceId: String)

class BtnNameAndStatus(val name: String, val status: Boolean)