package com.example.data.network

import android.util.Base64
import com.example.data.model.ApiKeyLocation
import com.example.data.model.ApiRequestState
import com.example.data.model.AuthType
import com.example.data.model.BodyType
import com.example.data.model.HttpMethod

object CodeGenerator {

    fun generateCurl(state: ApiRequestState): String {
        val sb = StringBuilder("curl -X ${state.method.name} '${state.url}'")

        // Query params
        val enabledParams = state.queryParams.filter { it.enabled && it.key.isNotBlank() }
        if (enabledParams.isNotEmpty()) {
            val queryStr = enabledParams.joinToString("&") { "${it.key}=${it.value}" }
            if (!state.url.contains("?")) {
                sb.append("?").append(queryStr)
            } else {
                sb.append("&").append(queryStr)
            }
        }

        // Headers
        state.headers.filter { it.enabled && it.key.isNotBlank() }.forEach {
            sb.append(" \\\n  -H '${it.key}: ${it.value}'")
        }

        // Auth
        when (state.authType) {
            AuthType.BEARER -> if (state.authToken.isNotBlank()) {
                sb.append(" \\\n  -H 'Authorization: Bearer ${state.authToken}'")
            }
            AuthType.BASIC -> if (state.authUser.isNotBlank() || state.authPass.isNotBlank()) {
                val encoded = Base64.encodeToString("${state.authUser}:${state.authPass}".toByteArray(), Base64.NO_WRAP)
                sb.append(" \\\n  -H 'Authorization: Basic $encoded'")
            }
            AuthType.API_KEY -> if (state.apiKeyLocation == ApiKeyLocation.HEADER && state.apiKeyName.isNotBlank()) {
                sb.append(" \\\n  -H '${state.apiKeyName}: ${state.apiKeyValue}'")
            }
            AuthType.NONE -> {}
        }

        // Body
        if (state.method.isBodySupported) {
            when (state.bodyType) {
                BodyType.JSON, BodyType.RAW_TEXT, BodyType.XML -> {
                    if (state.bodyContent.isNotBlank()) {
                        sb.append(" \\\n  -H 'Content-Type: ${state.bodyType.mimeType}'")
                        val escaped = state.bodyContent.replace("'", "'\\''")
                        sb.append(" \\\n  --data-raw '$escaped'")
                    }
                }
                BodyType.FORM_DATA -> {
                    state.formDataParams.filter { it.enabled && it.key.isNotBlank() }.forEach {
                        sb.append(" \\\n  -F '${it.key}=${it.value}'")
                    }
                }
                BodyType.NONE -> {}
            }
        }

        return sb.toString()
    }

    fun generateKotlinOkHttp(state: ApiRequestState): String {
        val sb = StringBuilder()
        sb.append("val client = OkHttpClient()\n\n")

        if (state.method.isBodySupported && state.bodyType != BodyType.NONE) {
            when (state.bodyType) {
                BodyType.JSON -> {
                    sb.append("val mediaType = \"application/json; charset=utf-8\".toMediaType()\n")
                    val escaped = state.bodyContent.replace("\"", "\\\"").replace("\n", "\\n")
                    sb.append("val body = \"$escaped\".toRequestBody(mediaType)\n\n")
                }
                BodyType.RAW_TEXT, BodyType.XML -> {
                    sb.append("val mediaType = \"${state.bodyType.mimeType}\".toMediaType()\n")
                    val escaped = state.bodyContent.replace("\"", "\\\"").replace("\n", "\\n")
                    sb.append("val body = \"$escaped\".toRequestBody(mediaType)\n\n")
                }
                BodyType.FORM_DATA -> {
                    sb.append("val body = MultipartBody.Builder().setType(MultipartBody.FORM)\n")
                    state.formDataParams.filter { it.enabled && it.key.isNotBlank() }.forEach {
                        sb.append("    .addFormDataPart(\"${it.key}\", \"${it.value}\")\n")
                    }
                    sb.append("    .build()\n\n")
                }
                BodyType.NONE -> {}
            }
        }

        sb.append("val request = Request.Builder()\n")
        sb.append("    .url(\"${state.url}\")\n")

        state.headers.filter { it.enabled && it.key.isNotBlank() }.forEach {
            sb.append("    .addHeader(\"${it.key}\", \"${it.value}\")\n")
        }

        when (state.authType) {
            AuthType.BEARER -> if (state.authToken.isNotBlank()) {
                sb.append("    .addHeader(\"Authorization\", \"Bearer ${state.authToken}\")\n")
            }
            AuthType.BASIC -> {
                sb.append("    .addHeader(\"Authorization\", Credentials.basic(\"${state.authUser}\", \"${state.authPass}\"))\n")
            }
            AuthType.API_KEY -> if (state.apiKeyLocation == ApiKeyLocation.HEADER) {
                sb.append("    .addHeader(\"${state.apiKeyName}\", \"${state.apiKeyValue}\")\n")
            }
            AuthType.NONE -> {}
        }

        val bodyRef = if (state.method.isBodySupported && state.bodyType != BodyType.NONE) "body" else "EMPTY_REQUEST_BODY"
        when (state.method) {
            HttpMethod.GET -> sb.append("    .get()\n")
            HttpMethod.POST -> sb.append("    .post($bodyRef)\n")
            HttpMethod.PUT -> sb.append("    .put($bodyRef)\n")
            HttpMethod.DELETE -> sb.append(if (bodyRef == "body") "    .delete(body)\n" else "    .delete()\n")
            HttpMethod.PATCH -> sb.append("    .patch($bodyRef)\n")
            HttpMethod.HEAD -> sb.append("    .head()\n")
        }

        sb.append("    .build()\n\n")
        sb.append("val response = client.newCall(request).execute()")
        return sb.toString()
    }

