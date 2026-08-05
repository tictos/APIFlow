package com.example.data.network

// Re-using Moshi or simple JSON serialization for Room Entities
import android.content.Context
import com.example.data.db.AppDatabase
import com.example.data.db.CollectionEntity
import com.example.data.db.EnvironmentEntity
import com.example.data.db.HistoryEntity
import com.example.data.db.SavedRequestEntity
import com.example.data.model.ApiKeyLocation
import com.example.data.model.ApiRequestState
import com.example.data.model.ApiResponseResult
import com.example.data.model.AuthType
import com.example.data.model.BodyType
import com.example.data.model.EnvironmentVariable
import com.example.data.model.FormDataParam
import com.example.data.model.HeaderParam
import com.example.data.model.HttpMethod
import com.example.data.model.PresetApiSample
import com.example.data.model.QueryParam
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

class ApiRepository(private val db: AppDatabase) {

    private val httpExecutor = HttpExecutor()

    val collections: Flow<List<CollectionEntity>> = db.collectionDao().getAllCollections()
    val savedRequests: Flow<List<SavedRequestEntity>> = db.savedRequestDao().getAllSavedRequests()
    val environments: Flow<List<EnvironmentEntity>> = db.environmentDao().getAllEnvironments()
    val activeEnvironment: Flow<EnvironmentEntity?> = db.environmentDao().getActiveEnvironmentFlow()
    val history: Flow<List<HistoryEntity>> = db.historyDao().getRecentHistory()

    // Environment variable substitution (e.g. {{baseUrl}} -> https://jsonplaceholder.typicode.com)
    suspend fun executeRequest(requestState: ApiRequestState): ApiResponseResult = withContext(Dispatchers.IO) {
        val activeEnv = try {
            db.environmentDao().getActiveEnvironment()
        } catch (e: Exception) { null }
        val variables = parseVariablesJson(activeEnv?.variablesJson ?: "[]")

        val processedState = try {
            substituteVariables(requestState, variables)
        } catch (e: Exception) { requestState }

        val result = httpExecutor.execute(processedState)

        // Save into history safely
        try {
            val historyEntity = HistoryEntity(
                method = processedState.method.name,
                url = processedState.url,
                statusCode = result.statusCode,
                statusMessage = result.statusMessage,
                timeMs = result.timeMs,
                responseSizeBytes = result.sizeBytes,
                timestamp = System.currentTimeMillis(),
                requestStateJson = serializeRequestState(processedState)
            )
            db.historyDao().insertHistory(historyEntity)
        } catch (e: Exception) {
            // Prevent Room insertion error from breaking request response display
        }

        result
    }

    private fun substituteVariables(state: ApiRequestState, variables: List<EnvironmentVariable>): ApiRequestState {
        val enabledVars = variables.filter { it.enabled && it.key.isNotBlank() }
        if (enabledVars.isEmpty()) return state

        fun replace(input: String): String {
            var res = input
            enabledVars.forEach { varItem ->
                res = res.replace("{{${varItem.key.trim()}}}", varItem.value.trim())
            }
            return res
        }

        val newUrl = replace(state.url)
        val newHeaders = state.headers.map { it.copy(key = replace(it.key), value = replace(it.value)) }
        val newQueryParams = state.queryParams.map { it.copy(key = replace(it.key), value = replace(it.value)) }
        val newBodyContent = replace(state.bodyContent)
        val newAuthToken = replace(state.authToken)
        val newAuthUser = replace(state.authUser)
        val newAuthPass = replace(state.authPass)
        val newApiKeyValue = replace(state.apiKeyValue)

        return state.copy(
            url = newUrl,
            headers = newHeaders,
            queryParams = newQueryParams,
            bodyContent = newBodyContent,
            authToken = newAuthToken,
            authUser = newAuthUser,
            authPass = newAuthPass,
            apiKeyValue = newApiKeyValue
        )
    }

    // Collections Management
    suspend fun saveCollection(name: String, description: String, colorHex: String): Long {
        return db.collectionDao().insertCollection(
            CollectionEntity(name = name, description = description, colorHex = colorHex)
        )
    }

