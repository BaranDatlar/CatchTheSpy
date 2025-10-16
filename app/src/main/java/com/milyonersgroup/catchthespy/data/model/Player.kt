package com.milyonersgroup.catchthespy.data.model

data class Player(
    val id: String = "",
    val name: String = "",
    val isHost: Boolean = false,
    val isReady: Boolean = false,
    val isSpy: Boolean = false,
    val word: String = ""
)
