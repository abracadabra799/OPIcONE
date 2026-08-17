package com.example.myapplication.ui.favorites

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Badge
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.myapplication.audio.SpeechAvailability
import com.example.myapplication.data.local.FavoriteSentence

private const val LEGACY_QUESTION_FALLBACK =
    "저장된 질문 없음 — 힌트를 보고 답해보세요."

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    viewModel: FavoritesViewModel,
    onPracticeFavorite: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val favorites by viewModel.filteredFavorites.collectAsState()
    val allFavorites by viewModel.allFavorites.collectAsState()
    val selectedFilter by viewModel.selectedFilter.collectAsState()
    val speechAvailability by viewModel.speechAvailability.collectAsState()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Info Banner
        item {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "⭐ 6일 스마트 복습 보관함",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "오픽 스피킹 문장과 세계 뉴스 단어·문장이 6일간 안전하게 보관되며, 6일 경과 시 망각 곡선 주기에 맞춰 자동 정리됩니다.",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }

        // Filter Row
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(FavoriteFilter.entries) { filter ->
                    val count = when (filter) {
                        FavoriteFilter.ALL -> allFavorites.size
                        FavoriteFilter.OPIC -> allFavorites.count { it.isOpic() }
                        FavoriteFilter.NEWS_VOCAB -> allFavorites.count { it.isNewsVocab() }
                        FavoriteFilter.NEWS_SENTENCE -> allFavorites.count { it.isNewsSentence() }
                    }
                    FilterChip(
                        selected = selectedFilter == filter,
                        onClick = { viewModel.setFilter(filter) },
                        label = { Text("${filter.label} ($count)") }
                    )
                }
            }
        }

        if (speechAvailability == SpeechAvailability.Unavailable) {
            item {
                Text(
                    "이 기기에서는 영어 음성 재생을 사용할 수 없습니다.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }

        if (favorites.isEmpty()) {
            item {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "저장된 즐겨찾기 항목이 없습니다.",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "오픽 연습이나 세계 뉴스에서 [☆ 즐겨찾기]를 눌러 문장과 단어를 추가해보세요!",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 6.dp)
                        )
                    }
                }
            }
        }

        items(favorites, key = { it.id }) { favorite ->
            FavoriteCardItem(
                favorite = favorite,
                onPlay = { viewModel.playFavorite(favorite) },
                onPractice = { onPracticeFavorite(favorite.id) },
                onRemove = { viewModel.removeFavorite(favorite) },
                speechEnabled = speechAvailability == SpeechAvailability.Available
            )
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun FavoriteCardItem(
    favorite: FavoriteSentence,
    onPlay: () -> Unit,
    onPractice: () -> Unit,
    onRemove: () -> Unit,
    speechEnabled: Boolean,
    modifier: Modifier = Modifier
) {
    val daysLeft = favorite.daysRemaining()
    val badgeColor = if (daysLeft <= 1) {
        MaterialTheme.colorScheme.error
    } else {
        MaterialTheme.colorScheme.primary
    }

    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Badge(containerColor = MaterialTheme.colorScheme.secondaryContainer) {
                    Text(
                        text = favorite.category,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        style = MaterialTheme.typography.labelSmall
                    )
                }

                Badge(containerColor = badgeColor) {
                    Text(
                        text = if (daysLeft <= 1) "D-1 (오늘/내일 만료)" else "D-$daysLeft 남음",
                        color = MaterialTheme.colorScheme.onError.takeIf { daysLeft <= 1 }
                            ?: MaterialTheme.colorScheme.onPrimary,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            favorite.opicQuestion?.let { q ->
                if (q.isNotBlank()) {
                    Text(
                        text = q,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Text(
                text = favorite.koreanHint,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = favorite.englishSentence,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = onPlay,
                    enabled = speechEnabled
                ) {
                    Text("🔊 바로 듣기")
                }
                Spacer(modifier = Modifier.padding(horizontal = 4.dp))
                OutlinedButton(onClick = onPractice) {
                    Text("🎙 스피킹 연습")
                }
                Spacer(modifier = Modifier.padding(horizontal = 4.dp))
                TextButton(onClick = onRemove) {
                    Text("삭제", color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}
