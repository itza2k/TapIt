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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dracarys.tapit.app.viewmodel.GameViewModel
import com.dracarys.tapit.app.viewmodel.Screen
import com.dracarys.tapit.app.screens.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModel: GameViewModel by viewModels {
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return GameViewModel(applicationContext) as T
                }
            }
        }

        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    when (viewModel.currentScreen) {
                        Screen.Welcome -> WelcomeScreen(
                            onNameSubmit = { name ->
                                viewModel.validateAndSetName(name)
                                viewModel.startCountdown()
                            },
                            nameError = viewModel.nameError,
                            scores = viewModel.scores,
                            bestPlayer = viewModel.getBestPlayer()
                        )
                        Screen.Game -> GameScreen(
                            playerName = viewModel.playerName,
                            targetPosition = viewModel.targetPosition,
                            reactionTime = viewModel.reactionTime,
                            gameState = viewModel.gameState,
                            countdownValue = viewModel.countdownValue,
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