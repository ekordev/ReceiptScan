package com.lucianbc.receiptscan.viewmodel.scanner

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lucianbc.receiptscan.view.fragment.scanner.Element

class ScannerViewModel: ViewModel() {

    val flash = MutableLiveData<Boolean>()
    val elements = MutableLiveData<Iterable<Element>>()

    init {
        flash.value = false
        elements.value = emptyList()
    }

    fun toggleFlash() {
        flash.value = flash.value?.not()
    }
}
