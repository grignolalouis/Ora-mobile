package com.ora.app.data.remote.sse

object SSEEventParser {

    fun parse(lines: List<String>): SSEEvent? {
        var event = "message"
        var data = StringBuilder()
        var id: String? = null

        for (line in lines) {
            when {
                line.startsWith("event:") -> event = line.substringAfter("event:").trim()
                line.startsWith("data:") -> {
                    if (data.isNotEmpty()) data.append("\n")
                    data.append(line.substringAfter("data:").trim())
                }
                line.startsWith("id:") -> id = line.substringAfter("id:").trim()
            }
        }

        if (data.isEmpty() && event == "message") return null

        return SSEEvent(
            event = event,
            data = data.toString(),
            id = id
        )
    }

    fun parseFromRaw(raw: String): List<SSEEvent> {
        val events = mutableListOf<SSEEvent>()
        val blocks = raw.split("\n\n").filter { it.isNotBlank() }

        for (block in blocks) {
            val lines = block.split("\n")
            parse(lines)?.let { events.add(it) }
        }

        return events
    }
}
