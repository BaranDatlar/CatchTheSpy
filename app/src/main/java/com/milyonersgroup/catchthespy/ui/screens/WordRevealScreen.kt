package com.milyonersgroup.catchthespy.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.milyonersgroup.catchthespy.data.model.GameState
import com.milyonersgroup.catchthespy.ui.navigation.Screen
import com.milyonersgroup.catchthespy.viewmodel.GameViewModel

@Composable
fun WordRevealScreen(
    navController: NavController,
    viewModel: GameViewModel,
    roomCode: String
) {
    val gameRoom by viewModel.gameRoom.collectAsState()
    val currentPlayerId by viewModel.currentPlayerId.collectAsState()

    val currentPlayer = gameRoom?.players?.get(currentPlayerId)
    val myWord = currentPlayer?.word ?: ""
    val isSpy = currentPlayer?.isSpy ?: false
    val isHost = gameRoom?.hostId == currentPlayerId

    // Debug logging
    LaunchedEffect(currentPlayer) {
        android.util.Log.d("WordRevealScreen", "Current Player ID: $currentPlayerId")
        android.util.Log.d("WordRevealScreen", "Current Player: ${currentPlayer?.name}")
        android.util.Log.d("WordRevealScreen", "Is Spy: $isSpy")
        android.util.Log.d("WordRevealScreen", "Word: $myWord")
        android.util.Log.d("WordRevealScreen", "Spy ID from room: ${gameRoom?.spyId}")
    }

    // Auto navigate when game state changes to PLAYING (for non-host players)
    LaunchedEffect(gameRoom?.gameState) {
        if (gameRoom?.gameState == GameState.PLAYING && !isHost) {
            navController.navigate(Screen.Game.createRoute(roomCode)) {
                popUpTo(Screen.WordReveal.createRoute(roomCode)) { inclusive = true }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (myWord.isBlank()) {
            CircularProgressIndicator()
            Spacer(modifier = Modifier.height(16.dp))
            Text("Rolünüz ve kelimeniz atanıyor...")
        } else {
            if (isSpy) {
                // SPY SCREEN
                Text(
                    text = "🕵️ SİZ SPY'SINIZ! 🕵️",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.error
                )

                Spacer(modifier = Modifier.height(24.dp))

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Sizin göreviniz diğer oyuncuları kandırmak!",
                            fontSize = 16.sp,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Sizin Kelimeniz:",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = myWord,
                            fontSize = 36.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "⚠️ Diğer oyuncuların kelimesi farklı!",
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            } else {
                // NORMAL PLAYER SCREEN
                Text(
                    text = "👥 Normal Oyuncusunuz",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(24.dp))

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Sizin Kelimeniz:",
                            fontSize = 18.sp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = myWord,
                            fontSize = 36.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "💡 Spy'ı bulun!",
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            if (isHost) {
                Button(
                    onClick = {
                        // Host starts the game and navigates with everyone
                        viewModel.updateGameState(GameState.PLAYING)
                        navController.navigate(Screen.Game.createRoute(roomCode)) {
                            popUpTo(Screen.WordReveal.createRoute(roomCode)) { inclusive = true }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    Text("Oyunu Başlat", fontSize = 18.sp)
                }
            } else {
                Text(
                    text = "Host oyunu başlatmasını bekleyin...",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
