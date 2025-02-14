package com.lucianbc.receiptscan.domain.drafts

import com.lucianbc.receiptscan.domain.extract.DraftId
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class DraftsUseCaseImpl @Inject constructor(
    private val repository: DraftsRepository,
    private val manageFactory: ManageUseCase.Factory
) : DraftsUseCase {
    override fun list() = repository.listDrafts()

    override fun fetch(draftId: DraftId) =
        repository
            .getDraft(draftId)
            .subscribeOn(Schedulers.io())
            .let { manageFactory.create(draftId, it) }
}