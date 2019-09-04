package com.lucianbc.receiptscan.presentation.home.exports

import com.lucianbc.receiptscan.domain.export.*
import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class ExportUseCaseImpl @Inject constructor(
    private val repository: ExportRepository,
    private val uploadFactory: UploadUseCase.Factory,
    private val localFactory: LocalExportUseCase.Factory
) : ExportUseCase {
    override fun list() = repository.linst()

    override fun markAsFinished(notification: FinishedNotification) =
        repository
            .updateStatus(
                notification.exportId,
                Status.COMPLETE,
                notification.downloadUrl
            )
            .subscribeOn(Schedulers.io())

    override fun newExport(manifest: CloudSession): Completable {
        return uploadFactory.create(manifest)()
    }

    override fun newExport(manifest: LocalSession): Completable {
        return localFactory.create(manifest)()
    }
}