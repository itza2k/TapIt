package com.dracarys.tapit.app.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import com.dracarys.tapit.app.viewmodel.PlayerScore
import androidx.compose.material.icons.Icons
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.filled.EmojiEvents
import kotlinx.coroutines.delay
import androidx.compose.runtime.LaunchedEffect

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun WelcomeScreen(
    onNameSubmit: (String) -> Unit,
    nameError: String? = null,
    scores: List<PlayerScore>,
    bestPlayer: PlayerScore?
) {
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
                text = "Tap!t",
                style = MaterialTheme.typography.displayLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "Test Your Reflexes",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 48.dp)
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
                    .padding(bottom = 24.dp)
            )

            // Enhanced start button
            Button(
                onClick = { if (name.isNotBlank()) onNameSubmit(name) },
                enabled = name.isNotBlank(),
                modifier = Modifier
                    .height(56.dp)
                    .fillMaxWidth(0.8f),
                shape = RoundedCornerShape(28.dp)
            ) {
                Text(
                    "Start Game",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }

        // Single centered leaderboard button
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 24.dp)
        ) {
            IconButton(
                onClick = { showScores = true },
                modifier = Modifier
                    .size(56.dp)
                    .background(
                        MaterialTheme.colorScheme.primaryContainer,
                        shape = CircleShape
                    )
            ) {
                Icon(
                    Icons.Default.Leaderboard,
                    contentDescription = "Leaderboard",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        // Enhanced leaderboard bottom sheet
        if (showScores) {
            ModalBottomSheet(
                onDismissRequest = { showScores = false },
                sheetState = rememberModalBottomSheetState(),
                dragHandle = { BottomSheetDefaults.DragHandle() },
                containerColor = MaterialTheme.colorScheme.surface,
                modifier = Modifier.fillMaxHeight(0.8f)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Animated title
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(bottom = 24.dp)
                    ) {
                        Icon(
                            Icons.Default.EmojiEvents,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            "Leaderboard",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    // Best player card with animation - simplified
                    bestPlayer?.let { player ->
                        var showBestPlayer by remember { mutableStateOf(false) }
                        LaunchedEffect(Unit) { showBestPlayer = true }

                        AnimatedVisibility(
                            visible = showBestPlayer,
                            enter = fadeIn() + expandVertically(
                                expandFrom = Alignment.Top,
                                animationSpec = tween(300, easing = FastOutSlowInEasing)
                            )
                        ) {
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 16.dp),
                                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .padding(16.dp)
                                        .fillMaxWidth(),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        "👑 Champion",
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        player.name,
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    Text(
                                        "${player.averageTime.toInt()} ms",
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }

                    // Animated scores list with rankings
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        itemsIndexed(
                            items = scores.sortedBy { it.averageTime },
                            key = { _, item -> item.name }
                        ) { index, score ->
                            var showItem by remember { mutableStateOf(false) }

                            LaunchedEffect(Unit) {
                                delay(index * 50L)
                                showItem = true
                            }

                            AnimatedVisibility(
                                visible = showItem,
                                enter = fadeIn() + slideInHorizontally(),
                                modifier = Modifier.animateItemPlacement()
                            ) {
                                Surface(
                                    modifier = Modifier.fillMaxWidth(),
                                    color = if (index < 3)
                                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = (0.7f - (index * 0.15f)))
                                    else MaterialTheme.colorScheme.surface,
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 16.dp, vertical = 12.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                                        ) {
                                            Text(
                                                text = when(index) {
                                                    0 -> "🥇"
                                                    1 -> "🥈"
                                                    2 -> "🥉"
                                                    else -> "${index + 1}"
                                                },
                                                style = MaterialTheme.typography.titleMedium.copy(
                                                    fontWeight = FontWeight.Bold
                                                ),
                                                modifier = Modifier.width(32.dp)
                                            )
                                            Text(
                                                score.name,
                                                style = MaterialTheme.typography.bodyLarge.copy(
                                                    fontWeight = if (index < 3) FontWeight.Bold else FontWeight.Normal
                                                )
                                            )
                                        }
                                        Text(
                                            "${score.averageTime.toInt()}ms",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                            }
                        }
                        item {
                            Spacer(modifier = Modifier.height(24.dp))
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}
