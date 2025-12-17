package com.yosselin.appsmarthlight.ui.viewmodels

import android.bluetooth.BluetoothDevice
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yosselin.appsmarthlight.bluetooth.BluetoothManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val bluetoothManager: BluetoothManager) : ViewModel() {

    val isConnected: StateFlow<Boolean> = bluetoothManager.isConnected
    val pairedDevices: StateFlow<List<BluetoothDevice>> = bluetoothManager.pairedDevices
    val scannedDevices: StateFlow<List<BluetoothDevice>> = bluetoothManager.scannedDevices
    val isScanning: StateFlow<Boolean> = bluetoothManager.isScanning
    val isConnecting: StateFlow<Boolean> = bluetoothManager.isConnecting

    val isBluetoothEnabled: Boolean
        get() = bluetoothManager.isBluetoothEnabled

    fun startDiscovery() {
        bluetoothManager.startDiscovery()
    }

    fun stopDiscovery() {
        bluetoothManager.stopDiscovery()
    }

    fun refreshPairedDevices() {
        bluetoothManager.refreshPairedDevices()
    }

    fun connect(device: BluetoothDevice) {
        viewModelScope.launch {
            bluetoothManager.connect(device)
        }
    }

    fun disconnect() {
        bluetoothManager.disconnect()
    }
}