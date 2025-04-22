package com.example.websocket_ii.network

import com.example.websocket_ii.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import java.util.concurrent.TimeUnit

class WebSocketClient(
    private val listener: WebSocketListener
) {
    private val client = OkHttpClient.Builder()
        .readTimeout(0, TimeUnit.MILLISECONDS)
        .build()

    private var webSocket: WebSocket? = null

    fun connect() {
        println("JAY_LOG, WebSocketClient, connect: wsUrl = ${BuildConfig.WS_URL}")
        val request = Request.Builder().url(BuildConfig.WS_URL).build()
        webSocket = client.newWebSocket(request, listener)
    }

    fun send(message: String) {
        webSocket?.send(message)
    }

    fun close() {
        webSocket?.close(1000, "Closed by user")
    }
}
