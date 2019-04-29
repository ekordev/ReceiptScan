package com.lucianbc.receiptscan.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ScannerViewModel : ViewModel() {

    sealed class State {
        object NoPermission: State()
        object Allowed : State()
        object Error : State()
    }

    val state = MutableLiveData<State>()

}
