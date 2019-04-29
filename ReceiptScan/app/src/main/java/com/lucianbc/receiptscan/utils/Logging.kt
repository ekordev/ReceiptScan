package com.lucianbc.receiptscan.utils

import android.util.Log

@Suppress("unused")
inline fun <reified T> T.logd(message: String) =
    Log.d(T::class.java.simpleName, message)