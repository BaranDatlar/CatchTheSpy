package com.milyonersgroup.catchthespy.data.model

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class GameRoom(
    var roomCode: String = "",
    var hostId: String = "",
    var category: String = "",
    var gameDuration: Int = 300, // seconds
    var players: Map<String, Player> = emptyMap(),
    var gameState: GameState = GameState.WAITING,
    var normalWord: String = "",
    var spyWord: String = "",
    var spyId: String = "",
    var gameStartTime: Long = 0L
) {
    // No-argument constructor required for Firebase
    constructor() : this("", "", "", 300, emptyMap(), GameState.WAITING, "", "", "", 0L)
}

enum class GameState {
    WAITING,      // Lobby - waiting for players
    STARTING,     // Host started the game, showing words
    PLAYING,      // Game in progress
    GUESSING,     // Voting phase
    FINISHED      // Game ended
}
