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

    // Auto navigate when game state changes to PLAYING (for non-host players)
    LaunchedEffect(gameRoom?.gameState) {
        if (gameRoom?.gameState == GameState.PLAYING) {
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
        Text(
            text = if (isSpy) "Siz Spy'sınız!" else "Siz Normal Oyuncusunuz",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = if (isSpy) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(32.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isSpy)
                    MaterialTheme.colorScheme.errorContainer
                else
                    MaterialTheme.colorScheme.primaryContainer
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
            }
        }

        Spacer(modifier = Modifier.height(48.dp))

        if (isHost) {
            Button(
                onClick = {
                    viewModel.updateGameState(GameState.PLAYING)
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
