package com.dracarys.tapit.app.viewmodel

import android.content.Context
import androidx.compose.runtime.*
import androidx.lifecycle.*
import kotlinx.coroutines.*
import kotlinx.serialization.*
import kotlinx.serialization.json.*
import java.io.File
import kotlin.random.Random

@Serializable
data class PlayerScore(
    val name: String,
    val averageTime: Double,
    val attempts: List<Long>,
    val dateTime: Long = System.currentTimeMillis()
)

enum class GameState {
    COUNTDOWN,
    PLAYING,
    PAUSED
}

class GameViewModel(private val context: Context) : ViewModel() {
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

    var gameState by mutableStateOf(GameState.COUNTDOWN)
    var countdownValue by mutableStateOf(3)
    private var gameJob: Job? = null

    init {
        loadScores()
    }

    private fun loadScores() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val file = File(context.filesDir, "scores.json")
                if (file.exists()) {
                    val jsonString = file.readText()
                    val scores = Json.decodeFromString<List<PlayerScore>>(jsonString)
                    _scores.clear()
                    _scores.addAll(scores)
                }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    private fun saveScores() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val jsonString = Json.encodeToString(_scores.toList())
                File(context.filesDir, "scores.json").writeText(jsonString)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

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
                startCountdown()
            }
        }
    }

    fun navigateToGame() {
        if (playerName.isNotBlank()) {
            currentScreen = Screen.Game
            startCountdown()
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

    fun startCountdown() {
        gameState = GameState.COUNTDOWN
        countdownValue = 3
        gameJob = viewModelScope.launch {
            while (countdownValue > 0) {
                delay(1000)
                countdownValue--
            }
            gameState = GameState.PLAYING
            resetTarget()
        }
    }

    fun onTargetTap() {
        if (gameState != GameState.PLAYING) return

        val currentTime = System.currentTimeMillis()
        reactionTime = currentTime - startTime
        currentAttempts.add(reactionTime)
        attemptCount++

        if (attemptCount >= maxAttempts) {
            val average = currentAttempts.average()
            _scores.add(PlayerScore(playerName, average, currentAttempts.toList()))
            saveScores()
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
