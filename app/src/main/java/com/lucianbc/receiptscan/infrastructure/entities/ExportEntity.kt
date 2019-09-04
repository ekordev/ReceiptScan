package com.lucianbc.receiptscan.infrastructure.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.lucianbc.receiptscan.domain.export.CloudSession
import com.lucianbc.receiptscan.domain.export.Status
import com.lucianbc.receiptscan.domain.export.Type
import java.util.*

@Entity(
    tableName = "export"
)
data class ExportEntity(
    @PrimaryKey(autoGenerate = false)
    val id: String,
    val firstDate: Date,
    val lastDate: Date,
    val content: CloudSession.Content,
    val format: CloudSession.Format,
    val status: Status,
    val downloadLink: String,
    val type: Type,
    val creationTimestamp: Date
)