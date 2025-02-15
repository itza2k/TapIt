package com.dracarys.tapit.app.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import kotlin.random.Random

class GameViewModel : ViewModel() {
    var playerName by mutableStateOf("")

    var currentScreen by mutableStateOf(Screen.Welcome)
        private set

    var targetPosition by mutableStateOf(generateRandomPosition())
        private set

    var gameStartTime by mutableStateOf(0L)
        private set

    var reactionTime by mutableStateOf(0L)
        private set

    fun navigateToGame() {
        currentScreen = Screen.Game
        startNewRound()
    }

    fun startNewRound() {
        targetPosition = generateRandomPosition()
        gameStartTime = System.currentTimeMillis()
    }

    fun onTargetTap() {
        reactionTime = System.currentTimeMillis() - gameStartTime
        startNewRound()
    }

    private fun generateRandomPosition(): Pair<Float, Float> {
        return Pair(Random.nextFloat(), Random.nextFloat())
    }
}

enum class Screen {
    Welcome,
    Game
}
