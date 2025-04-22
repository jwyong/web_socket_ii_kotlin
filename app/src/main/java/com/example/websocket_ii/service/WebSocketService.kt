package com.example.websocket_ii.service

import android.app.*
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.websocket_ii.data.model.BatteryInfo
import com.example.websocket_ii.data.model.WebSocketResponse
import com.example.websocket_ii.data.model.WebSocketStatus
import com.example.websocket_ii.data.model.WebSocketType
import com.example.websocket_ii.network.WebSocketClient
import com.example.websocket_ii.respository.WebSocketRepository
import com.example.websocket_ii.util.GsonUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString
import kotlin.math.log

class WebSocketService : Service() {
    companion object {
        var wsClient: WebSocketClient? = null
    }
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        startForegroundService()
        setupWebSocket()
    }

    private fun setupWebSocket() {
        wsClient = WebSocketClient(object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                println("JAY_LOG, WebSocket connected, wsClient = $wsClient")
                webSocket.send("Android: Hello from foreground service")
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                println("JAY_LOG, Received: $text")

                handleResponse(text)
            }

            override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
                println("JAY_LOG, Received bytes: ${bytes.hex()}")
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                println("JAY_LOG, WebSocket failed: ${t.message}")
                reconnectWithDelay()
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                println("JAY_LOG, WebSocket closed: $reason")
            }
        })
        wsClient?.connect()
    }

    private fun startForegroundService() {
        val channelId = "websocket_service_channel"
        val notificationId = 42

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "WebSocket Service",
                NotificationManager.IMPORTANCE_LOW
            )
            getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("WebSocket Active")
            .setContentText("WebSocket connection is live.")
            .setSmallIcon(android.R.drawable.stat_notify_sync)
            .build()

        startForeground(notificationId, notification)
    }

    private fun handleResponse(text: String) {
        println("JAY_LOG, WebSocketService, handleResponse: text = $text")
        GsonUtil.fromJson(text, WebSocketResponse::class.java)?.let { resp ->
            when (resp.status) {
                WebSocketStatus.SUCCESS -> when (resp.type) {
                    WebSocketType.BATT -> handleBattResponse(resp.data)
                    WebSocketType.CHAT -> println("JAY_LOG, WebSocketService, handleResponse: chat")
                    WebSocketType.APPT -> println("JAY_LOG, WebSocketService, handleResponse: appt")
                }
                WebSocketStatus.ERROR -> Log.e("JAY_LOG", "handleResponse: error: $text ")
            }
        }
    }

    private fun handleBattResponse(data: Any) {
        println("JAY_LOG, WebSocketService, handleBattResponse: data = $data")
        val batteryInfo = GsonUtil.fromJson(data.toString(), BatteryInfo::class.java)
        WebSocketRepository.updateWsResponseBatt(batteryInfo)
    }

    private fun reconnectWithDelay() {
        // Launch the coroutine in the IO dispatcher for background work
        coroutineScope.launch {
            var retryCount = 0
            val maxRetries = 15
            while (retryCount < maxRetries) {
                try {
                    delay(3000)  // Delay for 3 seconds before trying to reconnect
                    wsClient?.connect()  // Attempt to reconnect
                    // If successful, break the loop and exit
                    break
                } catch (e: Exception) {
                    // Handle the exception (e.g., log it or show a message)
                    retryCount++
                    if (retryCount >= maxRetries) {
                        // Optionally handle max retries exceeded
                        println("Max retries reached, could not reconnect.")
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        println("JAY_LOG, WebSocketService, onDestroy: ")
        wsClient?.close()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
