package com.milyonersgroup.catchthespy.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import kotlinx.coroutines.delay

@Composable
fun GameScreen(
    navController: NavController,
    viewModel: GameViewModel,
    roomCode: String
) {
    val gameRoom by viewModel.gameRoom.collectAsState()
    val currentPlayerId by viewModel.currentPlayerId.collectAsState()
    
    val currentPlayer = gameRoom?.players?.get(currentPlayerId)
    val isReady = currentPlayer?.isReady ?: false
    val allReady = gameRoom?.players?.values?.all { it.isReady } ?: false
    
    var timeLeft by remember { mutableStateOf(gameRoom?.gameDuration ?: 300) }
    
    LaunchedEffect(gameRoom?.gameStartTime) {
        gameRoom?.let { room ->
            val startTime = room.gameStartTime
            val duration = room.gameDuration
            
            while (timeLeft > 0) {
                val elapsed = (System.currentTimeMillis() - startTime) / 1000
                timeLeft = (duration - elapsed.toInt()).coerceAtLeast(0)
                delay(1000)
            }
        }
    }
    
    LaunchedEffect(allReady, gameRoom?.gameState) {
        if (allReady && gameRoom?.gameState == GameState.PLAYING) {
            viewModel.updateGameState(GameState.GUESSING)
        }
        
        if (gameRoom?.gameState == GameState.GUESSING) {
            val isHost = gameRoom?.hostId == currentPlayerId
            if (isHost) {
                navController.navigate(Screen.Guess.createRoute(roomCode)) {
                    popUpTo(Screen.Game.createRoute(roomCode)) { inclusive = true }
                }
            }
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        // Timer
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = if (timeLeft < 60) 
                    MaterialTheme.colorScheme.errorContainer 
                else 
                    MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Kalan Süre", fontSize = 18.sp)
                Text(
                    text = "${timeLeft / 60}:${(timeLeft % 60).toString().padStart(2, '0')}",
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text("Oyuncular:", fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
        
        Spacer(modifier = Modifier.height(16.dp))
        
        LazyColumn(
            modifier = Modifier.weight(1f)
        ) {
            gameRoom?.players?.values?.let { players ->
                items(players.toList()) { player ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = player.name,
                                fontWeight = if (player.id == currentPlayerId) FontWeight.Bold else FontWeight.Normal
                            )
                            if (player.isReady) {
                                Text("✓ Hazır", color = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Button(
            onClick = { viewModel.setPlayerReady(!isReady) },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = if (isReady) 
                ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
            else 
                ButtonDefaults.buttonColors()
        ) {
            Text(
                text = if (isReady) "Hazırım ✓" else "Hazırım",
                fontSize = 18.sp
            )
        }
        
        if (allReady) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Tüm oyuncular hazır! Tahmin ekranına geçiliyor...",
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
