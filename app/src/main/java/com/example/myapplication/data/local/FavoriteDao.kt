package com.example.myapplication.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {
    @Insert
    suspend fun insert(favorite: FavoriteSentence): Long

    @Delete
    suspend fun delete(favorite: FavoriteSentence)

    @Query("DELETE FROM favorite_sentences WHERE englishSentence = :englishSentence")
    suspend fun deleteByEnglishSentence(englishSentence: String)

    @Query("SELECT * FROM favorite_sentences ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<FavoriteSentence>>

    @Query("SELECT * FROM favorite_sentences WHERE id = :id LIMIT 1")
    suspend fun findById(id: Long): FavoriteSentence?

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_sentences WHERE englishSentence = :englishSentence)")
    suspend fun isFavorite(englishSentence: String): Boolean
}
