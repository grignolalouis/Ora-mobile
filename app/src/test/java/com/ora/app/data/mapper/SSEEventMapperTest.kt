package com.ora.app.data.mapper

import com.google.common.truth.Truth.assertThat
import com.ora.app.domain.model.StreamEvent
import org.junit.Test

class SSEEventMapperTest {

    @Test
    fun `delta event with content and accumulated`() {
        val data = """{"content": "Hello", "accumulated": "Hello World"}"""
        val result = SSEEventMapper.map("delta", data)

        assertThat(result).isInstanceOf(StreamEvent.Delta::class.java)
        val delta = result as StreamEvent.Delta
        assertThat(delta.content).isEqualTo("Hello")
        assertThat(delta.accumulated).isEqualTo("Hello World")
    }

    @Test
    fun `delta event with missing fields defaults`() {
        val data = """{}"""
        val result = SSEEventMapper.map("delta", data)

        assertThat(result).isInstanceOf(StreamEvent.Delta::class.java)
        val delta = result as StreamEvent.Delta
        assertThat(delta.content).isEmpty()
        assertThat(delta.accumulated).isNull()
    }

    @Test
    fun `delta event with only content`() {
        val data = """{"content": "Test"}"""
        val result = SSEEventMapper.map("delta", data)

        assertThat(result).isInstanceOf(StreamEvent.Delta::class.java)
        val delta = result as StreamEvent.Delta
        assertThat(delta.content).isEqualTo("Test")
        assertThat(delta.accumulated).isNull()
    }

    @Test
    fun `reasoning event parsing`() {
        val data = """{"reasoning": "Let me think...", "accumulated": "Let me think... about this"}"""
        val result = SSEEventMapper.map("reasoning", data)

        assertThat(result).isInstanceOf(StreamEvent.Reasoning::class.java)
        val reasoning = result as StreamEvent.Reasoning
        assertThat(reasoning.reasoning).isEqualTo("Let me think...")
        assertThat(reasoning.accumulated).isEqualTo("Let me think... about this")
    }

    @Test
    fun `reasoning event with missing fields`() {
        val data = """{}"""
        val result = SSEEventMapper.map("reasoning", data)

        assertThat(result).isInstanceOf(StreamEvent.Reasoning::class.java)
        val reasoning = result as StreamEvent.Reasoning
        assertThat(reasoning.reasoning).isEmpty()
        assertThat(reasoning.accumulated).isNull()
    }

    @Test
    fun `tool_call event with function details`() {
        val data = """{
            "tool_calls": [
                {
                    "id": "call_123",
                    "type": "function",
                    "function": {
                        "name": "get_weather",
                        "arguments": "{\"city\": \"Paris\"}"
                    }
                }
            ]
        }"""
        val result = SSEEventMapper.map("tool_call", data)

        assertThat(result).isInstanceOf(StreamEvent.ToolCallEvent::class.java)
        val toolCall = result as StreamEvent.ToolCallEvent
        assertThat(toolCall.toolCalls).hasSize(1)
        assertThat(toolCall.toolCalls[0].id).isEqualTo("call_123")
        assertThat(toolCall.toolCalls[0].type).isEqualTo("function")
        assertThat(toolCall.toolCalls[0].functionName).isEqualTo("get_weather")
        assertThat(toolCall.toolCalls[0].arguments).isEqualTo("{\"city\": \"Paris\"}")
    }

    @Test
    fun `tool_call event with multiple tool calls`() {
        val data = """{
            "tool_calls": [
                {
                    "id": "call_1",
                    "type": "function",
                    "function": {"name": "tool_a", "arguments": "{}"}
                },
                {
                    "id": "call_2",
                    "type": "function",
                    "function": {"name": "tool_b", "arguments": "{}"}
                }
            ]
        }"""
        val result = SSEEventMapper.map("tool_call", data)

        assertThat(result).isInstanceOf(StreamEvent.ToolCallEvent::class.java)
        val toolCall = result as StreamEvent.ToolCallEvent
        assertThat(toolCall.toolCalls).hasSize(2)
        assertThat(toolCall.toolCalls[0].functionName).isEqualTo("tool_a")
        assertThat(toolCall.toolCalls[1].functionName).isEqualTo("tool_b")
    }

    @Test
    fun `tool_call event with missing function defaults`() {
        val data = """{
            "tool_calls": [
                {
                    "id": "call_123"
                }
            ]
        }"""
        val result = SSEEventMapper.map("tool_call", data)

        assertThat(result).isInstanceOf(StreamEvent.ToolCallEvent::class.java)
        val toolCall = result as StreamEvent.ToolCallEvent
        assertThat(toolCall.toolCalls).hasSize(1)
        assertThat(toolCall.toolCalls[0].type).isEqualTo("function")
        assertThat(toolCall.toolCalls[0].functionName).isEmpty()
        assertThat(toolCall.toolCalls[0].arguments).isEqualTo("{}")
    }

