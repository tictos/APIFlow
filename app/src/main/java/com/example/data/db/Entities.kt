package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.ApiKeyLocation
import com.example.data.model.AuthType
import com.example.data.model.BodyType
import com.example.data.model.HttpMethod

@Entity(tableName = "collections")
data class CollectionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val description: String = "",
    val colorHex: String = "#6366F1",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "saved_requests")
data class SavedRequestEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val collectionId: Long? = null,
    val name: String,
    val method: String = HttpMethod.GET.name,
    val url: String,
    val headersJson: String = "[]",
    val queryParamsJson: String = "[]",
    val authType: String = AuthType.NONE.name,
    val authToken: String = "",
    val authUser: String = "",
    val authPass: String = "",
    val apiKeyName: String = "X-API-Key",
    val apiKeyValue: String = "",
    val apiKeyLocation: String = ApiKeyLocation.HEADER.name,
    val bodyType: String = BodyType.NONE.name,
    val bodyContent: String = "",
    val formDataParamsJson: String = "[]",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "environments")
data class EnvironmentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val variablesJson: String = "[]",
    val isActive: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "history")
data class HistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val method: String,
    val url: String,
    val statusCode: Int,
    val statusMessage: String,
    val timeMs: Long,
    val responseSizeBytes: Long,
    val timestamp: Long = System.currentTimeMillis(),
    val requestStateJson: String
)
