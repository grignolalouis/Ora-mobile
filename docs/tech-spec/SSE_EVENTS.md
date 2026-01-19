# Événements SSE (Server-Sent Events)

Documentation de tous les événements SSE gérés pour le streaming des réponses IA.

## Vue d'ensemble

Quand l'utilisateur envoie un message, le serveur répond via SSE (Server-Sent Events). C'est une connexion HTTP persistante où le serveur envoie des événements en temps réel.

## Flux de streaming

```
┌─────────┐         ┌─────────┐         ┌─────────┐
│  User   │         │  App    │         │ Server  │
└────┬────┘         └────┬────┘         └────┬────┘
     │                   │                   │
     │ Send message      │                   │
     │──────────────────▶│                   │
     │                   │ POST /message     │
     │                   │──────────────────▶│
     │                   │                   │
     │                   │ { streamId }      │
     │                   │◀──────────────────│
     │                   │                   │
     │                   │ GET /stream/{id}  │
     │                   │═══════════════════▶ (SSE connection)
     │                   │                   │
     │                   │ event: thinking_start
     │                   │◀══════════════════│
     │ "Thinking..."     │                   │
     │◀──────────────────│                   │
     │                   │                   │
     │                   │ event: delta      │
     │                   │ data: {"content":"Bonjour"}
     │                   │◀══════════════════│
     │ "Bonjour"         │                   │
     │◀──────────────────│                   │
     │                   │                   │
     │                   │ event: delta      │
     │                   │ data: {"content":" !"}
     │                   │◀══════════════════│
     │ "Bonjour !"       │                   │
     │◀──────────────────│                   │
     │                   │                   │
     │                   │ event: done       │
     │                   │◀══════════════════│
     │                   │                   │
     │                   │ (connection close)│
     │                   │══════════════════X│
```

## Types d'événements

### Événements de contenu

#### `delta`
Chunk de texte incrémental pendant le streaming.

```json
{
  "content": "texte partiel",
  "accumulated": "texte complet jusqu'ici"
}
```

**Usage UI** : Append le contenu à la réponse en cours, scroll vers le bas.

---

#### `reasoning`
Contenu du raisonnement interne de l'IA (extended thinking).

```json
{
  "reasoning": "texte de réflexion",
  "accumulated": "réflexion complète"
}
```

**Usage UI** : Stocké mais peut être affiché dans une section collapsible.

---

#### `message`
Message final avec métadonnées.

```json
{
  "id": "msg_abc123",
  "content": "Réponse complète de l'assistant.",
  "usage": {
    "input_tokens": 150,
    "output_tokens": 250
  }
}
```

**Usage UI** : Marque l'interaction comme `COMPLETED`, recharge les sessions.

---

#### `error`
Erreur pendant le streaming.

```json
{
  "error": "Message d'erreur",
  "code": "RATE_LIMIT"
}
```

**Usage UI** : Marque l'interaction comme `ERROR`, affiche un toast.

### Événements de cycle de vie

| Événement | Données | Description | Action UI |
|-----------|---------|-------------|-----------|
| `thinking_start` | - | L'IA commence à réfléchir | Affiche "Thinking..." |
| `thinking_end` | - | Fin de la réflexion | Change en "Streaming..." |
| `message_start` | - | Début du message | - |
| `message_end` | - | Fin du message | - |
| `preprocessing` | - | Pré-traitement serveur | - |
| `postprocessing` | - | Post-traitement serveur | - |
| `done` | - | Stream terminé | Cleanup |
| `close` | - | Connexion fermée | Cleanup |
| `heartbeat` | - | Keep-alive | - |

### Événements d'outils

#### `tool_call`
L'IA demande l'exécution d'un outil.

```json
{
  "tool_calls": [
    {
      "id": "call_xyz789",
      "type": "function",
      "function": {
        "name": "get_weather",
        "arguments": "{\"location\": \"Paris\", \"unit\": \"celsius\"}"
      }
    }
  ]
}
```

**Usage UI** : Affiche une carte "Tool: get_weather" avec status `RUNNING`.

---

#### `tool_response`
Résultat de l'exécution d'un outil.

