package com.ora.app.domain.model

data class Session(
    val id: String,
    val userId: String,
    val agentType: String,
    val title: String?,
    val createdAt: String,
    val updatedAt: String,
    val messageCount: Int,
    val metadata: Map<String, Any>?
)

data class Message(
    val role: String,
    val content: String,
    val timestamp: String,
    val metadata: Map<String, Any>?,
    // LG: Tool calls sur les messages assistant
    val toolCalls: List<ToolCallHistory>? = null,
    // LG: Pour les messages role='tool' (réponses d'outils)
    val toolId: String? = null,
    val toolName: String? = null
)

data class ToolCallHistory(
    val id: String,
    val name: String,
    val arguments: String
)

data class SessionDetail(
    val session: Session,
    val history: List<Message>
) {
    // LG: Convertir les messages user/assistant en Interactions avec tool calls
    val interactions: List<Interaction>
        get() {
            val result = mutableListOf<Interaction>()

            // LG: Construire une map des réponses de tools par tool_id
            val toolResponseMap = mutableMapOf<String, ToolResponseInfo>()
            for (msg in history) {
                if (msg.role == "tool" && msg.toolId != null) {
                    toolResponseMap[msg.toolId] = ToolResponseInfo(
                        content = msg.content,
                        toolName = msg.toolName
                    )
                }
            }

            var i = 0
            while (i < history.size) {
                val current = history[i]

                if (current.role == "user") {
                    // LG: Chercher la réponse assistant suivante (peut être après des messages tool)
                    var j = i + 1
                    var assistantMsg: Message? = null
                    val toolCallsForInteraction = mutableListOf<ToolCall>()

                    // LG: Parcourir jusqu'à trouver un message assistant ou un autre message user
                    while (j < history.size) {
                        val nextMsg = history[j]
                        when (nextMsg.role) {
                            "assistant" -> {
                                assistantMsg = nextMsg
                                // LG: Parser les tool_calls de ce message assistant
                                nextMsg.toolCalls?.forEach { tc ->
                                    val response = toolResponseMap[tc.id]
                                    val argsMap = try {
                                        kotlinx.serialization.json.Json.parseToJsonElement(tc.arguments)
                                            .let { it as? kotlinx.serialization.json.JsonObject }
                                            ?.mapValues { (_, v) -> v.toString().trim('"') }
                                            ?: mapOf("raw" to tc.arguments)
                                    } catch (_: Exception) {
                                        mapOf("raw" to tc.arguments)
                                    }
                                    toolCallsForInteraction.add(
                                        ToolCall(
                                            id = tc.id,
                                            name = tc.name,
                                            arguments = argsMap,
                                            status = if (response != null) ToolStatus.SUCCESS else ToolStatus.PENDING,
                                            result = response?.content
                                        )
                                    )
                                }
                                j++
                                break
                            }
                            "tool" -> {
                                // LG: Skip les messages tool, on les a déjà mappés
                                j++
                            }
                            "user" -> {
                                // LG: Nouveau message user = fin de cette interaction
                                break
                            }
                            else -> j++
                        }
                    }

                    result.add(
                        Interaction(
                            userMessage = current.content,
                            assistantResponse = assistantMsg?.content ?: "",
                            status = if (assistantMsg != null) InteractionStatus.COMPLETED else InteractionStatus.PENDING,
                            timestamp = current.timestamp,
                            toolCalls = toolCallsForInteraction
                        )
                    )

                    i = j
                } else {
                    // LG: Skip messages non-user au début
                    i++
                }
            }
            return result
        }
}

private data class ToolResponseInfo(
    val content: String,
    val toolName: String?
)
