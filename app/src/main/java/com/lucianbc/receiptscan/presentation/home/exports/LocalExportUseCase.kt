package com.lucianbc.receiptscan.presentation.home.exports

import com.lucianbc.receiptscan.domain.export.ExportRepository
import com.lucianbc.receiptscan.domain.export.LocalSession
import com.lucianbc.receiptscan.domain.export.Status
import com.lucianbc.receiptscan.infrastructure.SpreadSheetMaker
import com.lucianbc.receiptscan.infrastructure.SpreadsheetWriter
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.rxkotlin.zipWith

class LocalExportUseCase @AssistedInject constructor(
    private val repo: ExportRepository,
    private val spreadsheetWriter: SpreadsheetWriter,
    @Assisted private val manifest: LocalSession
) {
    private fun execute(): Completable {
        return repo
            .persist(manifest, Status.BUILDING_SPREADSHEET)
            .andThen(readData())
            .zipWith(Single.just(spreadsheetWriter.provideDestination(manifest)))
            .flatMap {
                SpreadSheetMaker()
                    .writeToSpreadsheet(it.first, it.second)
                    .andThen(Single.just(it.second.absolutePath))
            }
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

    operator fun invoke() = execute()

    @AssistedInject.Factory
    interface Factory {
        fun create(manifest: LocalSession): LocalExportUseCase
    }
}