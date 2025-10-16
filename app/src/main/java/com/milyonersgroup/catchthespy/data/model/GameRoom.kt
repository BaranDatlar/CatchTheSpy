package com.milyonersgroup.catchthespy.data.model

data class GameRoom(
    val roomCode: String = "",
    val hostId: String = "",
    val category: String = "",
    val gameDuration: Int = 300, // seconds
    val players: Map<String, Player> = emptyMap(),
    val gameState: GameState = GameState.WAITING,
    val normalWord: String = "",
    val spyWord: String = "",
    val spyId: String = "",
    val gameStartTime: Long = 0L
)

enum class GameState {
    WAITING,      // Lobby - waiting for players
    STARTING,     // Host started the game, showing words
    PLAYING,      // Game in progress
    GUESSING,     // Voting phase
    FINISHED      // Game ended
}
