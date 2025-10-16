package com.milyonersgroup.catchthespy.data.model

data class Category(
    val id: String,
    val name: String,
    val wordPairs: List<WordPair>
)

data class WordPair(
    val normal: String,
    val spy: String
)

data class WordData(
    val categories: List<Category>
)
