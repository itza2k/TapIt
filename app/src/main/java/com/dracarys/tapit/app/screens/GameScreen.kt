package com.dracarys.tapit.app.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.alpha
import com.dracarys.tapit.app.viewmodel.GameState

@Composable
fun GameScreen(
    playerName: String,
    targetPosition: Pair<Float, Float>,
    reactionTime: Long,
    gameState: GameState,
    countdownValue: Int,
    onTargetTap: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        // Score display at the top
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Player: $playerName",
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = "Reaction Time: ${reactionTime}ms",
                style = MaterialTheme.typography.bodyLarge
            )
        }

        when (gameState) {
            GameState.COUNTDOWN -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = countdownValue.toString(),
                        style = MaterialTheme.typography.displayLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            GameState.PLAYING -> {
                // Enhanced target button with ripple
                val interactionSource = remember { MutableInteractionSource() }

                Surface(
                    modifier = Modifier
                        .size(100.dp)
                        .offset(
                            x = (targetPosition.first * (LocalConfiguration.current.screenWidthDp - 100)).dp,
                            y = (targetPosition.second * (LocalConfiguration.current.screenHeightDp - 100)).dp
                        )
                        .align(Alignment.TopStart)
                        .alpha(0.9f)  // Slight transparency for the whole button
                        .clickable(
                            interactionSource = interactionSource,
                            indication = rememberRipple(
                                bounded = true,  // Keep ripple within bounds
                                radius = 50.dp,  // Match circle radius
                                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.3f)  // More transparent ripple
                            ),
                            onClick = onTargetTap
                        ),
                    shape = CircleShape,  // Make surface perfectly circular
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.85f),  // Slightly transparent base color
                    tonalElevation = 6.dp  // Slightly increased elevation for better depth
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Text(
                            "Tap!",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }
            else -> { /* Handle other states */ }
        }
    }
}
