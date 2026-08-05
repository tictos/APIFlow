package com.example.data.model

import androidx.compose.ui.graphics.Color

enum class HttpMethod(val color: Color, val isBodySupported: Boolean = true) {
    GET(Color(0xFF10B981), isBodySupported = false),
    POST(Color(0xFF3B82F6)),
    PUT(Color(0xFFF59E0B)),
    DELETE(Color(0xFFEF4444)),
    PATCH(Color(0xFF8B5CF6)),
    HEAD(Color(0xFF64748B), isBodySupported = false)
}

enum class AuthType(val label: String) {
    NONE("Aucune"),
    BEARER("Bearer Token"),
    BASIC("Basic Auth"),
    API_KEY("Clé API")
}

enum class ApiKeyLocation(val label: String) {
    HEADER("Header"),
    QUERY_PARAM("Paramètre d'URL")
}

enum class BodyType(val label: String, val mimeType: String) {
    NONE("Aucun", ""),
    JSON("JSON", "application/json"),
    FORM_DATA("Form Data", "multipart/form-data"),
    RAW_TEXT("Texte Brut", "text/plain"),
    XML("XML", "application/xml")
}

data class HeaderParam(
    val key: String = "",
    val value: String = "",
    val enabled: Boolean = true
)

data class QueryParam(
    val key: String = "",
    val value: String = "",
    val enabled: Boolean = true
)

data class FormDataParam(
    val key: String = "",
    val value: String = "",
    val enabled: Boolean = true
)

data class EnvironmentVariable(
    val key: String = "",
    val value: String = "",
    val enabled: Boolean = true
)

data class ApiRequestState(
    val id: Long = 0,
    val name: String = "Nouvelle requete",
    val collectionId: Long? = null,
    val method: HttpMethod = HttpMethod.GET,
    val url: String = "https://jsonplaceholder.typicode.com/posts/1",
    val headers: List<HeaderParam> = listOf(
        HeaderParam("Accept", "application/json", true),
        HeaderParam("User-Agent", "APIFlow-Mobile/1.0", true)
    ),
    val queryParams: List<QueryParam> = emptyList(),
    val authType: AuthType = AuthType.NONE,
    val authToken: String = "",
    val authUser: String = "",
    val authPass: String = "",
    val apiKeyName: String = "X-API-Key",
    val apiKeyValue: String = "",
    val apiKeyLocation: ApiKeyLocation = ApiKeyLocation.HEADER,
    val bodyType: BodyType = BodyType.NONE,
    val bodyContent: String = "",
    val formDataParams: List<FormDataParam> = emptyList()
)

data class ApiResponseResult(
    val statusCode: Int = 0,
    val statusMessage: String = "",
    val isSuccessful: Boolean = false,
    val timeMs: Long = 0,
    val sizeBytes: Long = 0,
    val contentType: String = "",
    val method: HttpMethod = HttpMethod.GET,
    val url: String = "",
    val headers: List<Pair<String, String>> = emptyList(),
    val body: String = "",
    val formattedBody: String = "",
    val isJson: Boolean = false,
    val isXml: Boolean = false,
    val isHtml: Boolean = false,
    val timestamp: Long = System.currentTimeMillis(),
    val errorMessage: String? = null
)

data class PresetApiSample(
    val name: String,
    val description: String,
    val method: HttpMethod,
    val url: String,
    val bodyType: BodyType = BodyType.NONE,
    val bodyContent: String = "",
    val headers: List<HeaderParam> = listOf(HeaderParam("Accept", "application/json", true))
)
