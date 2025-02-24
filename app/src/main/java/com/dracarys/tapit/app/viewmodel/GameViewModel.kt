package com.dracarys.tapit.app.viewmodel

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.*
import androidx.lifecycle.*
import kotlinx.coroutines.*
import java.util.UUID
import kotlin.random.Random

data class PlayerScore(
    val playerId: String,
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
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("TapItPrefs", Context.MODE_PRIVATE)

    private var _playerName = mutableStateOf("")
    var playerName: String
        get() = _playerName.value
        set(value) {
            _playerName.value = value
            sharedPreferences.edit().putString("lastPlayerName", value).commit()
        }

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
        // Load saved name on initialization
        _playerName.value = sharedPreferences.getString("lastPlayerName", "") ?: ""
    }

    private fun loadScores() {
        val scores = sharedPreferences.getStringSet("scores", null)
        scores?.forEach { scoreString ->
            try {
                val parts = scoreString.split("|")
                if (parts.size >= 4) {
                    val attempts = parts[3].split(",").mapNotNull { it.toLongOrNull() }
                    _scores.add(
                        PlayerScore(
                            playerId = parts[0],
                            name = parts[1],
                            averageTime = parts[2].toDoubleOrNull() ?: 0.0,
                            attempts = attempts
                        )
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun saveScore(score: PlayerScore) {
        val scoreString = "${score.playerId}|${score.name}|${score.averageTime}|${score.attempts.joinToString(",")}"
        val scores = sharedPreferences.getStringSet("scores", mutableSetOf()) ?: mutableSetOf()
        scores.add(scoreString)
        sharedPreferences.edit().putStringSet("scores", scores).commit()
    }

    fun isNameAvailable(name: String): Boolean {
        return _scores.none { it.name.equals(name, ignoreCase = true) }
    }

    fun validateAndSetName(name: String) {
        when {
            name.isBlank() -> {
                nameError = "Name cannot be empty"
            }
            else -> {
                nameError = null
                playerName = name // This will automatically persist
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
        gameJob?.cancel()

        gameJob = viewModelScope.launch {
            try {
                for (i in 3 downTo 1) {
                    countdownValue = i
                    delay(1000)
                }
                withContext(Dispatchers.Main) {
                    gameState = GameState.PLAYING
                    resetTarget()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                // Reset on error
                gameState = GameState.COUNTDOWN
                countdownValue = 3
            }
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
            val playerId = UUID.randomUUID().toString()
            val score = PlayerScore(playerId, playerName, average, currentAttempts.toList())
            _scores.add(score)
            saveScore(score)
            currentScreen = Screen.Result
        } else {
            resetTarget()
        }
    }

    fun startNewGame() {
        reactionTime = 0
        attemptCount = 0
        nameError = null
        currentAttempts.clear()
        currentScreen = Screen.Welcome
        // Don't clear playerName anymore
    }

    fun getBestPlayer(): PlayerScore? {
        return _scores.minByOrNull { it.averageTime }
    }

    override fun onCleared() {
        super.onCleared()
        // Force save preferences
        sharedPreferences.edit().commit()
    }
}

enum class Screen {
    Welcome,
    Game,
    Result
}
