package com.example.myapplication.data.local

import com.example.myapplication.data.model.PracticeQuestion
import kotlinx.coroutines.flow.Flow

class FavoriteRepository(private val dao: FavoriteDao) {

    fun observeFavorites(): Flow<List<FavoriteSentence>> = dao.observeAll()

    suspend fun addFavorite(question: PracticeQuestion) {
        dao.insert(
            FavoriteSentence(
                category = question.category.name,
                koreanHint = question.koreanHint,
                englishSentence = question.englishSentence,
                createdAt = System.currentTimeMillis()
            )
        )
    }

    suspend fun removeFavorite(favorite: FavoriteSentence) {
        dao.delete(favorite)
    }

    suspend fun removeFavoriteByQuestion(question: PracticeQuestion) {
        dao.deleteByEnglishSentence(question.englishSentence)
    }

    suspend fun isFavorite(question: PracticeQuestion): Boolean =
        dao.isFavorite(question.englishSentence)
}
