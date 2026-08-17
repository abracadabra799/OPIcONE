package com.example.myapplication.ui.news

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Badge
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
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
import com.example.myapplication.data.model.NewsVocabulary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsDetailScreen(
    viewModel: NewsDetailViewModel,
    onBack: () -> Unit,
    canRecordAudio: Boolean = true,
    recordAudioPermissionDenied: Boolean = false,
    onRequestRecordAudioPermission: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val article = uiState.article

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("오늘의 세계 뉴스") },
                navigationIcon = {
                    TextButton(onClick = onBack) { Text("목록") }
                }
            )
        }
    ) { innerPadding ->
        if (article == null) {
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("기사를 불러올 수 없습니다.")
                Button(onClick = onBack, modifier = Modifier.padding(top = 8.dp)) {
                    Text("목록으로 돌아가기")
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Header & Badges
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Badge(containerColor = MaterialTheme.colorScheme.primary) {
                                Text(article.source, color = MaterialTheme.colorScheme.onPrimary)
                            }
                            Badge(containerColor = MaterialTheme.colorScheme.secondaryContainer) {
                                Text(article.category, color = MaterialTheme.colorScheme.onSecondaryContainer)
                            }
                        }
                        Badge(containerColor = MaterialTheme.colorScheme.tertiaryContainer) {
                            Text(
                                text = "📅 ${article.publishedTime}",
                                color = MaterialTheme.colorScheme.onTertiaryContainer,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                    Text(
                        text = article.headline,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                    Text(
                        text = article.headlineKorean,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                // 3-Point Summary Card
                item {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = "💡 3줄 핵심 요약 & 브리핑",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            article.summaryPoints.forEachIndexed { index, point ->
                                Text(
                                    text = "${index + 1}. $point",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }

                // Full Article & TTS Player Card
                item {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "📰 영문 기사 전문 (Full Script)",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )

                                if (uiState.isPlayingFullArticle) {
                                    Button(onClick = viewModel::stopAudio) {
                                        Text("⏹ 음성 정지")
                                    }
                                } else {
                                    Button(onClick = viewModel::playFullArticle) {
                                        Text("🎧 전체 음성 듣기")
                                    }
                                }
                            }

                            Text(
                                text = article.fullScript,
                                style = MaterialTheme.typography.bodyLarge,
                                lineHeight = MaterialTheme.typography.bodyLarge.lineHeight * 1.3
                            )
                        }
                    }
                }

                // Key Vocabulary Section
                item {
                    Text(
                        text = "📚 꼭 알아야 할 핵심 & 빈출 어휘 (${article.keyVocabularies.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                items(article.keyVocabularies) { vocab ->
                    val isFav = vocab.word in uiState.favoriteKeys
                    VocabItemCard(
                        vocab = vocab,
                        isFavorite = isFav,
                        onPlay = { viewModel.playVocabulary(vocab) },
                        onToggleFavorite = { viewModel.toggleVocabFavorite(vocab) }
                    )
                }

                // Key Sentence Speaking Practice
                item {
                    val isSentenceFav = article.keySentence in uiState.favoriteKeys
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = "🎙 오늘의 핵심 문장 쉐도잉 발화 연습",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = article.keySentence,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = article.keySentenceKorean,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.padding(top = 4.dp)
                            ) {
                                Button(
                                    onClick = {
                                        when {
                                            uiState.isRecording -> viewModel.stopRecording()
                                            canRecordAudio -> viewModel.startRecording()
                                            else -> onRequestRecordAudioPermission()
                                        }
                                    }
                                ) {
                                    Text(if (uiState.isRecording) "🎙 녹음 중지" else "🎙 낭독 녹음")
                                }

                                if (uiState.hasRecording) {
                                    Button(onClick = viewModel::playMyRecording) {
                                        Text("🎧 내 목소리")
                                    }
                                }

                                OutlinedButton(onClick = viewModel::playKeySentence) {
                                    Text("🔊 모범 발음")
                                }

                                OutlinedButton(onClick = viewModel::toggleSentenceFavorite) {
                                    Text(if (isSentenceFav) "★ 즐겨찾기됨" else "☆ 즐겨찾기")
                                }
                            }

                            if (recordAudioPermissionDenied) {
                                Text("마이크 권한을 허용하면 실시간 발화 평가를 받을 수 있습니다.")
                            }

                            uiState.evaluationResult?.let { eval ->
                                if (eval.spokenText.isNotBlank()) {
                                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                                    Text("인식된 발화: \"${eval.spokenText}\"", style = MaterialTheme.typography.bodySmall)
                                    Text("일치율: ${eval.accuracyScore}% — ${eval.feedbackMessage}", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

@Composable
private fun VocabItemCard(
    vocab: NewsVocabulary,
    isFavorite: Boolean,
    onPlay: () -> Unit,
    onToggleFavorite: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = vocab.word,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = vocab.phonetic,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = vocab.partOfSpeech,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Badge(containerColor = MaterialTheme.colorScheme.tertiaryContainer) {
                    Text(vocab.tag.label, style = MaterialTheme.typography.labelSmall)
                }
            }

            Text(
                text = vocab.meaningKorean,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )

            Text(
                text = "예문: \"${vocab.exampleSentence}\"",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 2.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(onClick = onPlay) {
                    Text("🔊 발음 듣기")
                }
                Spacer(modifier = Modifier.padding(horizontal = 4.dp))
                OutlinedButton(onClick = onToggleFavorite) {
                    Text(if (isFavorite) "★ 즐겨찾기됨" else "☆ 6일 즐겨찾기")
                }
            }
        }
    }
}
