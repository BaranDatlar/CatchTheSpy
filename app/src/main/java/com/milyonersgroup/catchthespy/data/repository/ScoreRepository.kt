package com.milyonersgroup.catchthespy.data.repository

import android.content.Context
import android.content.SharedPreferences

class ScoreRepository(context: Context) {
    
    private val prefs: SharedPreferences = context.getSharedPreferences("game_scores", Context.MODE_PRIVATE)
    
    fun getWins(): Int {
        return prefs.getInt("wins", 0)
    }
    
    fun getLosses(): Int {
        return prefs.getInt("losses", 0)
    }
    
    fun incrementWins() {
        prefs.edit().putInt("wins", getWins() + 1).apply()
    }
    
    fun incrementLosses() {
        prefs.edit().putInt("losses", getLosses() + 1).apply()
    }
    
    fun resetScores() {
        prefs.edit().clear().apply()
    }
    
    fun getPlayerName(): String {
        return prefs.getString("player_name", "") ?: ""
    }
    
    fun setPlayerName(name: String) {
        prefs.edit().putString("player_name", name).apply()
    }
}
