package com.dracarys.tapit.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dracarys.tapit.app.viewmodel.GameViewModel
import com.dracarys.tapit.app.viewmodel.Screen
import com.dracarys.tapit.app.screens.*
import com.dracarys.tapit.app.ui.theme.TapItTheme

class MainActivity : ComponentActivity() {
    private val viewModel: GameViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return GameViewModel(applicationContext) as T
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            TapItTheme(dynamicColor = true) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    when (viewModel.currentScreen) {
                        Screen.Welcome -> {
                            WelcomeScreen(
                                onNameSubmit = { viewModel.validateAndSetName(it) },
                                nameError = viewModel.nameError,
                                scores = viewModel.scores,
                                bestPlayer = viewModel.getBestPlayer()
                            )
                        }
                        Screen.Game -> {
                            GameScreen(
                                playerName = viewModel.playerName,
                                targetPosition = viewModel.targetPosition,
                                reactionTime = viewModel.reactionTime,
                                gameState = viewModel.gameState,
                                countdownValue = viewModel.countdownValue,
                                onTargetTap = viewModel::onTargetTap
                            )
                        }
                        Screen.Result -> {
                            ResultScreen(
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
}