package com.example.grabbit.bnhome.bnhome

data class HomeResponse(val Table1: List<HomeResponseList>)

data class HomeResponseList(val COLNUMBER: Int,
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
                            val CLIENTID: Int
){
    companion object{}
}
