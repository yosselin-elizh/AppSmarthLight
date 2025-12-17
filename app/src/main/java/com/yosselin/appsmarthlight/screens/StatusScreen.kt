package com.yosselin.appsmarthlight.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.yosselin.appsmarthlight.ui.viewmodels.StatusViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatusScreen(
    navController: NavController,
    viewModel: StatusViewModel = hiltViewModel()
) {
    val isPirMovementDetected by viewModel.isPirMovementDetected.collectAsState()
    val isLedOn by viewModel.isLedOn.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Estado del Sistema") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        },
        bottomBar = {
            BottomAppBar {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    IconButton(onClick = { navController.navigate("menu") }) {
                        Icon(Icons.Default.Home, contentDescription = "Inicio")
                    }
                    IconButton(onClick = { /* Already on Status */ }) {
                        Icon(Icons.Default.Info, contentDescription = "Estado", tint = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = if (isPirMovementDetected) "Movimiento detectado" else "Sin movimiento",
                color = if (isPirMovementDetected) Color.Red else Color.Gray
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = if (isLedOn) "ENCENDIDO" else "APAGADO",
                color = if (isLedOn) Color.Green else Color.Gray
            )
            Spacer(modifier = Modifier.height(32.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(onClick = { viewModel.turnLedOn() }) {
                    Text("Encender LED")
                }
                Button(onClick = { viewModel.turnLedOff() }) {
                    Text("Apagar LED")
                }
            }
        }
    }
}