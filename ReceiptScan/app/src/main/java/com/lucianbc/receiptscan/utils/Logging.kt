package com.lucianbc.receiptscan.utils

import android.util.Log

inline fun <reified T> logd(message: String) =
    Log.d(T::class.java.simpleName, message)