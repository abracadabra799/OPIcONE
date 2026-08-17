package com.example.myapplication.ui.favorites

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.audio.SpeechAvailability
import com.example.myapplication.audio.SpeechPlayer
import com.example.myapplication.data.local.FavoriteRepository
import com.example.myapplication.data.local.FavoriteSentence
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class FavoriteFilter(val label: String) {
    ALL("전체"),
    OPIC("🎓 오픽 AL"),
    NEWS_VOCAB("📰 뉴스 단어"),
    NEWS_SENTENCE("📑 뉴스 문장")
}

class FavoritesViewModel(
    private val favoriteRepository: FavoriteRepository,
    private val speechPlayer: SpeechPlayer
) : ViewModel() {

    private val _selectedFilter = MutableStateFlow(FavoriteFilter.ALL)
    val selectedFilter: StateFlow<FavoriteFilter> = _selectedFilter

    val allFavorites: StateFlow<List<FavoriteSentence>> = favoriteRepository.observeFavorites()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val filteredFavorites: StateFlow<List<FavoriteSentence>> = combine(
        allFavorites,
        _selectedFilter
    ) { list, filter ->
        when (filter) {
            FavoriteFilter.ALL -> list
            FavoriteFilter.OPIC -> list.filter { it.isOpic() }
            FavoriteFilter.NEWS_VOCAB -> list.filter { it.isNewsVocab() }
            FavoriteFilter.NEWS_SENTENCE -> list.filter { it.isNewsSentence() }
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val favorites: StateFlow<List<FavoriteSentence>> = filteredFavorites
    val speechAvailability: StateFlow<SpeechAvailability> = speechPlayer.availability

    init {
        viewModelScope.launch {
            favoriteRepository.cleanupExpired()
        }
    }

    fun setFilter(filter: FavoriteFilter) {
        _selectedFilter.value = filter
    }

    fun playFavorite(favorite: FavoriteSentence) {
        if (speechAvailability.value == SpeechAvailability.Available) {
            speechPlayer.speak(favorite.englishSentence)
        }
    }

    fun removeFavorite(favorite: FavoriteSentence) {
        viewModelScope.launch {
            favoriteRepository.removeFavorite(favorite)
        }
    }

    @VisibleForTesting
    internal fun clearForTest() = onCleared()

    override fun onCleared() {
        speechPlayer.release()
    }
}
