package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
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
import com.example.data.network.ApiRepository
import com.example.data.network.HttpExecutor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AppNavigationTab {
    REQUEST_BUILDER,
    COLLECTIONS,
    ENVIRONMENTS,
    HISTORY
}

class ApiViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    val repository = ApiRepository(db)
    private val httpExecutor = HttpExecutor()

    // UI Tab Navigation
    private val _currentTab = MutableStateFlow(AppNavigationTab.REQUEST_BUILDER)
    val currentTab: StateFlow<AppNavigationTab> = _currentTab.asStateFlow()

    // Request State
    private val _requestState = MutableStateFlow(ApiRequestState())
    val requestState: StateFlow<ApiRequestState> = _requestState.asStateFlow()

    // Response State
    private val _responseResult = MutableStateFlow<ApiResponseResult?>(null)
    val responseResult: StateFlow<ApiResponseResult?> = _responseResult.asStateFlow()

    // Loading State
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Database Flows
    val collections: StateFlow<List<CollectionEntity>> = repository.collections
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val savedRequests: StateFlow<List<SavedRequestEntity>> = repository.savedRequests
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val environments: StateFlow<List<EnvironmentEntity>> = repository.environments
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeEnvironment: StateFlow<EnvironmentEntity?> = repository.activeEnvironment
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val history: StateFlow<List<HistoryEntity>> = repository.history
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            repository.seedDefaultsIfEmpty()
        }
    }

    fun selectTab(tab: AppNavigationTab) {
        _currentTab.value = tab
    }

    fun sendRequest() {
        if (_isLoading.value) return
        viewModelScope.launch {
            _isLoading.value = true
            _responseResult.value = null
            val result = repository.executeRequest(_requestState.value)
            _responseResult.value = result
            _isLoading.value = false
        }
    }

    fun updateMethod(method: HttpMethod) {
        _requestState.value = _requestState.value.copy(method = method)
    }

    fun updateUrl(url: String) {
        _requestState.value = _requestState.value.copy(url = url)
    }

    fun updateRequestName(name: String) {
        _requestState.value = _requestState.value.copy(name = name)
    }

    // Headers
    fun addHeader() {
        val current = _requestState.value.headers.toMutableList()
        current.add(HeaderParam("", "", true))
        _requestState.value = _requestState.value.copy(headers = current)
    }

    fun updateHeader(index: Int, key: String, value: String, enabled: Boolean) {
        val current = _requestState.value.headers.toMutableList()
        if (index in current.indices) {
            current[index] = HeaderParam(key, value, enabled)
            _requestState.value = _requestState.value.copy(headers = current)
        }
    }

    fun removeHeader(index: Int) {
        val current = _requestState.value.headers.toMutableList()
        if (index in current.indices) {
            current.removeAt(index)
            _requestState.value = _requestState.value.copy(headers = current)
        }
    }

    // Query Params
    fun addQueryParam() {
        val current = _requestState.value.queryParams.toMutableList()
        current.add(QueryParam("", "", true))
        _requestState.value = _requestState.value.copy(queryParams = current)
    }

    fun updateQueryParam(index: Int, key: String, value: String, enabled: Boolean) {
        val current = _requestState.value.queryParams.toMutableList()
        if (index in current.indices) {
            current[index] = QueryParam(key, value, enabled)
            _requestState.value = _requestState.value.copy(queryParams = current)
        }
    }

    fun removeQueryParam(index: Int) {
        val current = _requestState.value.queryParams.toMutableList()
        if (index in current.indices) {
            current.removeAt(index)
            _requestState.value = _requestState.value.copy(queryParams = current)
        }
    }

    // Auth
    fun updateAuthType(type: AuthType) {
        _requestState.value = _requestState.value.copy(authType = type)
    }

    fun updateAuthToken(token: String) {
        _requestState.value = _requestState.value.copy(authToken = token)
    }

    fun updateBasicAuth(user: String, pass: String) {
        _requestState.value = _requestState.value.copy(authUser = user, authPass = pass)
    }

    fun updateApiKey(name: String, value: String, location: ApiKeyLocation) {
        _requestState.value = _requestState.value.copy(apiKeyName = name, apiKeyValue = value, apiKeyLocation = location)
    }

    // Body
    fun updateBodyType(type: BodyType) {
        _requestState.value = _requestState.value.copy(bodyType = type)
    }

    fun updateBodyContent(content: String) {
        _requestState.value = _requestState.value.copy(bodyContent = content)
    }

    fun beautifyJsonBody() {
        val current = _requestState.value.bodyContent
        val formatted = httpExecutor.formatJson(current)
        _requestState.value = _requestState.value.copy(bodyContent = formatted)
    }

    // Form Data
    fun addFormDataParam() {
        val current = _requestState.value.formDataParams.toMutableList()
        current.add(FormDataParam("", "", true))
        _requestState.value = _requestState.value.copy(formDataParams = current)
    }

    fun updateFormDataParam(index: Int, key: String, value: String, enabled: Boolean) {
        val current = _requestState.value.formDataParams.toMutableList()
        if (index in current.indices) {
            current[index] = FormDataParam(key, value, enabled)
            _requestState.value = _requestState.value.copy(formDataParams = current)
        }
    }

    fun removeFormDataParam(index: Int) {
        val current = _requestState.value.formDataParams.toMutableList()
        if (index in current.indices) {
            current.removeAt(index)
            _requestState.value = _requestState.value.copy(formDataParams = current)
        }
    }

    // Loaders
    fun loadSavedRequest(entity: SavedRequestEntity) {
        val state = repository.parseRequestState(entity)
        _requestState.value = state
        _responseResult.value = null
        _currentTab.value = AppNavigationTab.REQUEST_BUILDER
    }

    fun loadPresetSample(sample: PresetApiSample) {
        _requestState.value = ApiRequestState(
            name = sample.name,
            method = sample.method,
            url = sample.url,
            headers = sample.headers,
            bodyType = sample.bodyType,
            bodyContent = sample.bodyContent
        )
        _responseResult.value = null
        _currentTab.value = AppNavigationTab.REQUEST_BUILDER
    }

    fun loadHistoryItem(item: HistoryEntity) {
        val state = repository.parseRequestStateFromJson(item.requestStateJson)
        if (state != null) {
            _requestState.value = state
            _responseResult.value = null
            _currentTab.value = AppNavigationTab.REQUEST_BUILDER
        }
    }

    // Save & DB Operations
    fun saveCurrentRequest(name: String, collectionId: Long?) {
        viewModelScope.launch {
            val newId = repository.saveRequest(_requestState.value, name, collectionId)
            _requestState.value = _requestState.value.copy(id = newId, name = name, collectionId = collectionId)
        }
    }

    fun deleteSavedRequest(id: Long) {
        viewModelScope.launch {
            repository.deleteRequest(id)
        }
    }

    fun createCollection(name: String, description: String, colorHex: String) {
        viewModelScope.launch {
            repository.saveCollection(name, description, colorHex)
        }
    }

    fun deleteCollection(id: Long) {
        viewModelScope.launch {
            repository.deleteCollection(id)
        }
    }

    // Environments
    fun saveEnvironment(id: Long = 0, name: String, variables: List<EnvironmentVariable>, isActive: Boolean = false) {
        viewModelScope.launch {
            repository.saveEnvironment(id, name, variables, isActive)
        }
    }

    fun setActiveEnvironment(id: Long) {
        viewModelScope.launch {
            repository.setActiveEnvironment(id)
        }
    }

    fun deleteEnvironment(id: Long) {
        viewModelScope.launch {
            repository.deleteEnvironment(id)
        }
    }

    // History
    fun clearHistory() {
        viewModelScope.launch {
            repository.clearHistory()
        }
    }

    fun deleteHistoryItem(id: Long) {
        viewModelScope.launch {
            repository.deleteHistoryItem(id)
        }
    }
}
