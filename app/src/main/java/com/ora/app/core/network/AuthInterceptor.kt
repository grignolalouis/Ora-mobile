package com.ora.app.core.network

import android.util.Log
import com.ora.app.core.session.AuthEvent
import com.ora.app.core.session.AuthEventBus
import com.ora.app.core.storage.TokenManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import java.util.concurrent.atomic.AtomicBoolean
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import java.io.IOException

// LG: Intercepteur qui gère le refresh automatique des tokens sur 401
class AuthInterceptor(
    private val tokenManager: TokenManager,
    private val cookieJar: AndroidCookieJar
) : Interceptor {

    private val json = Json { ignoreUnknownKeys = true }

    private val isRefreshing = AtomicBoolean(false)

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        // LG: Skip auth pour les endpoints publics
        if (isPublicEndpoint(originalRequest.url.encodedPath)) {
            return chain.proceed(originalRequest)
        }

        // LG: Ajouter le token si disponible
        val requestWithAuth = originalRequest.newBuilder()
            .apply {
                tokenManager.accessToken?.let { token ->
                    header("Authorization", "Bearer $token")
                }
            }
            .build()

        val response = chain.proceed(requestWithAuth)

        // LG: Si 401, tenter le refresh
        if (response.code == 401 && isRefreshing.compareAndSet(false, true)) {
            response.close()

            val refreshed = tryRefreshToken(chain)
            isRefreshing.set(false)

            if (refreshed) {
                // LG: Retry avec le nouveau token
                val retryRequest = originalRequest.newBuilder()
                    .header("Authorization", "Bearer ${tokenManager.accessToken}")
                    .build()
                return chain.proceed(retryRequest)
            } else {
                // LG: Refresh échoué, session expirée
                Log.w("AuthInterceptor", "Token refresh failed, session expired")
                CoroutineScope(Dispatchers.IO).launch {
                    tokenManager.clear()
                    AuthEventBus.emit(AuthEvent.SessionExpired)
                }
            }
        }

        return response
    }

    private fun tryRefreshToken(chain: Interceptor.Chain): Boolean {
        return try {
            val refreshUrl = "${ApiConfig.BASE_URL}/${ApiConfig.API_VERSION}/auth/refresh-tokens"
            val refreshRequest = Request.Builder()
                .url(refreshUrl)
                .post("".toRequestBody("application/json".toMediaType()))
                .build()

            // LG: Utiliser un client temporaire avec le même CookieJar
            val tempClient = OkHttpClient.Builder()
                .cookieJar(cookieJar)
                .build()

            val refreshResponse = tempClient.newCall(refreshRequest).execute()

            if (refreshResponse.isSuccessful) {
                val body = refreshResponse.body?.string()
                if (body != null) {
                    val jsonElement = json.parseToJsonElement(body)
                    val tokens = jsonElement.jsonObject["tokens"]?.jsonObject
                    val access = tokens?.get("access")?.jsonObject
                    val newToken = access?.get("token")?.jsonPrimitive?.content

                    if (newToken != null) {
                        tokenManager.accessToken = newToken
                        Log.d("AuthInterceptor", "Token refreshed successfully")
                        return true
                    }
                }
            }
            false
        } catch (e: IOException) {
            Log.e("AuthInterceptor", "Token refresh failed: ${e.message}")
            false
        }
    }

    private fun isPublicEndpoint(path: String): Boolean {
        val publicPaths = listOf(
            "auth/login",
            "auth/register",
            "auth/forgot-password",
            "auth/reset-password",
            "auth/verify-email",
            "auth/refresh-tokens"
        )
        return publicPaths.any { path.contains(it) }
    }
}
