package com.dastan.videoplayer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.wifi.WifiManager

class WifiBroadcastReceiver(private val updateWiFiState: (Boolean) -> Unit): BroadcastReceiver()  {
    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            WifiManager.WIFI_STATE_CHANGED_ACTION -> {
                val wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN)
                val wifi:Boolean = when (wifiState) {
                    WifiManager.WIFI_STATE_ENABLED -> {
                        true
                    }
                    WifiManager.WIFI_STATE_DISABLED -> {
                        false
                    }
                    else -> {
                        false
                    }
                }
                updateWiFiState(wifi)

            }
        }
    }
}