    suspend fun deleteCollection(collectionId: Long) {
        db.savedRequestDao().deleteRequestsByCollectionId(collectionId)
        db.collectionDao().deleteCollectionById(collectionId)
    }

    // Saved Requests Management
    suspend fun saveRequest(state: ApiRequestState, name: String, collectionId: Long?): Long {
        val entity = SavedRequestEntity(
            id = state.id,
            collectionId = collectionId,
            name = name,
            method = state.method.name,
            url = state.url,
            headersJson = serializeHeaders(state.headers),
            queryParamsJson = serializeQueryParams(state.queryParams),
            authType = state.authType.name,
            authToken = state.authToken,
            authUser = state.authUser,
            authPass = state.authPass,
            apiKeyName = state.apiKeyName,
            apiKeyValue = state.apiKeyValue,
            apiKeyLocation = state.apiKeyLocation.name,
            bodyType = state.bodyType.name,
            bodyContent = state.bodyContent,
            formDataParamsJson = serializeFormData(state.formDataParams),
            updatedAt = System.currentTimeMillis()
        )
        return db.savedRequestDao().insertRequest(entity)
    }

    suspend fun deleteRequest(id: Long) {
        db.savedRequestDao().deleteRequestById(id)
    }

    fun getRequestsForCollection(collectionId: Long): Flow<List<SavedRequestEntity>> {
        return db.savedRequestDao().getRequestsByCollection(collectionId)
    }

    // Environments Management
    suspend fun saveEnvironment(id: Long = 0, name: String, variables: List<EnvironmentVariable>, isActive: Boolean = false): Long {
        val entity = EnvironmentEntity(
            id = id,
            name = name,
            variablesJson = serializeVariables(variables),
            isActive = isActive
        )
        return db.environmentDao().insertEnvironment(entity)
    }

    suspend fun setActiveEnvironment(id: Long) {
        db.environmentDao().setActiveEnvironment(id)
    }

    suspend fun deleteEnvironment(id: Long) {
        db.environmentDao().deleteEnvironmentById(id)
    }

    // History Management
    suspend fun clearHistory() {
        db.historyDao().clearAllHistory()
    }

    suspend fun deleteHistoryItem(id: Long) {
        db.historyDao().deleteHistoryById(id)
    }

    // Pre-populate sample defaults on first run
    suspend fun seedDefaultsIfEmpty() = withContext(Dispatchers.IO) {
        val currentEnvs = db.environmentDao().getAllEnvironments().firstOrNull() ?: emptyList()
        if (currentEnvs.isEmpty()) {
            val devVars = listOf(
                EnvironmentVariable("baseUrl", "https://jsonplaceholder.typicode.com", true),
                EnvironmentVariable("apiToken", "sample_token_xyz123", true),
                EnvironmentVariable("userId", "1", true)
            )
            val defaultEnvId = saveEnvironment(name = "Développement", variables = devVars, isActive = true)

            saveEnvironment(
                name = "Production",
                variables = listOf(
                    EnvironmentVariable("baseUrl", "https://httpbin.org", true),
                    EnvironmentVariable("apiToken", "prod_token_889900", true)
                ),
                isActive = false
            )
        }

        val currentCollections = db.collectionDao().getAllCollections().firstOrNull() ?: emptyList()
        if (currentCollections.isEmpty()) {
            val colId = saveCollection("Exemples d'API", "Collection de requêtes pour débuter rapidement", "#6366F1")

            saveRequest(
                ApiRequestState(
                    name = "Obtenir les articles (GET)",
                    collectionId = colId,
                    method = HttpMethod.GET,
                    url = "{{baseUrl}}/posts/1"
                ),
                name = "Obtenir les articles (GET)",
                collectionId = colId
            )

            saveRequest(
                ApiRequestState(
                    name = "Créer un article (POST)",
                    collectionId = colId,
                    method = HttpMethod.POST,
                    url = "{{baseUrl}}/posts",
                    bodyType = BodyType.JSON,
                    bodyContent = """{
  "title": "Nouveau test APIFlow",
  "body": "Test d'envoi de requete POST depuis mobile",
  "userId": {{userId}}
}"""
                ),
                name = "Créer un article (POST)",
                collectionId = colId
            )

            saveRequest(
                ApiRequestState(
                    name = "Tester les En-têtes (HTTPBin)",
                    collectionId = colId,
                    method = HttpMethod.GET,
                    url = "https://httpbin.org/headers",
                    headers = listOf(
                        HeaderParam("Authorization", "Bearer {{apiToken}}", true),
                        HeaderParam("X-Custom-Header", "APIFlowMobile", true)
                    )
                ),
                name = "Tester les En-têtes (HTTPBin)",
                collectionId = colId
            )
        }
    }

