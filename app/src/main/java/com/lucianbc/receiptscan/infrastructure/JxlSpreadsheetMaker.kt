package com.lucianbc.receiptscan.infrastructure

import com.lucianbc.receiptscan.domain.export.TextReceipt
import io.reactivex.Completable
import java.io.File

class SpreadSheetMaker {
    fun writeToSpreadsheet(data: List<TextReceipt>?, dest: File?): Completable {
        return Completable.fromCallable {
//            val workbook = Workbook.createWorkbook(dest)
//
//            val receipts = workbook.createSheet("Receipts", 0)
//
//            receipts.addCell(Number(0, 0, 3.0))
        }
    }

}