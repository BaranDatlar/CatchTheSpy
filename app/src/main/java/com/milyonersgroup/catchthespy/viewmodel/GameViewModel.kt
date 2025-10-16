package com.milyonersgroup.catchthespy.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.milyonersgroup.catchthespy.data.model.*
import com.milyonersgroup.catchthespy.data.repository.GameRepository
import com.milyonersgroup.catchthespy.data.repository.ScoreRepository
import com.milyonersgroup.catchthespy.data.repository.WordRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID

class GameViewModel(application: Application) : AndroidViewModel(application) {
    
    private val gameRepository = GameRepository()
    private val wordRepository = WordRepository(application)
    private val scoreRepository = ScoreRepository(application)
    private val auth = FirebaseAuth.getInstance()
    
    private val _currentPlayerId = MutableStateFlow<String?>(null)
    val currentPlayerId: StateFlow<String?> = _currentPlayerId.asStateFlow()
    
    private val _currentRoomCode = MutableStateFlow<String?>(null)
    val currentRoomCode: StateFlow<String?> = _currentRoomCode.asStateFlow()
    
    private val _gameRoom = MutableStateFlow<GameRoom?>(null)
    val gameRoom: StateFlow<GameRoom?> = _gameRoom.asStateFlow()
    
    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    init {
        initializeAuth()
        loadCategories()
    }
    
    private fun initializeAuth() {
        viewModelScope.launch {
            try {
                // Anonymous authentication
                if (auth.currentUser == null) {
                    auth.signInAnonymously().addOnSuccessListener {
                        _currentPlayerId.value = it.user?.uid ?: UUID.randomUUID().toString()
                    }.addOnFailureListener {
                        _currentPlayerId.value = UUID.randomUUID().toString()
                    }
                } else {
                    _currentPlayerId.value = auth.currentUser?.uid ?: UUID.randomUUID().toString()
                }
            } catch (e: Exception) {
                _currentPlayerId.value = UUID.randomUUID().toString()
            }
        }
    }
    
    private fun loadCategories() {
        _categories.value = wordRepository.getCategories()
    }
    
    fun createRoom(playerName: String, categoryId: String, duration: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val playerId = _currentPlayerId.value ?: return@launch
                val roomCode = gameRepository.generateRoomCode()
                
                val host = Player(
                    id = playerId,
                    name = playerName,
                    isHost = true,
                    isReady = false
                )
                
                val room = GameRoom(
                    roomCode = roomCode,
                    hostId = playerId,
                    category = categoryId,
                    gameDuration = duration,
                    players = mapOf(playerId to host),
                    gameState = GameState.WAITING
                )
                
                gameRepository.createRoom(room).onSuccess {
                    _currentRoomCode.value = roomCode
                    scoreRepository.setPlayerName(playerName)
                    observeRoom(roomCode)
                }.onFailure {
                    _error.value = "Oda oluşturulamadı: ${it.message}"
                }
            } catch (e: Exception) {
                _error.value = "Hata: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun joinRoom(roomCode: String, playerName: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val playerId = _currentPlayerId.value ?: return@launch
                
                val player = Player(
                    id = playerId,
                    name = playerName,
                    isHost = false,
                    isReady = false
                )
                
                gameRepository.joinRoom(roomCode, player).onSuccess {
                    _currentRoomCode.value = roomCode
                    scoreRepository.setPlayerName(playerName)
                    observeRoom(roomCode)
                }.onFailure {
                    _error.value = "Odaya katılınamadı: ${it.message}"
                }
            } catch (e: Exception) {
                _error.value = "Hata: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    private fun observeRoom(roomCode: String) {
        viewModelScope.launch {
            gameRepository.observeRoom(roomCode).collect { room ->
                _gameRoom.value = room
            }
        }
    }
    
    fun startGame() {
        viewModelScope.launch {
            try {
                val roomCode = _currentRoomCode.value ?: return@launch
                val room = _gameRoom.value ?: return@launch
                val categoryId = room.category
                
                // Get random word pair
                val wordPair = wordRepository.getRandomWordPair(categoryId) ?: return@launch
                
                // Select random spy
                val playerIds = room.players.keys.toList()
                val spyId = playerIds.random()
                
                // Assign words to players
                val updatedPlayers = room.players.mapValues { (playerId, player) ->
                    player.copy(
                        isSpy = playerId == spyId,
                        word = if (playerId == spyId) wordPair.spy else wordPair.normal
                    )
                }
                
                val updates = mapOf(
                    "gameState" to GameState.STARTING.name,
                    "normalWord" to wordPair.normal,
                    "spyWord" to wordPair.spy,
                    "spyId" to spyId,
                    "players" to updatedPlayers,
                    "gameStartTime" to System.currentTimeMillis()
                )
                
                gameRepository.updateRoom(roomCode, updates)
            } catch (e: Exception) {
                _error.value = "Oyun başlatılamadı: ${e.message}"
            }
        }
    }
    
    fun setPlayerReady(isReady: Boolean) {
        viewModelScope.launch {
            val roomCode = _currentRoomCode.value ?: return@launch
            val playerId = _currentPlayerId.value ?: return@launch
            gameRepository.updatePlayerReady(roomCode, playerId, isReady)
        }
    }
    
    fun updateGameState(newState: GameState) {
        viewModelScope.launch {
            val roomCode = _currentRoomCode.value ?: return@launch
            gameRepository.updateRoom(roomCode, mapOf("gameState" to newState.name))
        }
    }
    
    fun submitGuess(spyWon: Boolean) {
        viewModelScope.launch {
            val roomCode = _currentRoomCode.value ?: return@launch
            val playerId = _currentPlayerId.value ?: return@launch
            val room = _gameRoom.value ?: return@launch
            
            // Update local score
            val currentPlayer = room.players[playerId]
            if (currentPlayer?.isSpy == true) {
                if (spyWon) scoreRepository.incrementWins() else scoreRepository.incrementLosses()
            } else {
                if (!spyWon) scoreRepository.incrementWins() else scoreRepository.incrementLosses()
            }
            
            gameRepository.updateRoom(roomCode, mapOf("gameState" to GameState.FINISHED.name))
        }
    }
    
    fun leaveRoom() {
        viewModelScope.launch {
            val roomCode = _currentRoomCode.value ?: return@launch
            val playerId = _currentPlayerId.value ?: return@launch
            val room = _gameRoom.value ?: return@launch
            
            if (room.hostId == playerId) {
                // If host leaves, delete the room
                gameRepository.deleteRoom(roomCode)
            } else {
                gameRepository.leaveRoom(roomCode, playerId)
            }
            
            _currentRoomCode.value = null
            _gameRoom.value = null
        }
    }
    
    fun clearError() {
        _error.value = null
    }
    
    fun getPlayerName(): String = scoreRepository.getPlayerName()
    fun getWins(): Int = scoreRepository.getWins()
    fun getLosses(): Int = scoreRepository.getLosses()
}
