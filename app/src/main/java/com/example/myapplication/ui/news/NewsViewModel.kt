package com.example.myapplication.ui.news

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.audio.SpeechPlayer
import com.example.myapplication.data.local.FavoriteRepository
import com.example.myapplication.data.model.NewsVocabulary
import com.example.myapplication.data.model.WorldNewsArticle
import com.example.myapplication.data.remote.WorldNewsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class NewsListUiState(
    val articles: List<WorldNewsArticle> = emptyList(),
    val favoriteKeys: Set<String> = emptySet()
)

class NewsViewModel(
    private val newsRepository: WorldNewsRepository,
    private val favoriteRepository: FavoriteRepository,
    private val speechPlayer: SpeechPlayer
) : ViewModel() {

    private val _uiState = MutableStateFlow(NewsListUiState())
    val uiState: StateFlow<NewsListUiState> = _uiState.asStateFlow()

    init {
        loadNews()
        observeFavorites()
    }

    private fun loadNews() {
        val news = newsRepository.getTodayNews()
        _uiState.value = _uiState.value.copy(articles = news)
    }

    private fun observeFavorites() {
        viewModelScope.launch {
            favoriteRepository.observeFavorites().collect { favorites ->
                val keys = favorites.map { it.englishSentence }.toSet()
                _uiState.value = _uiState.value.copy(favoriteKeys = keys)
            }
        }
    }

    fun playHeadline(article: WorldNewsArticle) {
        speechPlayer.speak(article.headline)
    }

    override fun onCleared() {
        speechPlayer.release()
    }
}
