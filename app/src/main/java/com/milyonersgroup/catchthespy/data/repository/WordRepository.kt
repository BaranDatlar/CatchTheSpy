package com.milyonersgroup.catchthespy.data.repository

import android.content.Context
import com.google.gson.Gson
import com.milyonersgroup.catchthespy.data.model.Category
import com.milyonersgroup.catchthespy.data.model.WordData
import com.milyonersgroup.catchthespy.data.model.WordPair

class WordRepository(private val context: Context) {
    
    private var wordData: WordData? = null
    
    init {
        loadWords()
    }
    
    private fun loadWords() {
        try {
            val json = context.assets.open("words.json").bufferedReader().use { it.readText() }
            wordData = Gson().fromJson(json, WordData::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    fun getCategories(): List<Category> {
        return wordData?.categories ?: emptyList()
    }
    
    fun getCategoryById(id: String): Category? {
        return wordData?.categories?.find { it.id == id }
    }
    
    fun getRandomWordPair(categoryId: String): WordPair? {
        val category = getCategoryById(categoryId)
        return category?.wordPairs?.randomOrNull()
    }
}
