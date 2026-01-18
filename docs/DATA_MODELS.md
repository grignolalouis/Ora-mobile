# Ora Data Models Documentation

This document describes all data transfer objects (DTOs) and domain models used in API communication.

## Response Wrapper

All API responses follow this base structure:

```json
{
  "code": 200,
  "status": "success | error",
  "message": "Human-readable message"
}
```

---

## User Models

### UserDto
```json
{
  "id": "string",
  "email": "string",
  "name": "string",
  "profile_picture_url": "string | null",
  "created_at": "ISO 8601 datetime",
  "updated_at": "ISO 8601 datetime"
}
```

### TokensDto
```json
{
  "access_token": "string",
  "expires_in": 3600
}
```

### AuthResponse
```json
{
  "code": 200,
  "status": "success",
  "message": "string",
  "user": UserDto,
  "tokens": TokensDto
}
```

### UserResponse
```json
{
  "code": 200,
  "status": "success",
  "message": "string",
  "user": UserDto
}
```

### ProfilePictureResponse
```json
{
  "code": 200,
  "status": "success",
  "message": "string",
  "profile_picture_url": "string"
}
```

---

## Agent Models

### AgentDto
```json
{
  "type": "string",
  "name": "string",
  "description": "string",
  "greeting": "string | null",
  "version": "string",
  "capabilities": ["string"],
  "icon": "string"
}
```

### AgentsListResponse
```json
{
  "code": 200,
  "status": "success",
  "message": "string",
  "agents": [AgentDto]
}
```

### AgentResponse
```json
{
  "code": 200,
  "status": "success",
  "message": "string",
  "agent": AgentDto
}
```

---

## Session Models

### SessionDto
```json
{
  "session_id": "string",
  "user_id": "string",
  "agent_type": "string",
  "title": "string | null",
  "created_at": "ISO 8601 datetime",
  "updated_at": "ISO 8601 datetime",
  "message_count": 0,
  "metadata": {}
}
```

### SessionDetailDto (extends SessionDto)
```json
{
  "session_id": "string",
  "user_id": "string",
  "agent_type": "string",
  "title": "string | null",
  "created_at": "ISO 8601 datetime",
  "updated_at": "ISO 8601 datetime",
  "message_count": 0,
  "metadata": {},
  "summary": "string | null",
  "history": [MessageDto]
}
```

### MessageDto
```json
{
  "role": "user | assistant | tool",
  "content": "string",
  "timestamp": "ISO 8601 datetime",
  "metadata": {},
  "tool_calls": [ToolCallHistoryDto],
  "tool_id": "string | null",
  "tool_name": "string | null"
}
```

### ToolCallHistoryDto
```json
{
  "id": "string",
  "name": "string",
  "arguments": "JSON string"
}
```

### SessionsListResponse
```json
{
  "code": 200,
  "status": "success",
  "message": "string",
  "sessions": [SessionDto]
}
```

### SessionResponse
```json
{
  "code": 200,
  "status": "success",
  "message": "string",
  "session": SessionDto
}
```

### SessionDetailResponse
```json
{
  "code": 200,
  "status": "success",
  "message": "string",
  "session": SessionDetailDto
}
```

---

## Streaming Models

### StreamInfoDto
```json
{
  "stream_id": "string",
  "session_id": "string",
  "message": "string"
}
```

### StreamResponse
```json
{
  "code": 200,
  "status": "success",
  "message": "string",
  "stream": StreamInfoDto
}
```

---

## SSE Event Models

### DeltaEvent
```json
{
  "type": "delta",
  "content": "string"
}
```

### ReasoningEvent
```json
{
  "type": "reasoning",
  "content": "string"
}
```

### ToolCallEvent
```json
{
  "type": "tool_call",
  "tool_call": {
    "id": "string",
    "name": "string",
    "arguments": "JSON string"
  }
}
```

### ToolResponseEvent
```json
{
  "type": "tool_response",
  "tool_response": {
    "tool_call_id": "string",
    "name": "string",
    "content": "string",
    "is_error": false
  }
}
```

### MessageCompleteEvent
```json
{
  "type": "message_complete",
  "message": {
    "role": "assistant",
    "content": "string",
    "tool_calls": [ToolCallData]
  }
}
```

### ErrorEvent
```json
{
  "type": "error",
  "error": {
    "code": "string",
    "message": "string"
  }
}
```

### Control Events
```json
{ "type": "thinking_start" }
{ "type": "thinking_end" }
{ "type": "message_start" }
{ "type": "message_end" }
{ "type": "preprocessing" }
{ "type": "postprocessing" }
{ "type": "done" }
{ "type": "close" }
{ "type": "heartbeat" }
```

---

## Request Models

### LoginRequest
```json
{
  "email": "string",
  "password": "string"
}
```

### RegisterRequest
```json
{
  "name": "string",
  "email": "string",
  "password": "string"
}
```

### CreateSessionRequest
```json
{
  "title": "string | null",
  "metadata": {}
}
```

### SendMessageRequest
```json
{
  "message": "string"
}
```

### RenameSessionRequest
```json
{
  "title": "string"
}
```

---

## Domain Models (Kotlin)

These are the app's internal domain models mapped from DTOs:

### User
```kotlin
data class User(
    val id: String,
    val email: String,
    val name: String,
    val profilePictureUrl: String?,
    val createdAt: String,
    val updatedAt: String
)
```

### Agent
```kotlin
data class Agent(
    val type: String,
    val name: String,
    val description: String,
    val greeting: String?,
    val version: String,
    val capabilities: List<String>,
    val icon: String
)
```

### Session
```kotlin
data class Session(
    val sessionId: String,
    val userId: String,
    val agentType: String,
    val title: String?,
    val createdAt: String,
    val updatedAt: String,
    val messageCount: Int,
    val summary: String?,
    val history: List<Interaction>
)
```

### Interaction
```kotlin
data class Interaction(
    val id: String,
    val userMessage: String,
    val assistantMessage: String,
    val timestamp: String,
    val status: InteractionStatus,
    val feedbackState: FeedbackState,
    val toolCalls: List<ToolCall>,
    val reasoning: String?
)

enum class InteractionStatus {
    PENDING, THINKING, STREAMING, COMPLETED, ERROR
}

enum class FeedbackState {
    NONE, POSITIVE, NEGATIVE
}
```

### ToolCall
```kotlin
data class ToolCall(
    val id: String,
    val name: String,
    val arguments: String,
    val status: ToolCallStatus,
    val result: String?,
    val error: String?
)

enum class ToolCallStatus {
    RUNNING, SUCCESS, ERROR
}
```

### StreamEvent (Sealed Class Hierarchy)
```kotlin
sealed class StreamEvent {
    data class Delta(val content: String) : StreamEvent()
    data class Reasoning(val content: String) : StreamEvent()
    data class ToolCallEvent(val toolCall: ToolCallData) : StreamEvent()
    data class ToolResponseEvent(val toolResponse: ToolResponseData) : StreamEvent()
    data class MessageComplete(val message: CompleteMessage) : StreamEvent()
    data class Error(val code: String, val message: String) : StreamEvent()

    // Control events
    object ThinkingStart : StreamEvent()
    object ThinkingEnd : StreamEvent()
    object MessageStart : StreamEvent()
    object MessageEnd : StreamEvent()
    object Preprocessing : StreamEvent()
    object Postprocessing : StreamEvent()
    object Done : StreamEvent()
    object Close : StreamEvent()
    object Heartbeat : StreamEvent()
}
```
