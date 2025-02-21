package com.dracarys.tapit.app.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import com.dracarys.tapit.app.viewmodel.PlayerScore
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.ui.text.font.FontWeight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WelcomeScreen(
    onNameSubmit: (String) -> Unit,
    nameError: String? = null,
    scores: List<PlayerScore>,
    bestPlayer: PlayerScore?
) {
    var showCredits by remember { mutableStateOf(false) }
    var showScores by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("") }

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

        // Bottom buttons row
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            IconButton(onClick = { showCredits = true }) {
                Icon(
                    Icons.Default.Info,
                    contentDescription = "Credits",
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            IconButton(onClick = { showScores = true }) {
                Icon(
                    Icons.Default.Favorite,
                    contentDescription = "Leaderboard",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        // Scores bottom sheet
        if (showScores) {
            ModalBottomSheet(
                onDismissRequest = { showScores = false },
                sheetState = rememberModalBottomSheetState(),
                dragHandle = { BottomSheetDefaults.DragHandle() }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        "Leaderboard",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Best player card
                    bestPlayer?.let { player ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Text(
                                    "👑 Best Player: ${player.name}",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    "Average: %.2f ms".format(player.averageTime),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    // Scores list
                    LazyColumn(
                        modifier = Modifier.heightIn(max = 300.dp)
                    ) {
                        items(scores.sortedBy { it.averageTime }) { score ->
                            ListItem(
                                headlineContent = { Text(score.name) },
                                trailingContent = {
                                    Text("%.2f ms".format(score.averageTime))
                                }
                            )
                            Divider()
                        }
                    }
                }
            }
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
