package com.example.grabbit.operator.model

data class OPProductListResponse(val Table1: List<OPProductList>)

data class OPProductList(val COLNUMBER: Int,
                                 val TRAYID: Int,
                                 val MAXCAPACITY: Int,
                                 val ISBLOCKED: Boolean,
                                 val CHILLED: Boolean,
                                 val SERIALDATA: String,
                                 val ITEMID: Int,
                                 val KioskID: String,
                                 val TRAYCOLID: Int,
                                 val ITEMNAME: String,
                                 val TYPE: String,
                                 val ITEMDESC: String,
                                 val ITEMRATE: Int,
                                 val ITEMENABLED: Boolean,
                                 val ITEMIMAGE: String,
                                 val CLIENTID: Int){
}
