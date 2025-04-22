package com.example.websocket_ii.data.model

data class WebSocketResponse(
    val status: WebSocketStatus, val type: WebSocketType, val data: Any
) {
    val isSuccess = status == WebSocketStatus.SUCCESS
}

enum class WebSocketStatus {
    SUCCESS, ERROR
}
enum class WebSocketType {
    BATT, CHAT, APPT
}