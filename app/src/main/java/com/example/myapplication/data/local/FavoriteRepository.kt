package com.example.myapplication.data.local

import com.example.myapplication.data.model.NewsVocabulary
import com.example.myapplication.data.model.PracticeQuestion
import com.example.myapplication.data.model.WorldNewsArticle
import kotlinx.coroutines.flow.Flow

class FavoriteRepository(private val dao: FavoriteDao) {

    fun observeFavorites(): Flow<List<FavoriteSentence>> {
        val cutoff = System.currentTimeMillis() - FavoriteSentence.RETENTION_MILLIS
        return dao.observeActive(cutoff)
    }

    suspend fun getFavorite(id: Long): FavoriteSentence? = dao.findById(id)

    suspend fun addFavorite(question: PracticeQuestion) {
        dao.insert(
            FavoriteSentence(
                category = "OPIC: ${question.category.koreanLabel}",
                opicQuestion = question.opicQuestion,
                koreanHint = question.koreanHint,
                englishSentence = question.englishSentence,
                createdAt = System.currentTimeMillis()
            )
        )
    }

    suspend fun addNewsVocabFavorite(vocab: NewsVocabulary, article: WorldNewsArticle) {
        dao.insert(
            FavoriteSentence(
                category = "NEWS_VOCAB: ${article.category}",
                opicQuestion = "[${vocab.partOfSpeech} ${vocab.phonetic}] ${vocab.exampleSentence}",
                koreanHint = vocab.meaningKorean,
                englishSentence = vocab.word,
                createdAt = System.currentTimeMillis()
            )
        )
    }

    suspend fun addNewsSentenceFavorite(article: WorldNewsArticle) {
        dao.insert(
            FavoriteSentence(
                category = "NEWS_SENTENCE: ${article.source}",
                opicQuestion = article.headline,
                koreanHint = article.keySentenceKorean,
                englishSentence = article.keySentence,
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

    suspend fun removeFavoriteByEnglishText(englishText: String) {
        dao.deleteByEnglishSentence(englishText)
    }

    suspend fun isFavorite(question: PracticeQuestion): Boolean =
        dao.isFavorite(question.englishSentence)

    suspend fun isEnglishTextFavorite(englishText: String): Boolean =
        dao.isFavorite(englishText)

    suspend fun cleanupExpired() {
        val cutoff = System.currentTimeMillis() - FavoriteSentence.RETENTION_MILLIS
        dao.deleteExpired(cutoff)
    }
}
