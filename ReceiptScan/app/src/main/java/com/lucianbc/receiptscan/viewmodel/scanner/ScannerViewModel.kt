package com.lucianbc.receiptscan.viewmodel.scanner

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ScannerViewModel: ViewModel() {

    val flash = MutableLiveData<Boolean>()

    init {
        flash.value = false
    }

    fun toggleFlash() {
        flash.value = flash.value?.not()
    }
}
