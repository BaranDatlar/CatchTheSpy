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

@Composable
fun LobbyScreen(
    navController: NavController,
    viewModel: GameViewModel,
    roomCode: String
) {
    val gameRoom by viewModel.gameRoom.collectAsState()
    val currentPlayerId by viewModel.currentPlayerId.collectAsState()
    
    LaunchedEffect(gameRoom?.gameState) {
        when (gameRoom?.gameState) {
            GameState.STARTING -> {
                navController.navigate(Screen.WordReveal.createRoute(roomCode)) {
                    popUpTo(Screen.Lobby.createRoute(roomCode)) { inclusive = true }
                }
            }
            else -> {}
        }
    }
    
    DisposableEffect(Unit) {
        onDispose {
            viewModel.leaveRoom()
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text(
            text = "Oda: $roomCode",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        gameRoom?.let { room ->
            val category = viewModel.categories.collectAsState().value.find { it.id == room.category }
            Text("Kategori: ${category?.name ?: ""}")
            Text("Süre: ${room.gameDuration / 60} dakika")
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = "Oyuncular:",
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold
        )
        
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
                                text = player.name + if (player.isHost) " (Host)" else "",
                                fontWeight = if (player.id == currentPlayerId) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        val isHost = gameRoom?.hostId == currentPlayerId
        val playerCount = gameRoom?.players?.size ?: 0
        
        if (isHost) {
            Button(
                onClick = { viewModel.startGame() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = playerCount >= 2
            ) {
                Text(
                    text = if (playerCount < 2) "En az 2 oyuncu gerekli" else "Oyunu Başlat",
                    fontSize = 18.sp
                )
            }
        } else {
            Text(
                text = "Host oyunu başlatmasını bekleyin...",
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.bodyLarge
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        TextButton(
            onClick = {
                viewModel.leaveRoom()
                navController.popBackStack(Screen.MainMenu.route, false)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Odayı Terk Et")
        }
    }
}
