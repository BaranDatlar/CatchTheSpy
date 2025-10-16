package com.milyonersgroup.catchthespy.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.milyonersgroup.catchthespy.data.model.GameState
import com.milyonersgroup.catchthespy.ui.navigation.Screen
import com.milyonersgroup.catchthespy.viewmodel.GameViewModel

@Composable
fun GuessScreen(
    navController: NavController,
    viewModel: GameViewModel,
    roomCode: String
) {
    val gameRoom by viewModel.gameRoom.collectAsState()
    val currentPlayerId by viewModel.currentPlayerId.collectAsState()

    // Find the spy player
    val spyPlayer = gameRoom?.players?.values?.find { it.isSpy }
    val isHost = gameRoom?.hostId == currentPlayerId

    LaunchedEffect(gameRoom?.gameState) {
        if (gameRoom?.gameState == GameState.FINISHED) {
            // Navigate to result screen
            // We need to determine if spy won from game state
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "🎭 Tahmin Zamanı!",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Show who the spy was
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
                    text = "🕵️ SPY KİMDİ?",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.error
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = spyPlayer?.name ?: "Bilinmiyor",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.error
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Kelimesi: ${spyPlayer?.word ?: ""}",
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        if (isHost) {
            Text(
                text = "Host olarak karar verin:",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    viewModel.submitGuess(false) // Spy lost
                    navController.navigate(Screen.Result.createRoute(roomCode, false)) {
                        popUpTo(Screen.MainMenu.route) { inclusive = false }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text("✓ Evet, Spy'ı Bulduk!", fontSize = 18.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    viewModel.submitGuess(true) // Spy won
                    navController.navigate(Screen.Result.createRoute(roomCode, true)) {
                        popUpTo(Screen.MainMenu.route) { inclusive = false }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("✗ Hayır, Spy Kazandı!", fontSize = 18.sp)
            }
        } else {
            Text(
                text = "Host karar vermesini bekleyin...",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
