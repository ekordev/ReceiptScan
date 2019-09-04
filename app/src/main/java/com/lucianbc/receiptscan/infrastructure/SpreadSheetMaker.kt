package com.lucianbc.receiptscan.infrastructure

import com.lucianbc.receiptscan.domain.export.TextReceipt
import com.lucianbc.receiptscan.infrastructure.entities.ProductEntity
import com.lucianbc.receiptscan.presentation.displayName
import io.reactivex.Single
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.OutputStream

@Suppress("UNUSED_CHANGED_VALUE")
class SpreadSheetMaker {
    private val workbook = XSSFWorkbook()
    private val receipts =
        workbook.createSheet("Receipts")
    private val products =
        workbook.createSheet("Products")

    private val helper = workbook.creationHelper

    private val headerFont = workbook.createFont().apply {
        bold = true
    }

    private val headerStyle = workbook.createCellStyle().apply {
        setFont(headerFont)
    }

    private val dateStyle = workbook.createCellStyle().apply {
        dataFormat = helper.createDataFormat().getFormat("dd-MM-yyyy")
    }

    fun create(data: List<TextReceipt>) =
        Single.fromCallable {
            writeReceiptHeader()
            writeProductHeader()

            data.forEachIndexed { index, receipt ->
                writeReceiptRow(index + 1, receipt)
            }

            data.flatMap { it.productEntities }
                .forEachIndexed { index, product ->
                    writeProduct(index + 1, product)
                }
            this
        }

    fun persist(outputStream: OutputStream) {
        try {
            workbook.write(outputStream)
        } finally {
            outputStream.close()
            workbook.close()
        }
    }

    private fun writeReceiptRow(index: Int, receipt: TextReceipt) {
        receipts.createRow(index).apply {
            var crtCell = 0
            createCell(crtCell++, CellType.NUMERIC)
                .setCellValue(receipt.id.toString())
            createCell(crtCell++, CellType.STRING)
                .setCellValue(receipt.merchantName)
            createCell(crtCell++).apply {
                setCellValue(receipt.date)
                cellStyle = dateStyle
            }
            createCell(crtCell++)
                .setCellValue(receipt.total.toDouble())
            createCell(crtCell++)
                .setCellValue(receipt.currency.toString())
            createCell(crtCell++)
                .setCellValue(receipt.category.displayName)
        }
    }

    private fun writeProduct(index: Int, prod: ProductEntity) {
        products.createRow(index)
            .apply {
                var crtCell = 0
                createCell(crtCell++, CellType.NUMERIC)
                    .setCellValue(prod.id.toString())
                createCell(crtCell++, CellType.NUMERIC)
                    .setCellValue(prod.receiptId.toString())
                createCell(crtCell++, CellType.STRING)
                    .setCellValue(prod.name)
                createCell(crtCell++, CellType.NUMERIC)
                    .setCellValue(prod.price.toDouble())
            }
    }

    private fun writeReceiptHeader() =
        receipts.createRow(0).run {
            listOf(
                "id",
                "merchantName",
                "date",
                "total",
                "currency",
                "category"
            ).forEachIndexed { index, colName ->
                createCell(index).apply {
                    cellStyle = headerStyle
                    setCellValue(colName)
                }
            }
        }

    private fun writeProductHeader() =
        products.createRow(0).run {
            listOf(
                "id",
                "receiptId",
                "name",
                "price"
            ).forEachIndexed { index, colName ->
                createCell(index).apply {
                    cellStyle = headerStyle
                    setCellValue(colName)
                }
            }
        }
}

