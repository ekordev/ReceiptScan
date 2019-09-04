package com.lucianbc.receiptscan.presentation.home.exports.form

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lucianbc.receiptscan.R
import com.lucianbc.receiptscan.domain.export.CloudSession
import com.lucianbc.receiptscan.domain.export.LocalSession
import com.lucianbc.receiptscan.util.map
import java.util.*
import javax.inject.Inject

class FormViewModel @Inject constructor() : ViewModel() {

    sealed class Option {
        object Next: Option()
        object Check: Option()
    }

    val contentOption = MutableLiveData<Int>()
    val formatOption = MutableLiveData<Int>()
    val firstDate = MutableLiveData(Date())
    val lastDate = MutableLiveData(Date())
    val option = MutableLiveData<Option>(Option.Next)
    val isCheck = option.map { it == Option.Check }

    fun validateCloudInput() = CloudSession.validate(
        firstDate.value,
        lastDate.value,
        content(),
        format()
    )

    fun validateLocalInput() = LocalSession.validate(
        firstDate.value,
        lastDate.value
    )

    private fun content(): CloudSession.Content? =
        contentOption.value?.let {
            when(it) {
                R.id.contentText -> CloudSession.Content.TextOnly
                else -> CloudSession.Content.TextAndImage
            }
        }

    private fun format(): CloudSession.Format? =
        formatOption.value?.let {
            when(it) {
                R.id.contentCsv -> CloudSession.Format.CSV
                else -> CloudSession.Format.JSON
            }
        }
}