    // Serialization Helpers
    fun parseRequestState(entity: SavedRequestEntity): ApiRequestState {
        return ApiRequestState(
            id = entity.id,
            name = entity.name,
            collectionId = entity.collectionId,
            method = try { HttpMethod.valueOf(entity.method) } catch (e: Exception) { HttpMethod.GET },
            url = entity.url,
            headers = parseHeadersJson(entity.headersJson),
            queryParams = parseQueryParamsJson(entity.queryParamsJson),
            authType = try { AuthType.valueOf(entity.authType) } catch (e: Exception) { AuthType.NONE },
            authToken = entity.authToken,
            authUser = entity.authUser,
            authPass = entity.authPass,
            apiKeyName = entity.apiKeyName,
            apiKeyValue = entity.apiKeyValue,
            apiKeyLocation = try { ApiKeyLocation.valueOf(entity.apiKeyLocation) } catch (e: Exception) { ApiKeyLocation.HEADER },
            bodyType = try { BodyType.valueOf(entity.bodyType) } catch (e: Exception) { BodyType.NONE },
            bodyContent = entity.bodyContent,
            formDataParams = parseFormDataJson(entity.formDataParamsJson)
        )
    }

    fun parseRequestStateFromJson(json: String): ApiRequestState? {
        return try {
            val obj = JSONObject(json)
            ApiRequestState(
                name = obj.optString("name", "Requete"),
                method = try { HttpMethod.valueOf(obj.optString("method", "GET")) } catch (e: Exception) { HttpMethod.GET },
                url = obj.optString("url", "https://jsonplaceholder.typicode.com/posts/1"),
                headers = parseHeadersJson(obj.optString("headersJson", "[]")),
                queryParams = parseQueryParamsJson(obj.optString("queryParamsJson", "[]")),
                authType = try { AuthType.valueOf(obj.optString("authType", "NONE")) } catch (e: Exception) { AuthType.NONE },
                authToken = obj.optString("authToken", ""),
                authUser = obj.optString("authUser", ""),
                authPass = obj.optString("authPass", ""),
                apiKeyName = obj.optString("apiKeyName", "X-API-Key"),
                apiKeyValue = obj.optString("apiKeyValue", ""),
                bodyType = try { BodyType.valueOf(obj.optString("bodyType", "NONE")) } catch (e: Exception) { BodyType.NONE },
                bodyContent = obj.optString("bodyContent", "")
            )
        } catch (e: Exception) {
            null
        }
    }

    private fun serializeRequestState(state: ApiRequestState): String {
        val obj = JSONObject()
        obj.put("name", state.name)
        obj.put("method", state.method.name)
        obj.put("url", state.url)
        obj.put("headersJson", serializeHeaders(state.headers))
        obj.put("queryParamsJson", serializeQueryParams(state.queryParams))
        obj.put("authType", state.authType.name)
        obj.put("authToken", state.authToken)
        obj.put("authUser", state.authUser)
        obj.put("authPass", state.authPass)
        obj.put("apiKeyName", state.apiKeyName)
        obj.put("apiKeyValue", state.apiKeyValue)
        obj.put("bodyType", state.bodyType.name)
        obj.put("bodyContent", state.bodyContent)
        return obj.toString()
    }

    fun serializeHeaders(headers: List<HeaderParam>): String {
        val array = JSONArray()
        headers.forEach {
            val obj = JSONObject()
            obj.put("key", it.key)
            obj.put("value", it.value)
            obj.put("enabled", it.enabled)
            array.put(obj)
        }
        return array.toString()
    }

