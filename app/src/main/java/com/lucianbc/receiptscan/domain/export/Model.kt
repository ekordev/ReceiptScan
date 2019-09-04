package com.lucianbc.receiptscan.domain.export

import android.os.Parcelable
import androidx.room.Relation
import com.lucianbc.receiptscan.domain.export.SessionException.Cause.*
import com.lucianbc.receiptscan.domain.model.Category
import com.lucianbc.receiptscan.infrastructure.entities.ProductEntity
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class CloudSession(
    val firstDate: Date,
    val lastDate: Date,
    val content: Content,
    val format: Format,
    val id: String
) : Parcelable {

    enum class Content {
        TextOnly,
        TextAndImage
    }

    enum class Format {
        JSON,
        CSV
    }

    companion object {
        @Suppress("LocalVariableName")
        fun validate(firstDate: Date?, lastDate: Date?, content: Content?, format: Format?): CloudSession {
            val _firstDate = firstDate ?: throw BAD_RANGE()
            val _lastDate = lastDate ?: throw BAD_RANGE()
            val _content = content ?: throw BAD_CONTENT()
            val _format = format ?: throw BAD_FORMAT()

            if (_firstDate > _lastDate) throw BAD_RANGE()

            return CloudSession(
                _firstDate,
                _lastDate,
                _content,
                _format,
                UUID.randomUUID().toString()
            )
        }
    }
}

@Parcelize
data class LocalSession(
    val firstDate: Date,
    val lastDate: Date,
    val id: String
) : Parcelable {
    companion object {
        @Suppress("LocalVariableName")
        fun validate(firstDate: Date?, lastDate: Date?): LocalSession {
            val _firstDate = firstDate ?: throw BAD_RANGE()
            val _lastDate = lastDate ?: throw BAD_RANGE()

            if (_firstDate > _lastDate) throw BAD_RANGE()

            return LocalSession(
                _firstDate,
                _lastDate,
                UUID.randomUUID().toString()
            )
        }
    }
}

enum class Status {
    UPLOADING,
    WAITING_DOWNLOAD,
    BUILDING_SPREADSHEET,
    COMPLETE,
    ERROR
}

enum class Type {
    CLOUD,
    LOCAL
}

data class Export (
    val id: String,
    val firstDate: Date,
    val lastDate: Date,
    val status: Status,
    val downloadLink: String,
    val type: Type
)

data class FinishedNotification(
    val exportId: String,
    val downloadUrl: String
)

data class Manifest (
    val firstDate: Date,
    val lastDate: Date,
    val content: CloudSession.Content,
    val format: CloudSession.Format,
    val id: String,
    val notificationToken: String
) {
    constructor(cloudSession: CloudSession, notificationToken: String) : this(
        cloudSession.firstDate,
        cloudSession.lastDate,
        cloudSession.content,
        cloudSession.format,
        cloudSession.id,
        notificationToken
    )
}

data class TextReceipt (
    val id: Long,
    val merchantName: String,
    val date: Date,
    val total: Float,
    val currency: Currency,
    val category: Category,
    @Relation(parentColumn = "id", entityColumn = "receiptId")
    val productEntities: List<ProductEntity>
)

data class ImageReceipt (
    val id: Long,
    val merchantName: String,
    val date: Date,
    val total: Float,
    val currency: Currency,
    val category: Category,
    val imagePath: String,
    @Relation(parentColumn = "id", entityColumn = "receiptId")
    val productEntities: List<ProductEntity>
)