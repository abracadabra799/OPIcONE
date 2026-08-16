package com.example.myapplication.ui.favorites

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.audio.SpeechAvailability
import com.example.myapplication.audio.SpeechPlayer
import com.example.myapplication.data.local.FavoriteRepository
import com.example.myapplication.data.local.FavoriteSentence
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FavoritesViewModel(
    private val favoriteRepository: FavoriteRepository,
    private val speechPlayer: SpeechPlayer
) : ViewModel() {

    val favorites: StateFlow<List<FavoriteSentence>> = favoriteRepository.observeFavorites()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
    val speechAvailability: StateFlow<SpeechAvailability> = speechPlayer.availability

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
