package com.yosselin.appsmarthlight.bluetooth

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BluetoothManager @Inject constructor(@ApplicationContext private val context: Context) {

    private val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    private var bluetoothSocket: BluetoothSocket? = null
    private var listeningThread: Thread? = null

    val isBluetoothEnabled: Boolean
        get() = bluetoothAdapter?.isEnabled ?: false

    private val _isConnecting = MutableStateFlow(false)
    val isConnecting: StateFlow<Boolean> = _isConnecting.asStateFlow()

    private val _isScanning = MutableStateFlow(false)
    val isScanning: StateFlow<Boolean> = _isScanning.asStateFlow()

    private val _isConnected = MutableStateFlow(false)
    val isConnected: StateFlow<Boolean> = _isConnected.asStateFlow()

    private val _scannedDevices = MutableStateFlow<List<BluetoothDevice>>(emptyList())
    val scannedDevices: StateFlow<List<BluetoothDevice>> = _scannedDevices.asStateFlow()

    private val _pairedDevices = MutableStateFlow<List<BluetoothDevice>>(emptyList())
    val pairedDevices: StateFlow<List<BluetoothDevice>> = _pairedDevices.asStateFlow()

    private val _pirMovementDetected = MutableStateFlow(false)
    val pirMovementDetected: StateFlow<Boolean> = _pirMovementDetected.asStateFlow()

    private val sppUuid: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

    private val discoveryReceiver = object : BroadcastReceiver() {
        @SuppressLint("MissingPermission")
        override fun onReceive(context: Context, intent: Intent) {
            when(intent.action) {
                BluetoothDevice.ACTION_FOUND -> {
                    val device: BluetoothDevice? = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                    device?.let { foundDevice ->
                        if (!_scannedDevices.value.any { it.address == foundDevice.address }) {
                            _scannedDevices.update { it + foundDevice }
                        }
                    }
                }
                BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> {
                    _isScanning.value = false
                }
            }
        }
    }
    
    private val pairingReceiver = object : BroadcastReceiver() {
        @SuppressLint("MissingPermission")
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == BluetoothDevice.ACTION_BOND_STATE_CHANGED) {
                val device: BluetoothDevice? = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                val bondState = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.BOND_NONE)
                if (bondState == BluetoothDevice.BOND_BONDED) {
                    device?.let { CoroutineScope(Dispatchers.Main).launch { connect(it) } }
                    context.unregisterReceiver(this)
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun pairDevice(device: BluetoothDevice) {
        context.registerReceiver(pairingReceiver, IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED))
        device.createBond()
    }

    @SuppressLint("MissingPermission")
    fun startDiscovery() {
        if (!isBluetoothEnabled) return
        _scannedDevices.value = emptyList()
        val filter = IntentFilter().apply {
            addAction(BluetoothDevice.ACTION_FOUND)
            addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
        }
        context.registerReceiver(discoveryReceiver, filter)
        bluetoothAdapter?.startDiscovery()
        _isScanning.value = true
    }

    @SuppressLint("MissingPermission")
    fun stopDiscovery() {
        if (!isBluetoothEnabled) return
        if (_isScanning.value) {
            bluetoothAdapter?.cancelDiscovery()
            _isScanning.value = false
            try {
                context.unregisterReceiver(discoveryReceiver)
            } catch (e: IllegalArgumentException) {
                // Receiver not registered, ignore
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun refreshPairedDevices() {
        if (!isBluetoothEnabled) return
        bluetoothAdapter?.bondedDevices?.let {
            _pairedDevices.value = it.toList()
        }
    }

    @SuppressLint("MissingPermission")
    suspend fun connect(device: BluetoothDevice) {
        if (device.bondState != BluetoothDevice.BOND_BONDED) {
            pairDevice(device)
            return
        }
        
        stopDiscovery()
        _isConnecting.value = true
        try {
            withContext(Dispatchers.IO) {
                bluetoothSocket = device.createRfcommSocketToServiceRecord(sppUuid)
                bluetoothSocket?.connect()
            }
            _isConnected.value = true
            startListening()
        } catch (e: IOException) {
            _isConnected.value = false
            closeSocket()
        } finally {
            _isConnecting.value = false
        }
    }

    fun disconnect() {
        closeSocket()
        _isConnected.value = false
    }

    fun sendCommand(command: String) {
        try {
            bluetoothSocket?.outputStream?.write(command.toByteArray())
        } catch (e: IOException) {
            // Handle error
        }
    }

    private fun startListening() {
        listeningThread = Thread {
            try {
                val inputStream = bluetoothSocket?.inputStream
                val buffer = ByteArray(1024)
                var bytes: Int

                while (_isConnected.value) {
                    bytes = inputStream?.read(buffer) ?: -1
                    if (bytes != -1) {
                        val readMessage = String(buffer, 0, bytes)
                        if (readMessage.trim() == "PIR_DETECTED") {
                            _pirMovementDetected.value = true
                        } else if (readMessage.trim() == "PIR_NO_MOTION") {
                             _pirMovementDetected.value = false
                        }
                    }
                }
            } catch (e: IOException) {
                disconnect()
            }
        }
        listeningThread?.start()
    }

    private fun closeSocket() {
        listeningThread?.interrupt()
        listeningThread = null
        try {
            bluetoothSocket?.close()
        } catch (e: IOException) {
            // Handle error
        }
        bluetoothSocket = null
    }
}