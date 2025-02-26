package com.dracarys.tapit.app.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.clickable
import com.dracarys.tapit.app.viewmodel.GameState
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.foundation.background
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Timer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.alpha

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun GameScreen(
    playerName: String,
    targetPosition: Pair<Float, Float>,
    reactionTime: Long,
    gameState: GameState,
    countdownValue: Int,
    onTargetTap: () -> Unit
) {
    val configuration = LocalConfiguration.current
    val topBarHeight = 120.dp

    // basically the unified colours
    val primaryColor = MaterialTheme.colorScheme.primary
    val surfaceColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.95f)
    val onPrimaryColor = MaterialTheme.colorScheme.onPrimary

    Box(modifier = Modifier.fillMaxSize()) {
        // top stats
        AnimatedVisibility(
            visible = true,
            enter = slideInVertically() + fadeIn(),
            modifier = Modifier.align(Alignment.TopCenter)
        ) {
            ElevatedCard(
                modifier = Modifier
                    .padding(12.dp)
                    .fillMaxWidth()
                    .height(topBarHeight),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = surfaceColor
                ),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = playerName, // player center
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.graphicsLayer {
                            alpha = 0.9f
                        }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // ani - reaction time show
                    AnimatedContent(
                        targetState = reactionTime,
                        transitionSpec = {
                            fadeIn(animationSpec = tween(300)) + scaleIn(
                                initialScale = 0.8f,
                                animationSpec = tween(300)
                            ) togetherWith fadeOut(animationSpec = tween(300))
                        }
                    ) { time ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                Icons.Default.Timer,
                                contentDescription = "Timer",
                                tint = primaryColor,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (time > 0) "${time}ms" else "Ready...",
                                style = MaterialTheme.typography.headlineMedium,
                                color = primaryColor,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    AnimatedContent(
                        targetState = reactionTime,
                        transitionSpec = {
                            fadeIn(animationSpec = tween(200)) togetherWith fadeOut(animationSpec = tween(200))
                        }
                    ) { time ->
                        Text(
                            text = when {
                                time == 0L -> "Let's go!"
                                time < 150 -> "Hacker Speed 🕵️"
                                time < 200 -> "Supersonic Speed ⚡"
                                time < 250 -> "Crazy fast 🚀"
                                time < 300 -> "Ultra Instinct 🌟"
                                time < 350 -> "Ultra Instinct 🌟"
                                time < 400 -> "Crazy Fast 🚀"
                                time < 450 -> "Do Better🏃"
                                time < 500 -> "Lil Faster 🐢"
                                else -> "Bro, even blind folks got quicker reflexes than you 💀"
                            },
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
        }

        // the game stuff
        when (gameState) {
            GameState.COUNTDOWN -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    val infiniteTransition = rememberInfiniteTransition()
                    val scale by infiniteTransition.animateFloat(
                        initialValue = 0.6f,
                        targetValue = 1.2f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(700, easing = FastOutSlowInEasing),
                            repeatMode = RepeatMode.Reverse
                        )
                    )

                    val rotation by infiniteTransition.animateFloat(
                        initialValue = -5f,
                        targetValue = 5f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(500, easing = FastOutSlowInEasing),
                            repeatMode = RepeatMode.Reverse
                        )
                    )

                    Text(
                        text = when(countdownValue) {
                            3 -> "Ready..."
                            2 -> "Set..."
                            1 -> "GO!"
                            else -> countdownValue.toString()
                        },
                        style = MaterialTheme.typography.displayLarge,
                        color = primaryColor,
                        modifier = Modifier
                            .scale(scale)
                            .graphicsLayer {
                                rotationZ = rotation
                                alpha = 0.9f
                            }
                    )
                }
            }

            GameState.PLAYING -> {
                val interactionSource = remember { MutableInteractionSource() }
                var isPressed by remember { mutableStateOf(false) }
                val targetSize = 140.dp


                val infiniteTransition = rememberInfiniteTransition()
                val pulseScale by infiniteTransition.animateFloat(
                    initialValue = 0.95f,
                    targetValue = 1.05f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(1000, easing = FastOutSlowInEasing), // target button animations
                        repeatMode = RepeatMode.Reverse
                    )
                )

                val glowAlpha by infiniteTransition.animateFloat(
                    initialValue = 0.2f,
                    targetValue = 0.4f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(1000, easing = FastOutSlowInEasing),
                        repeatMode = RepeatMode.Reverse
                    )
                )

                val buttonScale by animateFloatAsState(
                    targetValue = if (isPressed) 0.85f else 1f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                )

                val safeYOffset = topBarHeight.value + 16 // Reduced padding

                // avoid over lap with top bar
                val adjustedY = (targetPosition.second *
                        (configuration.screenHeightDp - safeYOffset - targetSize.value - 32) +
                        safeYOffset).dp

                // Cleaned up the  target button
                Box(
                    modifier = Modifier
                        .size(targetSize)
                        .offset(
                            x = (targetPosition.first * (configuration.screenWidthDp - targetSize.value - 32)).dp,
                            y = adjustedY
                        )
                        .scale(buttonScale * pulseScale)
                        .align(Alignment.TopStart)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    primaryColor,
                                    primaryColor.copy(alpha = 0.9f)
                                )
                            )
                        )
                        .clickable(
                            interactionSource = interactionSource,
                            indication = rememberRipple(
                                bounded = true,
                                color = onPrimaryColor.copy(alpha = 0.3f),
                                radius = 70.dp
                            ),
                            onClick = {
                                isPressed = true
                                onTargetTap()
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {

                    Text(
                        "TAP!T",
                        style = MaterialTheme.typography.headlineMedium,
                        color = onPrimaryColor,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
            else -> { /* Handle other states */ }
        }
    }
}
