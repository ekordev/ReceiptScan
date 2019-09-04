//package com.lucianbc.receiptscan.infrastructure.workers
//
//import com.lucianbc.receiptscan.domain.export.TextReceipt
//import io.reactivex.Single
//import org.dhatim.fastexcel.Workbook
//import org.dhatim.fastexcel.Worksheet
//import java.io.File
//import java.io.OutputStream
//
//class SpreadSheetMaker {
//
//
//    fun create(data: List<TextReceipt>, destination: File): Single<String> {
//        return Single.fromCallable {
//            val wb = Workbook(destination.outputStream(), "ReceiptScan", "0.1")
//            val receipts = wb.newWorksheet("Receipts")
//            val products = wb.newWorksheet("Products")
//
//            destination.absolutePath
//        }
//    }
//
//    private fun writeReceiptHeader(sheet: Worksheet) {
//
//    }
//
//
//    fun persist(outputStream: OutputStream) {
//
//    }
//}