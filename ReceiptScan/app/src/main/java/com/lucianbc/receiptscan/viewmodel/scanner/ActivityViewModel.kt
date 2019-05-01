package com.lucianbc.receiptscan.viewmodel.scanner

import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ActivityViewModel : ViewModel() {

    sealed class State {
        object NoPermission: State()
        object Allowed : State()
        object Error : State()
        class Preview(val image: Bitmap) : State()
    }

    val state = MutableLiveData<State>()
}
