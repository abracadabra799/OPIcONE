package com.example.myapplication.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_sentences")
data class FavoriteSentence(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val category: String,
    val opicQuestion: String? = null,
    val koreanHint: String,
    val englishSentence: String,
    val createdAt: Long
)
