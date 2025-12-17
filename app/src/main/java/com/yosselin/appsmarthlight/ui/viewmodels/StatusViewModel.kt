package com.yosselin.appsmarthlight.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.yosselin.appsmarthlight.bluetooth.BluetoothManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class StatusViewModel @Inject constructor(private val bluetoothManager: BluetoothManager) : ViewModel() {

    val isPirMovementDetected: StateFlow<Boolean> = bluetoothManager.pirMovementDetected

    private val _isLedOn = MutableStateFlow(false)
    val isLedOn: StateFlow<Boolean> = _isLedOn

    fun turnLedOn() {
        bluetoothManager.sendCommand("LED_ON")
        _isLedOn.value = true
    }

    fun turnLedOff() {
        bluetoothManager.sendCommand("LED_OFF")
        _isLedOn.value = false
    }
}