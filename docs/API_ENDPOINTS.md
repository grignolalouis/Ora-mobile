# Ora API Endpoints Documentation

This document describes all API endpoints used by the Ora Android application to interact with the backend.

## Overview

- **Base URL:** Configured via `BuildConfig.API_BASE_URL`
- **API Version:** `api/v1`
- **Full URL Pattern:** `{BASE_URL}/api/v1/{endpoint}`
- **HTTP Client:** Ktor HttpClient with OkHttp engine
- **Authentication:** Bearer token in Authorization header

---

## Table of Contents

1. [Authentication Endpoints](#1-authentication-endpoints)
2. [Agent Endpoints](#2-agent-endpoints)
3. [Session Endpoints](#3-session-endpoints)
4. [Message Endpoints](#4-message-endpoints)
5. [Streaming (SSE)](#5-streaming-sse)

---

## 1. Authentication Endpoints

All authentication endpoints are located in `AuthApiService.kt`.

### POST `/auth/login`

Authenticates a user with email and password.

**Authentication Required:** No

**Request Body:**
```json
{
  "email": "string",
  "password": "string"
}
```

**Response:**
```json
{
  "code": 200,
  "status": "success",
  "message": "Login successful",
  "user": {
    "id": "string",
    "email": "string",
    "name": "string",
    "profile_picture_url": "string | null",
    "created_at": "ISO 8601 datetime",
    "updated_at": "ISO 8601 datetime"
  },
  "tokens": {
    "access_token": "string",
    "expires_in": 3600
  }
}
```

**Notes:**
- On success, the access token is stored in encrypted SharedPreferences
- A refresh token is set as an HTTP-only cookie by the server

---

### POST `/auth/register`

Creates a new user account.

**Authentication Required:** No

**Request Body:**
```json
{
  "name": "string",
  "email": "string",
  "password": "string"
}
```

**Response:** Same as login response

---

### POST `/auth/logout`

Logs out the current user.

**Authentication Required:** Yes

**Request Body:** None

**Response:**
```json
{
  "code": 200,
  "status": "success",
  "message": "Logged out successfully"
}
```

**Notes:**
- Clears local tokens from storage
- Invalidates the refresh token cookie

---

### POST `/auth/refresh-tokens`

Refreshes the access token using the HTTP-only refresh token cookie.

**Authentication Required:** No (uses cookie)

**Request Body:** None

**Response:**
```json
{
  "code": 200,
  "status": "success",
  "message": "Token refreshed",
  "user": { ... },
  "tokens": {
    "access_token": "string",
    "expires_in": 3600
  }
}
```

**Notes:**
- Called automatically by `AuthInterceptor` when receiving a 401 response
- Uses synchronized block to prevent concurrent refresh attempts

---

### GET `/users/me`

Retrieves the current authenticated user's profile.

**Authentication Required:** Yes

**Response:**
```json
{
  "code": 200,
  "status": "success",
  "message": "User retrieved",
  "user": {
    "id": "string",
    "email": "string",
    "name": "string",
    "profile_picture_url": "string | null",
    "created_at": "ISO 8601 datetime",
    "updated_at": "ISO 8601 datetime"
  }
}
```

---

### DELETE `/users/me`

Deletes the current user's account.

**Authentication Required:** Yes

**Request Body:** None

**Response:**
```json
{
  "code": 200,
  "status": "success",
  "message": "Account deleted"
}
```

**Notes:**
- Clears all local tokens after successful deletion

---

### POST `/users/{userId}/profile-picture`

Uploads a profile picture for the user.

**Authentication Required:** Yes

**Content-Type:** `multipart/form-data`

**Request Body:**
| Field | Type | Description |
|-------|------|-------------|
| `file` | Binary | The image file |
| `fileName` | String | Original filename |
| `contentType` | String | MIME type (e.g., `image/jpeg`) |

**Response:**
```json
{
  "code": 200,
  "status": "success",
  "message": "Profile picture uploaded",
  "profile_picture_url": "string"
}
```

---

## 2. Agent Endpoints

Agent endpoints are located in `AgentApiService.kt`.

### GET `/agents`

Retrieves a list of all available AI agents.

**Authentication Required:** Yes

**Response:**
```json
{
  "code": 200,
  "status": "success",
  "message": "Agents retrieved",
  "agents": [
    {
      "type": "string",
      "name": "string",
      "description": "string",
      "greeting": "string | null",
      "version": "string",
      "capabilities": ["string"],
      "icon": "string"
    }
  ]
}
```

---

### GET `/agents/{agentType}`

Retrieves detailed information about a specific agent.

**Authentication Required:** Yes

**Path Parameters:**
| Parameter | Type | Description |
|-----------|------|-------------|
| `agentType` | String | The agent type identifier |

**Response:**
```json
{
  "code": 200,
  "status": "success",
  "message": "Agent retrieved",
  "agent": {
    "type": "string",
    "name": "string",
    "description": "string",
    "greeting": "string | null",
    "version": "string",
    "capabilities": ["string"],
    "icon": "string"
  }
}
```

---

## 3. Session Endpoints

Session endpoints are located in `AgentApiService.kt`.

### GET `/agents/{agentType}/sessions`

Retrieves all sessions for a specific agent.

**Authentication Required:** Yes

**Path Parameters:**
| Parameter | Type | Description |
|-----------|------|-------------|
| `agentType` | String | The agent type identifier |

**Response:**
```json
{
  "code": 200,
  "status": "success",
  "message": "Sessions retrieved",
  "sessions": [
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
  ]
}
```

---

### POST `/agents/{agentType}/sessions`

Creates a new chat session for an agent.

**Authentication Required:** Yes

**Path Parameters:**
| Parameter | Type | Description |
|-----------|------|-------------|
| `agentType` | String | The agent type identifier |

**Request Body:**
```json
{
  "title": "string | null",
  "metadata": {}
}
```

**Response:**
```json
{
  "code": 201,
  "status": "success",
  "message": "Session created",
  "session": {
    "session_id": "string",
    "user_id": "string",
    "agent_type": "string",
    "title": "string | null",
    "created_at": "ISO 8601 datetime",
    "updated_at": "ISO 8601 datetime",
    "message_count": 0,
    "metadata": {}
  }
}
```

---

### GET `/agents/{agentType}/sessions/{sessionId}`

Retrieves a session with its full message history.

**Authentication Required:** Yes

**Path Parameters:**
| Parameter | Type | Description |
|-----------|------|-------------|
| `agentType` | String | The agent type identifier |
| `sessionId` | String | The session identifier |

**Response:**
```json
{
  "code": 200,
  "status": "success",
  "message": "Session retrieved",
  "session": {
    "session_id": "string",
    "user_id": "string",
    "agent_type": "string",
    "title": "string | null",
    "created_at": "ISO 8601 datetime",
    "updated_at": "ISO 8601 datetime",
    "message_count": 5,
    "metadata": {},
    "summary": "string | null",
    "history": [
      {
        "role": "user | assistant | tool",
        "content": "string",
        "timestamp": "ISO 8601 datetime",
        "metadata": {},
        "tool_calls": [
          {
            "id": "string",
            "name": "string",
            "arguments": "JSON string"
          }
        ],
        "tool_id": "string | null",
        "tool_name": "string | null"
      }
    ]
  }
}
```

---

### DELETE `/agents/{agentType}/sessions/{sessionId}`

Deletes a chat session.

**Authentication Required:** Yes

**Path Parameters:**
| Parameter | Type | Description |
|-----------|------|-------------|
| `agentType` | String | The agent type identifier |
| `sessionId` | String | The session identifier |

**Response:**
```json
{
  "code": 200,
  "status": "success",
  "message": "Session deleted"
}
```

---

### PATCH `/agents/{agentType}/sessions/{sessionId}`

Renames a chat session.

**Authentication Required:** Yes

**Path Parameters:**
| Parameter | Type | Description |
|-----------|------|-------------|
| `agentType` | String | The agent type identifier |
| `sessionId` | String | The session identifier |

**Request Body:**
```json
{
  "title": "string"
}
```

**Response:**
```json
{
  "code": 200,
  "status": "success",
  "message": "Session renamed",
  "session": {
    "session_id": "string",
    "user_id": "string",
    "agent_type": "string",
    "title": "string",
    "created_at": "ISO 8601 datetime",
    "updated_at": "ISO 8601 datetime",
    "message_count": 5,
    "metadata": {}
  }
}
```

---

## 4. Message Endpoints

### POST `/agents/{agentType}/sessions/{sessionId}/messages`

Sends a message to the agent and initiates a streaming response.

**Authentication Required:** Yes

**Path Parameters:**
| Parameter | Type | Description |
|-----------|------|-------------|
| `agentType` | String | The agent type identifier |
| `sessionId` | String | The session identifier |

**Request Body:**
```json
{
  "message": "string"
}
```

**Response:**
```json
{
  "code": 200,
  "status": "success",
  "message": "Message sent",
  "stream": {
    "stream_id": "string",
    "session_id": "string",
    "message": "string"
  }
}
```

**Notes:**
- The `stream_id` is used to connect to the SSE endpoint for streaming the response
- The client should immediately connect to the SSE endpoint after receiving this response

---

## 5. Streaming (SSE)

The app uses Server-Sent Events (SSE) for real-time streaming of AI responses.

### SSE `/agents/{agentType}/stream/{streamId}`

Streams the AI agent's response in real-time.

**Authentication Required:** Yes (Bearer token in header)

**Path Parameters:**
| Parameter | Type | Description |
|-----------|------|-------------|
| `agentType` | String | The agent type identifier |
| `streamId` | String | The stream identifier from `sendMessage` response |

**Headers:**
```
Accept: text/event-stream
Cache-Control: no-cache
Authorization: Bearer {token}
```

**Connection Configuration:**
- Connect timeout: 30 seconds
- Read timeout: 5 minutes (300 seconds)
- Write timeout: 30 seconds

### SSE Event Types

The stream emits various event types:

#### `delta`
Text chunk from the AI response.
```json
{
  "type": "delta",
  "content": "string"
}
```

#### `reasoning`
Internal reasoning from the AI (if enabled).
```json
{
  "type": "reasoning",
  "content": "string"
}
```

#### `tool_call`
Tool invocation by the AI.
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

#### `tool_response`
Result from a tool execution.
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

#### `message_complete`
Final complete message.
```json
{
  "type": "message_complete",
  "message": {
    "role": "assistant",
    "content": "string",
    "tool_calls": []
  }
}
```

#### `error`
Error during streaming.
```json
{
  "type": "error",
  "error": {
    "code": "string",
    "message": "string"
  }
}
```

#### Control Events
- `thinking_start` - AI started thinking
- `thinking_end` - AI finished thinking
- `message_start` - Message generation started
- `message_end` - Message generation ended
- `preprocessing` - Request preprocessing
- `postprocessing` - Response postprocessing
- `done` - Stream completed successfully
- `close` - Stream closed
- `heartbeat` - Keep-alive ping

---

## Error Responses

All endpoints return errors in a consistent format:

```json
{
  "code": 400,
  "status": "error",
  "message": "Error description"
}
```

### Common HTTP Status Codes

| Code | Description |
|------|-------------|
| 200 | Success |
| 201 | Created |
| 400 | Bad Request |
| 401 | Unauthorized (triggers token refresh) |
| 403 | Forbidden |
| 404 | Not Found |
| 409 | Conflict |
| 429 | Rate Limited |
| 500 | Internal Server Error |

---

## Authentication Flow

```
1. User logs in via POST /auth/login
   └── Server returns access_token + sets refresh_token cookie

2. Client stores access_token in encrypted SharedPreferences

3. All subsequent requests include: Authorization: Bearer {access_token}

4. On 401 response:
   └── AuthInterceptor calls POST /auth/refresh-tokens (uses cookie)
       ├── Success: Retry original request with new token
       └── Failure: Emit SessionExpired, redirect to login
```

---

## Files Reference

| Component | File Path |
|-----------|-----------|
| Auth API Service | `data/remote/api/AuthApiService.kt` |
| Agent API Service | `data/remote/api/AgentApiService.kt` |
| SSE Client | `data/remote/sse/SSEClient.kt` |
| HTTP Client Factory | `core/network/HttpClientFactory.kt` |
| Auth Interceptor | `core/network/AuthInterceptor.kt` |
| API Configuration | `core/network/ApiConfig.kt` |
| Token Manager | `core/storage/TokenManager.kt` |
