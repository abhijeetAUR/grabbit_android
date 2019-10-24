package com.example.grabbit.scanner.model

data class TDetailResponse(val Table1: List<TransactionDetailsResponse>)

data class TransactionDetailsResponse(val InventryID: Int,
                                      val INVOICEID: String,
                                      val DATEOFINSERT: String,
                                      val TIMEOFINSERT: String,
                                      val ITEMID: Int,
                                      val itemName: String,
                                      val QUANTITY: Int,
                                      val TRAYID: Int,
                                      val COLNUMBER: Int,
                                      val DISPENSED: Boolean,
                                      val PAYMENTMODE: String,
                                      val AMOUNT: Int,
                                      val Mobileno: String,
                                      val TransactionStauts: String,
                                      val FEEDBACKSPIRAL: String,
                                      val FEEDBACKSENSOR: String,
                                      val CARDREADERDATA: String,
                                      val DATACOLLECTED: String,
                                      val KIOSKID: String)