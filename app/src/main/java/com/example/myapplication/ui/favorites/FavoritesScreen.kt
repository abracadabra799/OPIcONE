package com.example.myapplication.ui.favorites

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myapplication.audio.SpeechAvailability

private const val LEGACY_QUESTION_FALLBACK =
    "저장된 질문 없음 — 힌트를 보고 답해보세요."

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    viewModel: FavoritesViewModel,
    onPracticeFavorite: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val favorites by viewModel.favorites.collectAsState()
    val speechAvailability by viewModel.speechAvailability.collectAsState()

    Scaffold(
        modifier = modifier,
        topBar = { TopAppBar(title = { Text("즐겨찾기") }) }
    ) { innerPadding ->
        LazyColumn(modifier = Modifier.padding(innerPadding)) {
            if (speechAvailability == SpeechAvailability.Unavailable) {
                item {
                    Text(
                        "이 기기에서는 영어 음성 재생을 사용할 수 없습니다.",
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
            }
            items(favorites, key = { it.id }) { favorite ->
                Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)) {
                    Text(
                        favorite.opicQuestion ?: LEGACY_QUESTION_FALLBACK,
                        modifier = Modifier.padding(16.dp)
                    )
                    Text(favorite.koreanHint, modifier = Modifier.padding(16.dp))
                    Text(favorite.englishSentence, modifier = Modifier.padding(horizontal = 16.dp))
                    TextButton(
                        onClick = { viewModel.playFavorite(favorite) },
                        enabled = speechAvailability == SpeechAvailability.Available
                    ) {
                        Text("바로 듣기")
                    }
                    TextButton(onClick = { onPracticeFavorite(favorite.id) }) {
                        Text("전체 연습")
                    }
                    TextButton(onClick = { viewModel.removeFavorite(favorite) }) {
                        Text("삭제")
                    }
                }
            }
        }
    }
}
