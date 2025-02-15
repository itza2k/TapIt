package com.dracarys.tapit.app.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp

@Composable
fun GameScreen(
    playerName: String,
    targetPosition: Pair<Float, Float>,
    reactionTime: Long,
    onTargetTap: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Player: $playerName",
            modifier = Modifier.padding(16.dp)
        )

        Text(
            text = "Last Reaction Time: ${reactionTime}ms",
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .pointerInput(Unit) {
                    detectTapGestures { tapOffset ->
                        onTargetTap()
                    }
                }
        ) {
            val circleRadius = 50f
            drawCircle(
                color = Color.Red,
                radius = circleRadius,
                center = Offset(
                    x = targetPosition.first * (size.width - 2 * circleRadius) + circleRadius,
                    y = targetPosition.second * (size.height - 2 * circleRadius) + circleRadius
                )
            )
        }
    }
}