    fun generateJavaScriptFetch(state: ApiRequestState): String {
        val sb = StringBuilder("const options = {\n")
        sb.append("  method: '${state.method.name}',\n")

        val headers = mutableMapOf<String, String>()
        state.headers.filter { it.enabled && it.key.isNotBlank() }.forEach {
            headers[it.key] = it.value
        }

        when (state.authType) {
            AuthType.BEARER -> if (state.authToken.isNotBlank()) {
                headers["Authorization"] = "Bearer ${state.authToken}"
            }
            AuthType.BASIC -> {
                val encoded = Base64.encodeToString("${state.authUser}:${state.authPass}".toByteArray(), Base64.NO_WRAP)
                headers["Authorization"] = "Basic $encoded"
            }
            AuthType.API_KEY -> if (state.apiKeyLocation == ApiKeyLocation.HEADER && state.apiKeyName.isNotBlank()) {
                headers[state.apiKeyName] = state.apiKeyValue
            }
            AuthType.NONE -> {}
        }

        if (state.method.isBodySupported && state.bodyType != BodyType.NONE && state.bodyType.mimeType.isNotBlank()) {
            headers["Content-Type"] = state.bodyType.mimeType
        }

        if (headers.isNotEmpty()) {
            sb.append("  headers: {\n")
            headers.forEach { (k, v) ->
                sb.append("    '$k': '$v',\n")
            }
            sb.append("  },\n")
        }

        if (state.method.isBodySupported) {
            when (state.bodyType) {
                BodyType.JSON -> {
                    val formattedBody = state.bodyContent.replace("\n", " ")
                    sb.append("  body: JSON.stringify($formattedBody)\n")
                }
                BodyType.RAW_TEXT, BodyType.XML -> {
                    val escaped = state.bodyContent.replace("'", "\\'").replace("\n", "\\n")
                    sb.append("  body: '$escaped'\n")
                }
                else -> {}
            }
        }

        sb.append("};\n\n")
        sb.append("fetch('${state.url}', options)\n")
        sb.append("  .then(res => res.json())\n")
        sb.append("  .then(data => console.log(data))\n")
        sb.append("  .catch(err => console.error(err));")

        return sb.toString()
    }

    fun generatePythonRequests(state: ApiRequestState): String {
        val sb = StringBuilder("import requests\n\n")
        sb.append("url = \"${state.url}\"\n\n")

        val headers = mutableMapOf<String, String>()
        state.headers.filter { it.enabled && it.key.isNotBlank() }.forEach {
            headers[it.key] = it.value
        }

        when (state.authType) {
            AuthType.BEARER -> if (state.authToken.isNotBlank()) {
                headers["Authorization"] = "Bearer ${state.authToken}"
            }
            AuthType.BASIC -> {
                val encoded = Base64.encodeToString("${state.authUser}:${state.authPass}".toByteArray(), Base64.NO_WRAP)
                headers["Authorization"] = "Basic $encoded"
            }
            AuthType.API_KEY -> if (state.apiKeyLocation == ApiKeyLocation.HEADER && state.apiKeyName.isNotBlank()) {
                headers[state.apiKeyName] = state.apiKeyValue
            }
            AuthType.NONE -> {}
        }

        if (headers.isNotEmpty()) {
            sb.append("headers = {\n")
            headers.forEach { (k, v) ->
                sb.append("    \"$k\": \"$v\",\n")
            }
            sb.append("}\n\n")
        } else {
            sb.append("headers = {}\n\n")
        }

        var payloadRef = ""
        if (state.method.isBodySupported) {
            when (state.bodyType) {
                BodyType.JSON -> {
                    val escaped = state.bodyContent.replace("\"", "\\\"").replace("\n", "\\n")
                    sb.append("payload = \"$escaped\"\n\n")
                    payloadRef = ", data=payload"
                }
                BodyType.RAW_TEXT, BodyType.XML -> {
                    val escaped = state.bodyContent.replace("\"", "\\\"").replace("\n", "\\n")
                    sb.append("payload = \"$escaped\"\n\n")
                    payloadRef = ", data=payload"
                }
                BodyType.FORM_DATA -> {
                    sb.append("files = [\n")
                    state.formDataParams.filter { it.enabled && it.key.isNotBlank() }.forEach {
                        sb.append("    ('${it.key}', (None, '${it.value}')),\n")
                    }
                    sb.append("]\n\n")
                    payloadRef = ", files=files"
                }
                BodyType.NONE -> {}
            }
        }

        val pyMethod = state.method.name.lowercase()
        sb.append("response = requests.request(\"${state.method.name}\", url, headers=headers$payloadRef)\n\n")
        sb.append("print(response.status_code)\n")
        sb.append("print(response.text)")

        return sb.toString()
    }
}
