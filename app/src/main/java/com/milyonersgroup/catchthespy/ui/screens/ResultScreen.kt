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
import com.milyonersgroup.catchthespy.ui.navigation.Screen
import com.milyonersgroup.catchthespy.viewmodel.GameViewModel

@Composable
fun ResultScreen(
    navController: NavController,
    viewModel: GameViewModel,
    roomCode: String,
    spyWon: Boolean
) {
    val gameRoom by viewModel.gameRoom.collectAsState()
    val currentPlayerId by viewModel.currentPlayerId.collectAsState()
    
    val currentPlayer = gameRoom?.players?.get(currentPlayerId)
    val iWasSpy = currentPlayer?.isSpy ?: false
    val iWon = (iWasSpy && spyWon) || (!iWasSpy && !spyWon)
    
    val spyPlayer = gameRoom?.players?.values?.find { it.isSpy }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = if (spyWon) "Spy Kazandı!" else "Spy Yakalandı!",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = if (spyWon) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = if (iWon) 
                    MaterialTheme.colorScheme.primaryContainer 
                else 
                    MaterialTheme.colorScheme.errorContainer
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (iWon) "Kazandınız! 🎉" else "Kaybettiniz 😔",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text("Spy: ${spyPlayer?.name ?: "?"}")
                Text("Spy Kelimesi: ${gameRoom?.spyWord ?: ""}")
                Text("Normal Kelime: ${gameRoom?.normalWord ?: ""}")
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        val wins = viewModel.getWins()
        val losses = viewModel.getLosses()
        
        Text("Toplam Kazanma: $wins | Kaybetme: $losses")
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Button(
            onClick = {
                navController.navigate(Screen.MainMenu.route) {
                    popUpTo(0) { inclusive = true }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text("Ana Menüye Dön", fontSize = 18.sp)
        }
    }
}
