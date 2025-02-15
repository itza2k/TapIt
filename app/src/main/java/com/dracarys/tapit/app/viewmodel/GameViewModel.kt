package com.dracarys.tapit.app.viewmodel

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import kotlin.random.Random

data class PlayerScore(
    val name: String,
    val averageTime: Double,
    val attempts: List<Long>
)

class GameViewModel : ViewModel() {
    var playerName by mutableStateOf("")
    var reactionTime by mutableStateOf(0L)
    var targetPosition by mutableStateOf(Pair(0f, 0f))
    var currentScreen by mutableStateOf(Screen.Welcome)
    var attemptCount by mutableStateOf(0)
    private var startTime by mutableStateOf(0L)
    private val maxAttempts = 15

    private val _scores = mutableStateListOf<PlayerScore>()
    val scores: List<PlayerScore> = _scores
    private val currentAttempts = mutableListOf<Long>()
    var nameError by mutableStateOf<String?>(null)
        private set

    fun isNameAvailable(name: String): Boolean {
        return _scores.none { it.name.equals(name, ignoreCase = true) }
    }

    fun validateAndSetName(name: String) {
        when {
            name.isBlank() -> {
                nameError = "Name cannot be empty"
            }
            !isNameAvailable(name) -> {
                nameError = "This name has already played"
            }
            else -> {
                nameError = null
                playerName = name
                currentScreen = Screen.Game
                resetTarget()
            }
        }
    }

    fun navigateToGame() {
        if (playerName.isNotBlank()) {
            currentScreen = Screen.Game
            resetTarget()
        }
    }

    private fun resetTarget() {
        // Adjust random position to account for screen edges
        targetPosition = Pair(
            Random.nextFloat() * 0.8f + 0.1f,  // Keep button 10% away from edges
            Random.nextFloat() * 0.7f + 0.1f    // Account for top bar and bottom space
        )
        startTime = System.currentTimeMillis()
    }

    fun onTargetTap() {
        val currentTime = System.currentTimeMillis()
        reactionTime = currentTime - startTime
        currentAttempts.add(reactionTime)
        attemptCount++

        if (attemptCount >= maxAttempts) {
            // Calculate average and store score
            val average = currentAttempts.average()
            _scores.add(PlayerScore(playerName, average, currentAttempts.toList()))

            // Reset for next player
            currentAttempts.clear()
            attemptCount = 0
            currentScreen = Screen.Result
        } else {
            resetTarget()
        }
    }

    fun startNewGame() {
        playerName = ""
        reactionTime = 0
        attemptCount = 0
        nameError = null
        currentAttempts.clear()
        currentScreen = Screen.Welcome
    }

    fun getBestPlayer(): PlayerScore? {
        return _scores.minByOrNull { it.averageTime }
    }
}

enum class Screen {
    Welcome,
    Game,
    Result
}
