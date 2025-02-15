package com.dracarys.tapit.app.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalConfiguration

@Composable
fun GameScreen(
    playerName: String,
    targetPosition: Pair<Float, Float>,
    reactionTime: Long,
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

        // Target button
        Button(
            onClick = onTargetTap,
            modifier = Modifier
                .size(100.dp)
                .offset(
                    x = (targetPosition.first * (LocalConfiguration.current.screenWidthDp - 100)).dp,
                    y = (targetPosition.second * (LocalConfiguration.current.screenHeightDp - 100)).dp
                )
                .align(Alignment.TopStart),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text("Tap!")
        }
    }
}
