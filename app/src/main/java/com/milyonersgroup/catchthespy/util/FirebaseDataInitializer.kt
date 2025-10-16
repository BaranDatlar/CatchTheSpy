package com.milyonersgroup.catchthespy.util

import com.google.firebase.database.FirebaseDatabase
import com.milyonersgroup.catchthespy.data.model.Category
import com.milyonersgroup.catchthespy.data.model.WordPair

object FirebaseDataInitializer {

    fun initializeWords() {
        val database = FirebaseDatabase.getInstance("https://catchthespy-2f5cd-default-rtdb.europe-west1.firebasedatabase.app")
        val wordsRef = database.getReference("words")

        val categories = listOf(
            Category(
                id = "celebrities",
                name = "Ünlüler",
                wordPairs = listOf(
                    WordPair("Tarkan", "Sezen Aksu"),
                    WordPair("Kemal Sunal", "Şener Şen"),
                    WordPair("Atatürk", "İnönü"),
                    WordPair("Cristiano Ronaldo", "Lionel Messi"),
                    WordPair("Elon Musk", "Jeff Bezos"),
                    WordPair("Beyonce", "Rihanna"),
                    WordPair("Tom Cruise", "Brad Pitt"),
                    WordPair("Nusret", "CZN Burak"),
                    WordPair("Cem Yılmaz", "Ata Demirer"),
                    WordPair("Hadise", "Demet Akalın")
                )
            ),
            Category(
                id = "foods",
                name = "Yemekler",
                wordPairs = listOf(
                    WordPair("Lahmacun", "Pide"),
                    WordPair("Döner", "Şiş Kebap"),
                    WordPair("Mantı", "Ravioli"),
                    WordPair("Baklava", "Künefe"),
                    WordPair("Kumpir", "Patates Kızartması"),
                    WordPair("İskender", "Adana Kebap"),
                    WordPair("Menemen", "Omlet"),
                    WordPair("Çiğ Köfte", "İçli Köfte"),
                    WordPair("Börek", "Gözleme"),
                    WordPair("Sushi", "Ramen")
                )
            ),
            Category(
                id = "movies",
                name = "Filmler",
                wordPairs = listOf(
                    WordPair("Titanic", "Avatar"),
                    WordPair("Harry Potter", "Lord of the Rings"),
                    WordPair("Star Wars", "Star Trek"),
                    WordPair("Matrix", "Inception"),
                    WordPair("Recep İvedik", "GORA"),
                    WordPair("Eyvah Eyvah", "Eyyvah Eyvah 2"),
                    WordPair("Organize İşler", "Düğün Dernek"),
                    WordPair("Avengers", "Justice League"),
                    WordPair("Joker", "Batman"),
                    WordPair("Frozen", "Moana")
                )
            )
        )

        // Upload to Firebase
        wordsRef.child("categories").setValue(categories)
    }
}