```json
{
  "tool_responses": [
    {
      "tool_id": "call_xyz789",
      "content": "Température à Paris: 18°C, ensoleillé",
      "error": null
    }
  ]
}
```

**Usage UI** : Met à jour la carte avec le résultat, status `SUCCESS` ou `ERROR`.

## Mapping des événements

Fichier : `data/mapper/SSEEventMapper.kt`

```kotlin
fun map(eventType: String, data: String): StreamEvent = when {
    data.isBlank() -> mapEmptyEvent(eventType)
    else -> mapJsonEvent(eventType, parseJson(data))
}

private fun mapEmptyEvent(eventType: String) = when (eventType) {
    "thinking_start", "reasoning_start" -> StreamEvent.ThinkingStart
    "thinking_end", "reasoning_end" -> StreamEvent.ThinkingEnd
    "message_start" -> StreamEvent.MessageStart
    "message_end" -> StreamEvent.MessageEnd
    "preprocessing" -> StreamEvent.Preprocessing
    "postprocessing" -> StreamEvent.Postprocessing
    "done" -> StreamEvent.Done
    "close" -> StreamEvent.Close
    "heartbeat" -> StreamEvent.Heartbeat
    else -> StreamEvent.Unknown(eventType)
}
```

## Gestion dans ChatViewModel

Fichier : `presentation/features/chat/ChatViewModel.kt`

```kotlin
private suspend fun handleStreamEvent(event: StreamEvent) {
    when (event) {
        is StreamEvent.Delta -> {
            setState { copy(streamingContent = streamingContent + event.content) }
            updateLastInteractionResponse(currentState.streamingContent)
            sendEffect(ChatEffect.ScrollToBottom)
        }

        is StreamEvent.MessageComplete -> {
            updateLastInteractionStatus(InteractionStatus.COMPLETED)
            setState { copy(isStreaming = false, streamingContent = "") }
            loadSessions() // Refresh message counts
        }

        is StreamEvent.Error -> {
            updateLastInteractionStatus(InteractionStatus.ERROR)
            setState { copy(isStreaming = false, error = event.message) }
        }

        is StreamEvent.ThinkingStart -> {
            updateLastInteractionStatus(InteractionStatus.THINKING)
        }

        is StreamEvent.ThinkingEnd -> {
            updateLastInteractionStatus(InteractionStatus.STREAMING)
        }

        is StreamEvent.ToolCallEvent -> {
            val toolCalls = event.toolCalls.map { /* convert to ToolCall */ }
            addToolCallsToLastInteraction(toolCalls)
        }

        is StreamEvent.ToolResponseEvent -> {
            event.responses.forEach { response ->
                updateToolCallResult(response.toolId, response.content, response.error)
            }
        }

        else -> { /* Heartbeat, preprocessing, etc. - ignored */ }
    }
}
```

## États d'une Interaction

```kotlin
enum class InteractionStatus {
    PENDING,    // Message envoyé, attente réponse
    THINKING,   // IA réfléchit (thinking_start reçu)
    STREAMING,  // Réponse en cours (delta reçus)
    COMPLETED,  // Terminé (message reçu)
    ERROR       // Erreur (error reçu)
}
```

## États d'un ToolCall

```kotlin
enum class ToolStatus {
    PENDING,    // En attente
    RUNNING,    // Exécution en cours
    SUCCESS,    // Succès (response sans error)
    ERROR       // Échec (response avec error)
}
```

## Séquence type complète

```
1. message_start
2. thinking_start
3. reasoning (multiple)
4. thinking_end
5. message_start
6. delta (multiple) → texte streamé
7. tool_call → outil demandé
8. tool_response → résultat outil
9. delta (multiple) → suite du texte
10. message → message final avec usage
11. message_end
12. done
13. close
```

## Gestion des erreurs

| Erreur | Comportement |
|--------|--------------|
| Parse JSON | Retourne `StreamEvent.Error("Parse error: ...")` |
| Connexion perdue | `SSEClient` ferme, ViewModel reset streaming |
| Event inconnu | Retourne `StreamEvent.Unknown(type)`, ignoré |
| Timeout | Heartbeat garde la connexion alive |
