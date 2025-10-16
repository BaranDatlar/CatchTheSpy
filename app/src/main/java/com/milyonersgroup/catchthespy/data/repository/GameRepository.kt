package com.milyonersgroup.catchthespy.data.repository

import com.google.firebase.database.*
import com.milyonersgroup.catchthespy.data.model.GameRoom
import com.milyonersgroup.catchthespy.data.model.Player
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlin.random.Random

class GameRepository {

    private val database = FirebaseDatabase.getInstance("https://catchthespy-2f5cd-default-rtdb.europe-west1.firebasedatabase.app")
    private val roomsRef = database.getReference("rooms")
    
    // Generate 6-digit room code
    fun generateRoomCode(): String {
        return Random.nextInt(100000, 999999).toString()
    }
    
    // Create a new game room
    suspend fun createRoom(gameRoom: GameRoom): Result<String> {
        return try {
            roomsRef.child(gameRoom.roomCode).setValue(gameRoom).await()
            Result.success(gameRoom.roomCode)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Join an existing room
    suspend fun joinRoom(roomCode: String, player: Player): Result<Boolean> {
        return try {
            val roomSnapshot = roomsRef.child(roomCode).get().await()
            if (!roomSnapshot.exists()) {
                return Result.failure(Exception("Room not found"))
            }
            
            roomsRef.child(roomCode).child("players").child(player.id).setValue(player).await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Observe room changes
    fun observeRoom(roomCode: String): Flow<GameRoom?> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val room = snapshot.getValue(GameRoom::class.java)
                trySend(room)
            }
            
            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        
        roomsRef.child(roomCode).addValueEventListener(listener)
        
        awaitClose {
            roomsRef.child(roomCode).removeEventListener(listener)
        }
    }
    
    // Update room data
    suspend fun updateRoom(roomCode: String, updates: Map<String, Any>): Result<Boolean> {
        return try {
            roomsRef.child(roomCode).updateChildren(updates).await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Update player ready status
    suspend fun updatePlayerReady(roomCode: String, playerId: String, isReady: Boolean): Result<Boolean> {
        return try {
            roomsRef.child(roomCode).child("players").child(playerId).child("isReady").setValue(isReady).await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Leave room
    suspend fun leaveRoom(roomCode: String, playerId: String): Result<Boolean> {
        return try {
            roomsRef.child(roomCode).child("players").child(playerId).removeValue().await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Delete room
    suspend fun deleteRoom(roomCode: String): Result<Boolean> {
        return try {
            roomsRef.child(roomCode).removeValue().await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
