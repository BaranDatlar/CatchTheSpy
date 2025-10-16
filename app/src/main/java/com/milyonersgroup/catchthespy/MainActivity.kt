package com.milyonersgroup.catchthespy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.milyonersgroup.catchthespy.ui.navigation.Screen
import com.milyonersgroup.catchthespy.ui.screens.*
import com.milyonersgroup.catchthespy.ui.theme.CatchTheSpyTheme
import com.milyonersgroup.catchthespy.viewmodel.GameViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CatchTheSpyTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    CatchTheSpyApp(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun CatchTheSpyApp(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val viewModel: GameViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = Screen.MainMenu.route,
        modifier = modifier
    ) {
        composable(Screen.MainMenu.route) {
            MainMenuScreen(navController = navController, viewModel = viewModel)
        }

        composable(Screen.CreateRoom.route) {
            CreateRoomScreen(navController = navController, viewModel = viewModel)
        }

        composable(Screen.JoinRoom.route) {
            JoinRoomScreen(navController = navController, viewModel = viewModel)
        }

        composable(
            route = Screen.Lobby.route,
            arguments = listOf(navArgument("roomCode") { type = NavType.StringType })
        ) { backStackEntry ->
            val roomCode = backStackEntry.arguments?.getString("roomCode") ?: ""
            LobbyScreen(navController = navController, viewModel = viewModel, roomCode = roomCode)
        }

        composable(
            route = Screen.WordReveal.route,
            arguments = listOf(navArgument("roomCode") { type = NavType.StringType })
        ) { backStackEntry ->
            val roomCode = backStackEntry.arguments?.getString("roomCode") ?: ""
            WordRevealScreen(navController = navController, viewModel = viewModel, roomCode = roomCode)
        }

        composable(
            route = Screen.Game.route,
            arguments = listOf(navArgument("roomCode") { type = NavType.StringType })
        ) { backStackEntry ->
            val roomCode = backStackEntry.arguments?.getString("roomCode") ?: ""
            GameScreen(navController = navController, viewModel = viewModel, roomCode = roomCode)
        }

        composable(
            route = Screen.Guess.route,
            arguments = listOf(navArgument("roomCode") { type = NavType.StringType })
        ) { backStackEntry ->
            val roomCode = backStackEntry.arguments?.getString("roomCode") ?: ""
            GuessScreen(navController = navController, viewModel = viewModel, roomCode = roomCode)
        }

        composable(
            route = Screen.Result.route,
            arguments = listOf(
                navArgument("roomCode") { type = NavType.StringType },
                navArgument("spyWon") { type = NavType.BoolType }
            )
        ) { backStackEntry ->
            val roomCode = backStackEntry.arguments?.getString("roomCode") ?: ""
            val spyWon = backStackEntry.arguments?.getBoolean("spyWon") ?: false
            ResultScreen(navController = navController, viewModel = viewModel, roomCode = roomCode, spyWon = spyWon)
        }
    }
}