package com.yosselin.appsmarthlight

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.yosselin.appsmarthlight.navigation.AppNavigation
import com.yosselin.appsmarthlight.ui.theme.AppSmarthLightTheme
import com.yosselin.appsmarthlight.ui.viewmodels.ConfigurationViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val granted = permissions.all { it.value }
            if (!granted) {
                Toast.makeText(this, "Permisos Bluetooth requeridos", Toast.LENGTH_LONG).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestBluetoothPermissions()

        setContent {
            val configViewModel: ConfigurationViewModel = hiltViewModel()
            val isDarkMode by configViewModel.isDarkMode.collectAsState()

            AppSmarthLightTheme(darkTheme = isDarkMode) {
                AppNavigation()
            }
        }
    }

    private fun requestBluetoothPermissions() {
        val permissions = arrayOf(
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.ACCESS_FINE_LOCATION
        )

        val missing = permissions.any {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (missing) {
            permissionLauncher.launch(permissions)
        }
    }
}