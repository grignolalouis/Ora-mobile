package com.ora.app.core.network

import android.util.Log
import android.webkit.CookieManager
import com.ora.app.core.error.ApiException
import com.ora.app.core.storage.TokenManager
import com.ora.app.data.remote.dto.response.BaseResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl

object HttpClientFactory {

    private val cookieJar = AndroidCookieJar()

    fun create(tokenManager: TokenManager): HttpClient = HttpClient(OkHttp) {
        engine {
            config {
                // LG: CookieJar pour persister le refresh token (HTTP-only cookie)
                cookieJar(cookieJar)
                // LG: Intercepteur pour refresh automatique sur 401
                addInterceptor(AuthInterceptor(tokenManager, cookieJar))
            }
        }

        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
                encodeDefaults = true
            })
        }

        install(HttpTimeout) {
            connectTimeoutMillis = ApiConfig.CONNECT_TIMEOUT
            requestTimeoutMillis = ApiConfig.READ_TIMEOUT
            socketTimeoutMillis = ApiConfig.READ_TIMEOUT
        }

        install(Logging) {
            logger = object : Logger {
                override fun log(message: String) {
                    Log.d("Ktor", message)
                }
            }
            level = LogLevel.BODY
        }

        // LG: Valider les réponses HTTP et lancer ApiException pour les erreurs
        HttpResponseValidator {
            validateResponse { response ->
                if (!response.status.isSuccess()) {
                    val errorBody = try {
                        response.body<BaseResponse>()
                    } catch (e: Exception) {
                        null
                    }
                    val errorMessage = errorBody?.message ?: "Request failed with status ${response.status.value}"
                    throw ApiException(response.status, errorMessage)
                }
            }
        }

        defaultRequest {
            url(ApiConfig.BASE_URL + "/" + ApiConfig.API_VERSION + "/")
            contentType(ContentType.Application.Json)
        }
    }
}

// LG: CookieJar qui utilise Android CookieManager pour persister les cookies
class AndroidCookieJar : CookieJar {
    private val cookieManager = CookieManager.getInstance()

    init {
        cookieManager.setAcceptCookie(true)
    }

    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        cookies.forEach { cookie ->
            cookieManager.setCookie(url.toString(), cookie.toString())
        }
    }

    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        val cookieHeader = cookieManager.getCookie(url.toString()) ?: return emptyList()
        return cookieHeader.split(";").mapNotNull { cookieStr ->
            Cookie.parse(url, cookieStr.trim())
        }
    }
}
