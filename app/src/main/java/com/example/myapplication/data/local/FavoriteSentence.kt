package com.example.myapplication.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlin.math.ceil

@Entity(tableName = "favorite_sentences")
data class FavoriteSentence(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val category: String,
    val opicQuestion: String? = null,
    val koreanHint: String,
    val englishSentence: String,
    val createdAt: Long
) {
    fun daysRemaining(now: Long = System.currentTimeMillis()): Int {
        val expiryTime = createdAt + RETENTION_MILLIS
        val diff = expiryTime - now
        return if (diff <= 0) 1 else ceil(diff / 86400000.0).toInt().coerceIn(1, 6)
    }

    fun isNewsVocab(): Boolean = category.startsWith("NEWS_VOCAB")
    fun isNewsSentence(): Boolean = category.startsWith("NEWS_SENTENCE")
    fun isOpic(): Boolean = !isNewsVocab() && !isNewsSentence()

    companion object {
        const val RETENTION_DAYS = 6L
        const val RETENTION_MILLIS = RETENTION_DAYS * 24 * 60 * 60 * 1000L
    }
}
