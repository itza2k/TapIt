package com.dracarys.tapit.app.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.foundation.shape.RoundedCornerShape

@Composable
fun WelcomeScreen(
    onNameSubmit: (String) -> Unit,
    nameError: String? = null
) {
    var name by remember { mutableStateOf("") }
    var showCredits by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Title with emphasis
            Text(
                text = "TapIt!",
                style = MaterialTheme.typography.displayLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "Test Your Reflexes",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            // Styled input field
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Enter your name") },
                shape = RoundedCornerShape(12.dp),
                isError = nameError != null,
                supportingText = nameError?.let {
                    { Text(it, color = MaterialTheme.colorScheme.error) }
                },
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .padding(bottom = 16.dp)
            )

            // Enhanced start button
            Button(
                onClick = { if (name.isNotBlank()) onNameSubmit(name) },
                enabled = name.isNotBlank(),
                modifier = Modifier
                    .height(50.dp)
                    .fillMaxWidth(0.8f),
                shape = RoundedCornerShape(25.dp)
            ) {
                Text(
                    "Start Game",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }

        // Credits button at bottom
        IconButton(
            onClick = { showCredits = true },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp)
        ) {
            Icon(
                Icons.Default.Info,
                contentDescription = "Credits",
                tint = MaterialTheme.colorScheme.primary
            )
        }

        // Credits dialog
        if (showCredits) {
            AlertDialog(
                onDismissRequest = { showCredits = false },
                title = { Text("About") },
                text = {
                    Text(
                        "Made by Team Dracarys\nfor GDG Chennai Event",
                        style = MaterialTheme.typography.bodyLarge
                    )
                },
                confirmButton = {
                    TextButton(onClick = { showCredits = false }) {
                        Text("Close")
                    }
                }
            )
        }
    }
}
