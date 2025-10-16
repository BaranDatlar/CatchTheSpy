package com.milyonersgroup.catchthespy.data.repository

import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.milyonersgroup.catchthespy.data.model.Category
import com.milyonersgroup.catchthespy.data.model.WordPair
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class WordRepository {

    private val database = FirebaseDatabase.getInstance("https://catchthespy-2f5cd-default-rtdb.europe-west1.firebasedatabase.app")
    private val wordsRef = database.getReference("words")

    private var cachedCategories: List<Category> = emptyList()

    suspend fun loadCategories(): List<Category> {
        return try {
            val snapshot = wordsRef.child("categories").get().await()
            val categories = mutableListOf<Category>()

            snapshot.children.forEach { categorySnapshot ->
                val id = categorySnapshot.child("id").getValue(String::class.java) ?: ""
                val name = categorySnapshot.child("name").getValue(String::class.java) ?: ""
                val wordPairs = mutableListOf<WordPair>()

                categorySnapshot.child("wordPairs").children.forEach { pairSnapshot ->
                    val normal = pairSnapshot.child("normal").getValue(String::class.java) ?: ""
                    val spy = pairSnapshot.child("spy").getValue(String::class.java) ?: ""
                    wordPairs.add(WordPair(normal, spy))
                }

                categories.add(Category(id, name, wordPairs))
            }

            cachedCategories = categories
            categories
        } catch (e: Exception) {
            e.printStackTrace()
            cachedCategories
        }
    }

    fun getCategories(): List<Category> {
        return cachedCategories
    }

    fun getCategoryById(id: String): Category? {
        return cachedCategories.find { it.id == id }
    }

    fun getRandomWordPair(categoryId: String): WordPair? {
        val category = getCategoryById(categoryId)
        return category?.wordPairs?.randomOrNull()
    }
}
