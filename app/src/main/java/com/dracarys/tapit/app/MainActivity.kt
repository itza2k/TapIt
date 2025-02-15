package com.dracarys.tapit.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dracarys.tapit.app.viewmodel.GameViewModel
import com.dracarys.tapit.app.viewmodel.Screen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModel: GameViewModel by viewModels()

        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    when (viewModel.currentScreen) {
                        Screen.Welcome -> WelcomeScreen(viewModel)
                        Screen.Game -> GameScreen(viewModel)
                    }
                }
            }
        }
    }
}

@Composable
fun WelcomeScreen(viewModel: GameViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        OutlinedTextField(
            value = viewModel.playerName,
            onValueChange = { viewModel.playerName = it },
            label = { Text("Enter your name") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { viewModel.navigateToGame() }) {
            Text("Start Game")
        }
    }
}

@Composable
fun GameScreen(viewModel: GameViewModel) {
    Box(modifier = Modifier.fillMaxSize()) {
        Button(
            onClick = { viewModel.onTargetTap() },
            modifier = Modifier
                .offset(
                    x = (viewModel.targetPosition.first * 300).dp,
                    y = (viewModel.targetPosition.second * 500).dp
                )
        ) {
            Text("Tap Me!")
        }

        Text(
            text = "Reaction Time: ${viewModel.reactionTime}ms",
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(16.dp)
        )
    }
}