package com.example.grabbit.bnhome.bnhome

class SingletonProductDataHolder{
    companion object{
        val instance = SingletonProductDataHolder()
    }
    var lstProductsAddedToCart: List<HomeResponseList> = listOf()
    var homeProductDictionary: HashMap<String, ArrayList<HomeResponseList>> = hashMapOf()
    var lstBtnNameAndStatus: ArrayList<BtnNameAndStatus> = arrayListOf()
}

class BtnNameAndStatus(val name: String, val status: Boolean)