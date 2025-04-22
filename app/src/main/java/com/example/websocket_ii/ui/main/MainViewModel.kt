package com.example.websocket_ii.ui.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.websocket_ii.data.model.BatteryInfo
import com.example.websocket_ii.service.WebSocketService
import com.example.websocket_ii.service.WebSocketService.Companion.wsClient
import com.example.websocket_ii.util.BatteryUtil
import com.example.websocket_ii.util.GsonUtil
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val _batteryInfo = MutableSharedFlow<BatteryInfo?>()
    val batteryInfo: SharedFlow<BatteryInfo?> get() = _batteryInfo

    // Function to get battery info
    fun getBatteryInfo() {
        val context = getApplication<Application>().applicationContext

        viewModelScope.launch {
            _batteryInfo.emit(BatteryUtil.getBatteryInfo(context))
        }
    }

    // Send battery info to WebSocket
    fun sendBatteryInfoToWebSocket(batteryInfo: BatteryInfo) {
        val batteryInfoJson = GsonUtil.toJson(batteryInfo)
        println("JAY_LOG, WebSocketService, wsClient = $wsClient, sendBatteryInfo: $batteryInfoJson")
        wsClient?.send(batteryInfoJson)
    }
}
