//package com.lucianbc.receiptscan.infrastructure
//
//import com.lucianbc.receiptscan.domain.export.TextReceipt
//import com.lucianbc.receiptscan.infrastructure.entities.ProductEntity
//import com.lucianbc.receiptscan.presentation.displayName
//import io.reactivex.Completable
//import org.apache.poi.xssf.usermodel.XSSFWorkbook
//import java.io.File
//
//@Suppress("UNUSED_CHANGED_VALUE")
//class SpreadSheetMaker {
//    private val workbook = XSSFWorkbook()
//    private val receipts =
//        workbook.createSheet("Receipts")
//    private val products =
//        workbook.createSheet("Products")
//
//    private val helper = workbook.creationHelper
//
//    private val headerFont = workbook.createFont().apply {
//        bold = true
//    }
//
//    private val headerStyle = workbook.createCellStyle().apply {
//        setFont(headerFont)
//    }
//
//    private val dateStyle = workbook.createCellStyle().apply {
//        dataFormat = helper.createDataFormat().getFormat("dd-MM-yyyy")
//    }
//
//    fun writeToSpreadsheet(data: List<TextReceipt>, file: File): Completable {
//        return Completable.fromCallable {
//            writeReceiptHeader()
//            writeProductHeader()
//
//            data.forEachIndexed { index, receipt ->
//                writeReceiptRow(index + 1, receipt)
//            }
//
//            data.flatMap { it.productEntities }
//                .forEachIndexed { index, product ->
//                    writeProduct(index + 1, product)
//                }
//            val os = file.outputStream()
//
//            try {
//                workbook.write(os)
//            } finally {
//                workbook.close()
//                os.close()
//            }
//        }
//    }
//
//
//    private fun writeReceiptRow(index: Int, receipt: TextReceipt) {
//        receipts.createRow(index).apply {
//            var crtCell = 0
//            createCell(crtCell++)
//                .setCellValue(receipt.id.toString())
//            createCell(crtCell++)
//                .setCellValue(receipt.merchantName)
//            createCell(crtCell++).apply {
//                setCellValue(receipt.date)
//                cellStyle = dateStyle
//            }
//            createCell(crtCell++)
//                .setCellValue(receipt.total.toDouble())
//            createCell(crtCell++)
//                .setCellValue(receipt.currency.toString())
//            createCell(crtCell++)
//                .setCellValue(receipt.category.displayName)
//        }
//    }
//
//    private fun writeProduct(index: Int, prod: ProductEntity) {
//        products.createRow(index)
//            .apply {
//                var crtCell = 0
//                createCell(crtCell++)
//                    .setCellValue(prod.id.toString())
//                createCell(crtCell++)
//                    .setCellValue(prod.receiptId.toString())
//                createCell(crtCell++)
//                    .setCellValue(prod.name)
//                createCell(crtCell++)
//                    .setCellValue(prod.price.toDouble())
//            }
//    }
//
//    private fun writeReceiptHeader() =
//        receipts.createRow(0).run {
//            listOf(
//                "id",
//                "merchantName",
//                "date",
//                "total",
//                "currency",
//                "category"
//            ).forEachIndexed { index, colName ->
//                createCell(index).apply {
//                    cellStyle = headerStyle
//                    setCellValue(colName)
//                }
//            }
//        }
//
//    private fun writeProductHeader() =
//        products.createRow(0).run {
//            listOf(
//                "id",
//                "receiptId",
//                "name",
//                "price"
//            ).forEachIndexed { index, colName ->
//                createCell(index).apply {
//                    cellStyle = headerStyle
//                    setCellValue(colName)
//                }
//            }
//        }
//}
//
