package com.lucianbc.receiptscan.infrastructure.dao

import androidx.room.TypeConverter
import com.lucianbc.receiptscan.domain.export.CloudSession
import com.lucianbc.receiptscan.domain.export.Status
import com.lucianbc.receiptscan.domain.export.Type
import com.lucianbc.receiptscan.domain.model.Category
import com.lucianbc.receiptscan.util.toDate
import java.lang.IllegalStateException
import java.util.*

class Converters {
    companion object {
        @TypeConverter
        @JvmStatic
        fun fromCurrency(currency: Currency?) = currency?.currencyCode

        @TypeConverter
        @JvmStatic
        fun toCurrency(currencyCode: String?): Currency? =
            if (currencyCode == null) null else Currency.getInstance(currencyCode)

        @TypeConverter
        @JvmStatic
        fun fromTimestamp(value: Long?) = value?.toDate()

        @TypeConverter
        @JvmStatic
        fun toTimestamp(value: Date?) = value?.time

        @TypeConverter
        @JvmStatic
        fun fromCategory(value: String?) =
            Category.values().firstOrNull { c -> c.name == value } ?: Category.Grocery

        @TypeConverter
        @JvmStatic
        fun toCategory(category: Category) = category.name

        @TypeConverter
        @JvmStatic
        fun toStatus(value: String?): Status = value?.let { Status.valueOf(it) }
            ?: throw IllegalStateException("Export status was null")

        @TypeConverter
        @JvmStatic
        fun fromStatus(status: Status) = status.name

        @TypeConverter
        @JvmStatic
        fun toContent(value: String?) = value?.let { CloudSession.Content.valueOf(it) }
            ?: throw IllegalStateException("Export session content was null")

        @TypeConverter
        @JvmStatic
        fun fromContent(content: CloudSession.Content) = content.name

        @TypeConverter
        @JvmStatic
        fun toFormat(value: String?) = value?.let { CloudSession.Format.valueOf(it) }
            ?: throw IllegalStateException("Export session content was null")

        @TypeConverter
        @JvmStatic
        fun fromFormat(format: CloudSession.Format) = format.name

        @TypeConverter
        @JvmStatic
        fun toType(value: String?) = value?.let { Type.valueOf(it) }
            ?: throw IllegalStateException("Export session type was null")

        @TypeConverter
        @JvmStatic
        fun fromType(type: Type) = type.name
    }
}