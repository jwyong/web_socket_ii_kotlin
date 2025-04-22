package com.example.websocket_ii.util

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Build
import com.example.websocket_ii.data.model.BatteryInfo

object BatteryUtil {
    // Get current battery level
    fun getBatteryInfo(context: Context): BatteryInfo? {
        return (context.getSystemService(Context.BATTERY_SERVICE) as? BatteryManager)?.run {
            val batteryStatus: Intent? = context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
            val temperature = batteryStatus?.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0) ?: 0

            BatteryInfo(
                level = getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY),
                temperature = temperature / 10,
            )
        }
    }
}
