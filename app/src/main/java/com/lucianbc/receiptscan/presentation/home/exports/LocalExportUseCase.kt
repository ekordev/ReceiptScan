package com.lucianbc.receiptscan.presentation.home.exports

import com.lucianbc.receiptscan.domain.export.LocalSession
import com.lucianbc.receiptscan.util.logd
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import io.reactivex.Completable

class LocalExportUseCase @AssistedInject constructor(
    @Assisted private val manifest: LocalSession
) {
    private fun execute(): Completable {
        return Completable.fromCallable {
            logd("Exporting manifest")
            logd(manifest.toString())
            Thread.sleep(5000)
        }
    }

    operator fun invoke() = execute()

    @AssistedInject.Factory
    interface Factory {
        fun create(manifest: LocalSession): LocalExportUseCase
    }
}