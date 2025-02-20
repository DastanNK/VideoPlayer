package com.dastan.videoplayer.domain

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class WifiViewModel:ViewModel() {
    private val _wifiState = MutableStateFlow(false)
    val wifiState = _wifiState.asStateFlow()

    fun updateWifiState(isConnected: Boolean) {
        _wifiState.value = isConnected
    }
}