    @Test
    fun `tool_call event with empty tool_calls array`() {
        val data = """{"tool_calls": []}"""
        val result = SSEEventMapper.map("tool_call", data)

        assertThat(result).isInstanceOf(StreamEvent.ToolCallEvent::class.java)
        val toolCall = result as StreamEvent.ToolCallEvent
        assertThat(toolCall.toolCalls).isEmpty()
    }

    @Test
    fun `tool_call event without tool_calls field`() {
        val data = """{}"""
        val result = SSEEventMapper.map("tool_call", data)

        assertThat(result).isInstanceOf(StreamEvent.ToolCallEvent::class.java)
        val toolCall = result as StreamEvent.ToolCallEvent
        assertThat(toolCall.toolCalls).isEmpty()
    }

    @Test
    fun `tool_response event with result`() {
        val data = """{
            "tool_responses": [
                {
                    "tool_id": "call_123",
                    "content": "Weather in Paris: 20C"
                }
            ]
        }"""
        val result = SSEEventMapper.map("tool_response", data)

        assertThat(result).isInstanceOf(StreamEvent.ToolResponseEvent::class.java)
        val toolResponse = result as StreamEvent.ToolResponseEvent
        assertThat(toolResponse.responses).hasSize(1)
        assertThat(toolResponse.responses[0].toolId).isEqualTo("call_123")
        assertThat(toolResponse.responses[0].content).isEqualTo("Weather in Paris: 20C")
        assertThat(toolResponse.responses[0].error).isNull()
    }

    @Test
    fun `tool_response event with error`() {
        val data = """{
            "tool_responses": [
                {
                    "tool_id": "call_123",
                    "content": "",
                    "error": "Tool execution failed"
                }
            ]
        }"""
        val result = SSEEventMapper.map("tool_response", data)

        assertThat(result).isInstanceOf(StreamEvent.ToolResponseEvent::class.java)
        val toolResponse = result as StreamEvent.ToolResponseEvent
        assertThat(toolResponse.responses[0].error).isEqualTo("Tool execution failed")
    }

    @Test
    fun `tool_response event with multiple responses`() {
        val data = """{
            "tool_responses": [
                {"tool_id": "call_1", "content": "Result 1"},
                {"tool_id": "call_2", "content": "Result 2"}
            ]
        }"""
        val result = SSEEventMapper.map("tool_response", data)

        assertThat(result).isInstanceOf(StreamEvent.ToolResponseEvent::class.java)
        val toolResponse = result as StreamEvent.ToolResponseEvent
        assertThat(toolResponse.responses).hasSize(2)
    }

    @Test
    fun `message_complete event with usage stats`() {
        val data = """{
            "id": "msg_123",
            "content": "Final response",
            "usage": {
                "input_tokens": 100,
                "output_tokens": 50
            }
        }"""
        val result = SSEEventMapper.map("message", data)

        assertThat(result).isInstanceOf(StreamEvent.MessageComplete::class.java)
        val msg = result as StreamEvent.MessageComplete
        assertThat(msg.id).isEqualTo("msg_123")
        assertThat(msg.content).isEqualTo("Final response")
        assertThat(msg.usage).isNotNull()
        assertThat(msg.usage?.inputTokens).isEqualTo(100)
        assertThat(msg.usage?.outputTokens).isEqualTo(50)
    }

    @Test
    fun `message_complete with null usage`() {
        val data = """{
            "id": "msg_123",
            "content": "Final response"
        }"""
        val result = SSEEventMapper.map("message", data)

        assertThat(result).isInstanceOf(StreamEvent.MessageComplete::class.java)
        val msg = result as StreamEvent.MessageComplete
        assertThat(msg.usage).isNull()
    }

    @Test
    fun `message_complete with partial usage`() {
        val data = """{
            "id": "msg_123",
            "content": "Response",
            "usage": {"input_tokens": 50}
        }"""
        val result = SSEEventMapper.map("message", data)

        assertThat(result).isInstanceOf(StreamEvent.MessageComplete::class.java)
        val msg = result as StreamEvent.MessageComplete
        assertThat(msg.usage?.inputTokens).isEqualTo(50)
        assertThat(msg.usage?.outputTokens).isEqualTo(0)
    }

    @Test
    fun `error event with code and message`() {
        val data = """{"error": "Rate limit exceeded", "code": "rate_limit"}"""
        val result = SSEEventMapper.map("error", data)

        assertThat(result).isInstanceOf(StreamEvent.Error::class.java)
        val error = result as StreamEvent.Error
        assertThat(error.message).isEqualTo("Rate limit exceeded")
        assertThat(error.code).isEqualTo("rate_limit")
    }

    @Test
    fun `error event without code`() {
        val data = """{"error": "Something went wrong"}"""
        val result = SSEEventMapper.map("error", data)

        assertThat(result).isInstanceOf(StreamEvent.Error::class.java)
        val error = result as StreamEvent.Error
        assertThat(error.message).isEqualTo("Something went wrong")
        assertThat(error.code).isNull()
    }