    fun parseHeadersJson(json: String): List<HeaderParam> {
        val list = mutableListOf<HeaderParam>()
        try {
            val array = JSONArray(json)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(HeaderParam(obj.optString("key"), obj.optString("value"), obj.optBoolean("enabled", true)))
            }
        } catch (e: Exception) {}
        return list
    }

    fun serializeQueryParams(params: List<QueryParam>): String {
        val array = JSONArray()
        params.forEach {
            val obj = JSONObject()
            obj.put("key", it.key)
            obj.put("value", it.value)
            obj.put("enabled", it.enabled)
            array.put(obj)
        }
        return array.toString()
    }

    fun parseQueryParamsJson(json: String): List<QueryParam> {
        val list = mutableListOf<QueryParam>()
        try {
            val array = JSONArray(json)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(QueryParam(obj.optString("key"), obj.optString("value"), obj.optBoolean("enabled", true)))
            }
        } catch (e: Exception) {}
        return list
    }

    fun serializeFormData(params: List<FormDataParam>): String {
        val array = JSONArray()
        params.forEach {
            val obj = JSONObject()
            obj.put("key", it.key)
            obj.put("value", it.value)
            obj.put("enabled", it.enabled)
            array.put(obj)
        }
        return array.toString()
    }

    fun parseFormDataJson(json: String): List<FormDataParam> {
        val list = mutableListOf<FormDataParam>()
        try {
            val array = JSONArray(json)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(FormDataParam(obj.optString("key"), obj.optString("value"), obj.optBoolean("enabled", true)))
            }
        } catch (e: Exception) {}
        return list
    }

    fun serializeVariables(variables: List<EnvironmentVariable>): String {
        val array = JSONArray()
        variables.forEach {
            val obj = JSONObject()
            obj.put("key", it.key)
            obj.put("value", it.value)
            obj.put("enabled", it.enabled)
            array.put(obj)
        }
        return array.toString()
    }

    fun parseVariablesJson(json: String): List<EnvironmentVariable> {
        val list = mutableListOf<EnvironmentVariable>()
        try {
            val array = JSONArray(json)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(EnvironmentVariable(obj.optString("key"), obj.optString("value"), obj.optBoolean("enabled", true)))
            }
        } catch (e: Exception) {}
        return list
    }

    // Quick presets for test APIs
    fun getPresetApis(): List<PresetApiSample> {
        return listOf(
            PresetApiSample(
                name = "JSONPlaceholder - Get Post",
                description = "GET: Obtenir un article JSON de test",
                method = HttpMethod.GET,
                url = "https://jsonplaceholder.typicode.com/posts/1"
            ),
            PresetApiSample(
                name = "JSONPlaceholder - Create Post",
                description = "POST: Envoyez un payload JSON pour simuler une création",
                method = HttpMethod.POST,
                url = "https://jsonplaceholder.typicode.com/posts",
                bodyType = BodyType.JSON,
                bodyContent = """{
  "title": "Nouveau post APIFlow",
  "body": "Test de requete POST depuis mobile",
  "userId": 1
}"""
            ),
            PresetApiSample(
                name = "Cat Facts - Anecdote Chat",
                description = "GET: API ultra rapide renvoyant un fait aléatoire sur les chats",
                method = HttpMethod.GET,
                url = "https://catfact.ninja/fact"
            ),
            PresetApiSample(
                name = "Dog API - Image Aléatoire",
                description = "GET: Obtenir l'URL d'une image de chien en JSON",
                method = HttpMethod.GET,
                url = "https://dog.ceo/api/breeds/image/random"
            ),
            PresetApiSample(
                name = "IPify - Obtenir mon IP",
                description = "GET: Détecte votre adresse IP publique actuelle",
                method = HttpMethod.GET,
                url = "https://api.ipify.org?format=json"
            ),
            PresetApiSample(
                name = "GitHub - Citation Zen",
                description = "GET: Une pensée zen courte au format texte",
                method = HttpMethod.GET,
                url = "https://api.github.com/zen"
            )
        )
    }
}
