package com.example.myapplication.ui.set

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
fun PracticeScreen(
    viewModel: PracticeSetViewModel,
    onSetComplete: () -> Unit,
    onBack: () -> Unit,
    onOpenSettings: () -> Unit = {},
    canRecordAudio: Boolean = true,
    recordAudioPermissionDenied: Boolean = false,
    onRequestRecordAudioPermission: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        modifier = modifier,
        topBar = { TopAppBar(title = { Text("오늘의 세트") }) }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).padding(16.dp)) {
            when (val state = uiState) {
                is PracticeSetUiState.Loading -> Text("문제를 만드는 중입니다...")
                is PracticeSetUiState.Error -> {
                    Text(state.message)
                    if (state.showSettingsAction) {
                        Button(onClick = onOpenSettings) { Text("설정으로 이동") }
                    }
                    Button(onClick = viewModel::loadSet) { Text("다시 시도") }
                }
                is PracticeSetUiState.Ready -> {
                    if (state.isSetComplete) {
                        LaunchedEffect(Unit) { onSetComplete() }
                    } else {
                        val question = state.questions[state.currentIndex]
                        Text("${state.currentIndex + 1} / ${state.questions.size}")
                        SpeakingPracticeContent(
                            opicQuestion = question.opicQuestion,
                            koreanHint = question.koreanHint,
                            englishSentence = question.englishSentence,
                            isRecording = state.isRecording,
                            hasRecording = state.hasRecording,
                            isFavorite = state.isCurrentFavorite,
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
                            onToggleFavorite = viewModel::toggleFavorite,
                            onNext = viewModel::nextQuestion,
                            nextLabel = "다음 문제"
                        )
                    }
                }
            }
        }
    }
}
