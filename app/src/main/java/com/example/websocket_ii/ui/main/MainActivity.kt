package com.example.websocket_ii.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.websocket_ii.R
import com.example.websocket_ii.databinding.ActivityMainBinding
import com.example.websocket_ii.respository.WebSocketRepository
import com.example.websocket_ii.service.WebSocketService
import com.example.websocket_ii.util.showToast
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        // Start the WebSocket service to keep the connection alive
        val serviceIntent = Intent(this, WebSocketService::class.java)
        startService(serviceIntent)

        collectStateFlow()

        binding.fab.setOnClickListener {
            // Call ViewModel to get battery info and send it to WebSocket
            mainViewModel.getBatteryInfo()
        }
    }

    private fun collectStateFlow() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    mainViewModel.batteryInfo.collect { batteryInfo ->
                        println("JAY_LOG, MainActivity, collectStateFlow: MainVM battInfo = $batteryInfo")
                        batteryInfo?.let { mainViewModel.sendBatteryInfoToWebSocket(it) }
                    }
                }
                launch {
                    WebSocketRepository.wsResponseBatt.collect { batteryInfo ->
                        println("JAY_LOG, MainActivity, collectStateFlow: wsResp battInfo = $batteryInfo")

                        with(batteryInfo) {
                            // show snack bar
                            showToast(
                                this@MainActivity, getString(
                                    R.string.ws_battery_info_success, level, temperature
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}