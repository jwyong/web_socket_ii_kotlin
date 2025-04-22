package com.example.websocket_ii.respository

import com.example.websocket_ii.data.model.BatteryInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * Singleton object to hold websocket response for UI updates
 **/
object WebSocketRepository {
    private val scope = CoroutineScope(Dispatchers.IO)

    private val _wsResponseBatt = MutableSharedFlow<BatteryInfo>()
    val wsResponseBatt: SharedFlow<BatteryInfo> = _wsResponseBatt

    fun updateWsResponseBatt(batteryInfo: BatteryInfo?) {
        scope.launch {
            _wsResponseBatt.emit(batteryInfo?: return@launch)
        }
    }
}
