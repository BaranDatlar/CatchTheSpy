package com.milyonersgroup.catchthespy.ui.navigation

sealed class Screen(val route: String) {
    object MainMenu : Screen("main_menu")
    object CreateRoom : Screen("create_room")
    object JoinRoom : Screen("join_room")
    object Lobby : Screen("lobby/{roomCode}") {
        fun createRoute(roomCode: String) = "lobby/$roomCode"
    }
    object Game : Screen("game/{roomCode}") {
        fun createRoute(roomCode: String) = "game/$roomCode"
    }
    object WordReveal : Screen("word_reveal/{roomCode}") {
        fun createRoute(roomCode: String) = "word_reveal/$roomCode"
    }
    object Guess : Screen("guess/{roomCode}") {
        fun createRoute(roomCode: String) = "guess/$roomCode"
    }
    object Result : Screen("result/{roomCode}/{spyWon}") {
        fun createRoute(roomCode: String, spyWon: Boolean) = "result/$roomCode/$spyWon"
    }
}
