package com.dracarys.tapit.app.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dracarys.tapit.app.viewmodel.PlayerScore

@Composable
fun ResultScreen(
    scores: List<PlayerScore>,
    bestPlayer: PlayerScore?,
    onNewGame: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Game Results",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        bestPlayer?.let { player ->
            Text("Best Player: ${player.name}")
            Text("Average Time: %.2f ms".format(player.averageTime))
            Spacer(modifier = Modifier.height(16.dp))
        }

        scores.forEach { score ->
            Text("${score.name}: %.2f ms".format(score.averageTime))
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(onClick = onNewGame) {
            Text("Start New Game")
        }
    }
}
