package com.dracarys.tapit.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.dracarys.tapit.app.viewmodel.GameViewModel
import com.dracarys.tapit.app.viewmodel.Screen
import com.dracarys.tapit.app.screens.*

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
                        Screen.Welcome -> WelcomeScreen(
                            onNameSubmit = viewModel::validateAndSetName,
                            nameError = viewModel.nameError
                        )
                        Screen.Game -> GameScreen(
                            playerName = viewModel.playerName,
                            targetPosition = viewModel.targetPosition,
                            reactionTime = viewModel.reactionTime,
                            onTargetTap = viewModel::onTargetTap
                        )
                        Screen.Result -> ResultScreen(
                            scores = viewModel.scores,
                            bestPlayer = viewModel.getBestPlayer(),
                            onNewGame = viewModel::startNewGame
                        )
                    }
                }
            }
        }
    }
}