package com.lucianbc.receiptscan.infrastructure

import android.content.Context
import android.os.Environment
import com.lucianbc.receiptscan.domain.export.LocalSession
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class SpreadsheetWriter @Inject constructor(
    private val context: Context
) {
    private val dateFormat = SimpleDateFormat("dd-MM-yy", Locale.US)

    fun provideDestination(localSession: LocalSession) : File {
        assertWritable()

        val begin = dateFormat.format(localSession.firstDate)

        val end = dateFormat.format(localSession.lastDate)

        val filename = "Export_${begin}_${end}.xlsx"

        return getPrivateStorageDir(filename)
    }

    private fun getPrivateStorageDir(filename: String): File {
        val newFile = context
            .getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
            .let { File(it, filename) }

        check(newFile.mkdirs()) { "Could not create directory path" }

        return newFile
    }

    private fun assertWritable() = check(
        Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
    ) { "External Storage is not mounted" }
}