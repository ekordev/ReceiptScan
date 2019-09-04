package com.lucianbc.receiptscan.presentation.home.exports

import com.lucianbc.receiptscan.domain.export.ExportRepository
import com.lucianbc.receiptscan.domain.export.LocalSession
import com.lucianbc.receiptscan.domain.export.Status
import com.lucianbc.receiptscan.domain.export.TextReceipt
import com.lucianbc.receiptscan.infrastructure.SpreadSheetMaker
import com.lucianbc.receiptscan.infrastructure.SpreadsheetWriter
import com.lucianbc.receiptscan.util.logd
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import io.reactivex.Completable
import io.reactivex.Single
import java.io.File
import java.net.URI

class LocalExportUseCase @AssistedInject constructor(
    private val repo: ExportRepository,
    private val spreadsheetWriter: SpreadsheetWriter,
    @Assisted private val manifest: LocalSession
) {
    private fun execute(): Completable {
        return repo
            .persist(manifest, Status.BUILDING_SPREADSHEET)
            .andThen(readData())
            .flatMap(::makeSpreadsheet)
            .map(::writeSpreadsheet)
            .doOnSuccess {
                repo.updateStatus(manifest.id, Status.COMPLETE, it)
            }
            .doOnError {
                repo.updateStatus(manifest.id, Status.ERROR)
            }
            .ignoreElement()
    }

    private fun readData() =
        repo.getTextReceiptsBeteewn(manifest.firstDate, manifest.lastDate)

    private fun makeSpreadsheet(data: List<TextReceipt>) =
        SpreadSheetMaker().create(data)

    private fun writeSpreadsheet(maker: SpreadSheetMaker): String {
        val file = spreadsheetWriter.provideDestination(manifest)
        val outputStream = file.outputStream()
        maker.persist(outputStream)
        return file.absoluteFile.absolutePath
    }

    operator fun invoke() = execute()

    @AssistedInject.Factory
    interface Factory {
        fun create(manifest: LocalSession): LocalExportUseCase
    }
}