package com.example.myapplication.ui.favorites

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myapplication.ui.practice.SpeakingPracticeContent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritePracticeScreen(
    viewModel: FavoritePracticeViewModel,
    onComplete: () -> Unit,
    onBack: () -> Unit,
    canRecordAudio: Boolean = true,
    recordAudioPermissionDenied: Boolean = false,
    onRequestRecordAudioPermission: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    BackHandler(onBack = onBack)

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("즐겨찾기 전체 연습") },
                navigationIcon = {
                    TextButton(onClick = onBack) { Text("목록") }
                }
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).padding(16.dp)) {
            when (val state = uiState) {
                FavoritePracticeUiState.Loading -> {
                    Text("즐겨찾기 문장을 불러오는 중입니다...")
                }

                FavoritePracticeUiState.NotFound -> {
                    Text("즐겨찾기 문장을 찾을 수 없습니다.")
                    Button(onClick = onBack) { Text("목록으로") }
                }

                is FavoritePracticeUiState.Ready -> {
                    if (state.isComplete) {
                        LaunchedEffect(state.isComplete) { onComplete() }
                    } else {
                        SpeakingPracticeContent(
                            opicQuestion = state.favorite.opicQuestion,
                            koreanHint = state.favorite.koreanHint,
                            englishSentence = state.favorite.englishSentence,
                            isRecording = state.isRecording,
                            hasRecording = state.hasRecording,
                            isFavorite = true,
                            speechAvailability = state.speechAvailability,
                            canRecordAudio = canRecordAudio,
                            recordAudioPermissionDenied = recordAudioPermissionDenied,
                            onToggleRecording = {
                                if (state.isRecording) {
                                    viewModel.stopRecording()
                                } else {
                                    viewModel.startRecording()
                                }
                            },
                            onRequestRecordAudioPermission = onRequestRecordAudioPermission,
                            onPlayRecording = viewModel::playMyRecording,
                            onPlayModelSentence = viewModel::playModelSentence,
                            onToggleFavorite = null,
                            onNext = viewModel::complete,
                            nextLabel = "연습 완료"
                        )
                    }
                }
            }
        }
    }
}
