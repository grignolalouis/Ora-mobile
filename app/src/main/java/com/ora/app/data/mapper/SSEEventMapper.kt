package com.ora.app.data.mapper

import com.ora.app.domain.model.StreamEvent
import com.ora.app.domain.model.ToolCallData
import com.ora.app.domain.model.ToolResponseData
import com.ora.app.domain.model.Usage
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.int
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

object SSEEventMapper {

    private val json = Json { ignoreUnknownKeys = true }

    fun map(eventType: String, data: String): StreamEvent {
        if (data.isBlank()) {
            return mapEmptyEvent(eventType)
        }

        return try {
            val jsonData = json.parseToJsonElement(data).jsonObject
            mapJsonEvent(eventType, jsonData)
        } catch (e: Exception) {
            StreamEvent.Error("Parse error: ${e.message}", "parse_error")
        }
    }

    private fun mapEmptyEvent(eventType: String): StreamEvent = when (eventType) {
        "reasoning_start", "thinking_start" -> StreamEvent.ThinkingStart
        "reasoning_end", "thinking_end" -> StreamEvent.ThinkingEnd
        "message_start" -> StreamEvent.MessageStart
        "message_end" -> StreamEvent.MessageEnd
        "preprocessing" -> StreamEvent.Preprocessing
        "postprocessing" -> StreamEvent.Postprocessing
        "done" -> StreamEvent.Done
        "close" -> StreamEvent.Close
        "heartbeat" -> StreamEvent.Heartbeat
        else -> StreamEvent.Unknown(eventType)
    }

    private fun mapJsonEvent(eventType: String, data: JsonObject): StreamEvent = when (eventType) {
        "delta" -> StreamEvent.Delta(
            content = data["content"]?.jsonPrimitive?.content ?: "",
            accumulated = data["accumulated"]?.jsonPrimitive?.contentOrNull
        )

        "reasoning" -> StreamEvent.Reasoning(
            reasoning = data["reasoning"]?.jsonPrimitive?.content ?: "",
            accumulated = data["accumulated"]?.jsonPrimitive?.contentOrNull
        )

        "tool_call" -> {
            val toolCalls = data["tool_calls"]?.jsonArray?.map { tc ->
                val obj = tc.jsonObject
                val func = obj["function"]?.jsonObject
                ToolCallData(
                    id = obj["id"]?.jsonPrimitive?.content ?: "",
                    type = obj["type"]?.jsonPrimitive?.content ?: "function",
                    functionName = func?.get("name")?.jsonPrimitive?.content ?: "",
                    arguments = func?.get("arguments")?.jsonPrimitive?.content ?: "{}"
                )
            } ?: emptyList()
            StreamEvent.ToolCallEvent(toolCalls)
        }

        "tool_response" -> {
            val responses = data["tool_responses"]?.jsonArray?.map { tr ->
                val obj = tr.jsonObject
                ToolResponseData(
                    toolId = obj["tool_id"]?.jsonPrimitive?.content ?: "",
                    content = obj["content"]?.jsonPrimitive?.content ?: "",
                    error = obj["error"]?.jsonPrimitive?.contentOrNull
                )
            } ?: emptyList()
            StreamEvent.ToolResponseEvent(responses)
        }

        "message" -> {
            val usage = data["usage"]?.jsonObject?.let {
                Usage(
                    inputTokens = it["input_tokens"]?.jsonPrimitive?.intOrNull ?: 0,
                    outputTokens = it["output_tokens"]?.jsonPrimitive?.intOrNull ?: 0
                )
            }
            StreamEvent.MessageComplete(
                id = data["id"]?.jsonPrimitive?.content ?: "",
                content = data["content"]?.jsonPrimitive?.content ?: "",
                usage = usage
            )
        }

        "error" -> StreamEvent.Error(
            message = data["error"]?.jsonPrimitive?.content ?: "Unknown error",
            code = data["code"]?.jsonPrimitive?.contentOrNull
        )

        "preprocessing" -> StreamEvent.Preprocessing
        "postprocessing" -> StreamEvent.Postprocessing
        "done" -> StreamEvent.Done
        "close" -> StreamEvent.Close
        "heartbeat" -> StreamEvent.Heartbeat

        else -> StreamEvent.Unknown(eventType)
    }
}
