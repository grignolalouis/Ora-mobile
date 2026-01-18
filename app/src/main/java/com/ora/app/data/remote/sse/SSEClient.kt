package com.ora.app.data.remote.sse

import android.util.Log
import com.ora.app.core.network.ApiConfig
import com.ora.app.core.storage.TokenManager
import com.ora.app.data.mapper.SSEEventMapper
import com.ora.app.domain.model.StreamEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.sse.EventSource
import okhttp3.sse.EventSourceListener
import okhttp3.sse.EventSources
import java.io.Closeable
import java.util.concurrent.TimeUnit

class SSEClient(private val tokenManager: TokenManager) : Closeable {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(0, TimeUnit.SECONDS) // LG: No read timeout for SSE
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    fun connect(agentType: String, streamId: String): Flow<StreamEvent> = callbackFlow {
        val url = "${ApiConfig.BASE_URL}/${ApiConfig.API_VERSION}/agents/$agentType/stream/$streamId"
        Log.d("SSEClient", "Connecting to: $url")

        val request = Request.Builder()
            .url(url)
            .header("Accept", "text/event-stream")
            .header("Cache-Control", "no-cache")
            .apply {
                tokenManager.accessToken?.let { token ->
                    header("Authorization", "Bearer $token")
                }
            }
            .build()

        val eventSourceFactory = EventSources.createFactory(client)

        val listener = object : EventSourceListener() {
            override fun onOpen(eventSource: EventSource, response: Response) {
                Log.d("SSEClient", "Connection opened, status: ${response.code}")
            }

            override fun onEvent(
                eventSource: EventSource,
                id: String?,
                type: String?,
                data: String
            ) {
                Log.d("SSEClient", "Event received: type=$type, data=$data")
                val eventType = type ?: "message"
                val streamEvent = SSEEventMapper.map(eventType, data)
                trySend(streamEvent)

                // LG: Fermer la connexion uniquement sur Done, Close, ou Error
                if (streamEvent is StreamEvent.Done || streamEvent is StreamEvent.Close || streamEvent is StreamEvent.Error) {
                    eventSource.cancel()
                    close()
                }
            }

            override fun onClosed(eventSource: EventSource) {
                Log.d("SSEClient", "Connection closed")
                close()
            }

            override fun onFailure(eventSource: EventSource, t: Throwable?, response: Response?) {
                Log.e("SSEClient", "Connection failed: ${t?.message}, response: ${response?.code}")
                trySend(StreamEvent.Error(t?.message ?: "SSE connection failed", "connection_error"))
                close()
            }
        }

        val eventSource = eventSourceFactory.newEventSource(request, listener)

        awaitClose {
            Log.d("SSEClient", "Closing SSE connection")
            eventSource.cancel()
        }
    }

    override fun close() {
        client.dispatcher.executorService.shutdown()
        client.connectionPool.evictAll()
    }
}