    @Test
    fun `error event with missing error field`() {
        val data = """{}"""
        val result = SSEEventMapper.map("error", data)

        assertThat(result).isInstanceOf(StreamEvent.Error::class.java)
        val error = result as StreamEvent.Error
        assertThat(error.message).isEqualTo("Unknown error")
    }

    @Test
    fun `thinking_start returns ThinkingStart for reasoning_start`() {
        val result = SSEEventMapper.map("reasoning_start", "")
        assertThat(result).isEqualTo(StreamEvent.ThinkingStart)
    }

    @Test
    fun `thinking_start returns ThinkingStart for thinking_start`() {
        val result = SSEEventMapper.map("thinking_start", "")
        assertThat(result).isEqualTo(StreamEvent.ThinkingStart)
    }

    @Test
    fun `thinking_end returns ThinkingEnd for reasoning_end`() {
        val result = SSEEventMapper.map("reasoning_end", "")
        assertThat(result).isEqualTo(StreamEvent.ThinkingEnd)
    }

    @Test
    fun `thinking_end returns ThinkingEnd for thinking_end`() {
        val result = SSEEventMapper.map("thinking_end", "")
        assertThat(result).isEqualTo(StreamEvent.ThinkingEnd)
    }

    @Test
    fun `message_start returns MessageStart`() {
        val result = SSEEventMapper.map("message_start", "")
        assertThat(result).isEqualTo(StreamEvent.MessageStart)
    }

    @Test
    fun `message_end returns MessageEnd`() {
        val result = SSEEventMapper.map("message_end", "")
        assertThat(result).isEqualTo(StreamEvent.MessageEnd)
    }

    @Test
    fun `done returns Done`() {
        val result = SSEEventMapper.map("done", "")
        assertThat(result).isEqualTo(StreamEvent.Done)
    }

    @Test
    fun `close returns Close`() {
        val result = SSEEventMapper.map("close", "")
        assertThat(result).isEqualTo(StreamEvent.Close)
    }

    @Test
    fun `heartbeat returns Heartbeat`() {
        val result = SSEEventMapper.map("heartbeat", "")
        assertThat(result).isEqualTo(StreamEvent.Heartbeat)
    }

    @Test
    fun `preprocessing returns Preprocessing`() {
        val result = SSEEventMapper.map("preprocessing", "")
        assertThat(result).isEqualTo(StreamEvent.Preprocessing)
    }

    @Test
    fun `postprocessing returns Postprocessing`() {
        val result = SSEEventMapper.map("postprocessing", "")
        assertThat(result).isEqualTo(StreamEvent.Postprocessing)
    }

    @Test
    fun `unknown event type returns Unknown`() {
        val result = SSEEventMapper.map("some_unknown_event", "")

        assertThat(result).isInstanceOf(StreamEvent.Unknown::class.java)
        val unknown = result as StreamEvent.Unknown
        assertThat(unknown.type).isEqualTo("some_unknown_event")
    }

    @Test
    fun `unknown event type with data returns Unknown`() {
        val result = SSEEventMapper.map("custom_event", """{"key": "value"}""")

        assertThat(result).isInstanceOf(StreamEvent.Unknown::class.java)
        val unknown = result as StreamEvent.Unknown
        assertThat(unknown.type).isEqualTo("custom_event")
    }

    @Test
    fun `malformed JSON returns Error`() {
        val result = SSEEventMapper.map("delta", "not valid json")

        assertThat(result).isInstanceOf(StreamEvent.Error::class.java)
        val error = result as StreamEvent.Error
        assertThat(error.code).isEqualTo("parse_error")
        assertThat(error.message).startsWith("Parse error:")
    }

    @Test
    fun `preprocessing with JSON data returns Preprocessing`() {
        val result = SSEEventMapper.map("preprocessing", """{"status": "starting"}""")
        assertThat(result).isEqualTo(StreamEvent.Preprocessing)
    }

    @Test
    fun `postprocessing with JSON data returns Postprocessing`() {
        val result = SSEEventMapper.map("postprocessing", """{"status": "finishing"}""")
        assertThat(result).isEqualTo(StreamEvent.Postprocessing)
    }

    @Test
    fun `done with JSON data returns Done`() {
        val result = SSEEventMapper.map("done", """{"complete": true}""")
        assertThat(result).isEqualTo(StreamEvent.Done)
    }

    @Test
    fun `close with JSON data returns Close`() {
        val result = SSEEventMapper.map("close", """{"reason": "complete"}""")
        assertThat(result).isEqualTo(StreamEvent.Close)
    }

    @Test
    fun `heartbeat with JSON data returns Heartbeat`() {
        val result = SSEEventMapper.map("heartbeat", """{"timestamp": 12345}""")
        assertThat(result).isEqualTo(StreamEvent.Heartbeat)
    }

    @Test
    fun `empty data string returns mapped empty event`() {
        val result = SSEEventMapper.map("done", "   ")
        assertThat(result).isEqualTo(StreamEvent.Done)
    }
}
