package com.yosselin.appsmarthlight.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.yosselin.appsmarthlight.ui.viewmodels.LoginUiState
import com.yosselin.appsmarthlight.ui.viewmodels.LoginViewModel

@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: LoginViewModel = hiltViewModel()
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Usuario") }
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation()
        )
        Spacer(modifier = Modifier.height(16.dp))

        val currentUiState = uiState
        if (currentUiState is LoginUiState.Error) {
            Text(currentUiState.message, color = MaterialTheme.colorScheme.error)
            Spacer(modifier = Modifier.height(16.dp))
        }

        if (currentUiState is LoginUiState.Loading) {
            CircularProgressIndicator()
        } else {
            Row {
                Button(onClick = { viewModel.login(username, password) }) {
                    Text("Ingresar")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = { viewModel.register(username, password) }) {
                    Text("Registrar")
                }
            }
        }
    }

    LaunchedEffect(uiState) {
        if (uiState is LoginUiState.Success) {
            navController.navigate("menu") {
                popUpTo("login") { inclusive = true }
            }
        }
    }
}