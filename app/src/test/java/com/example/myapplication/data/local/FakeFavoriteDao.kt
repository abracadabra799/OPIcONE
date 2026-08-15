package com.example.myapplication.data.local

import kotlinx.coroutines.flow.MutableStateFlow

class FakeFavoriteDao : FavoriteDao {

    private val favorites = MutableStateFlow<List<FavoriteSentence>>(emptyList())
    private var nextId = 1L

    override suspend fun insert(favorite: FavoriteSentence): Long {
        val stored = favorite.copy(id = nextId++)
        favorites.value = favorites.value + stored
        return stored.id
    }

    override suspend fun delete(favorite: FavoriteSentence) {
        favorites.value = favorites.value.filterNot { it.id == favorite.id }
    }

    override suspend fun deleteByEnglishSentence(englishSentence: String) {
        favorites.value = favorites.value.filterNot { it.englishSentence == englishSentence }
    }

    override fun observeAll() = favorites

    override suspend fun findById(id: Long): FavoriteSentence? =
        favorites.value.firstOrNull { it.id == id }

    override suspend fun isFavorite(englishSentence: String): Boolean =
        favorites.value.any { it.englishSentence == englishSentence }
}
