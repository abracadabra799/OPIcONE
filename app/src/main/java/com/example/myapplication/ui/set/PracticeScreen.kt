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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PracticeScreen(
    viewModel: PracticeSetViewModel,
    onSetComplete: (Int) -> Unit,
    onBack: () -> Unit,
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
                    Button(onClick = viewModel::loadSet) { Text("다시 시도") }
                }
                is PracticeSetUiState.Ready -> {
                    if (state.isSetComplete) {
                        LaunchedEffect(Unit) { onSetComplete(state.questions.size) }
                    } else {
                        PracticeReadyContent(state = state, viewModel = viewModel)
                    }
                }
            }
        }
    }
}

@Composable
private fun PracticeReadyContent(
    state: PracticeSetUiState.Ready,
    viewModel: PracticeSetViewModel
) {
    val question = state.questions[state.currentIndex]

    Text("${state.currentIndex + 1} / ${state.questions.size}")
    Text(question.koreanHint, modifier = Modifier.padding(vertical = 16.dp))

    Button(onClick = {
        if (state.isRecording) viewModel.stopRecording() else viewModel.startRecording()
    }) {
        Text(if (state.isRecording) "녹음 중지" else "녹음 시작")
    }

    if (state.hasRecording) {
        Button(onClick = viewModel::playMyRecording) { Text("내 녹음 듣기") }
        Button(onClick = viewModel::playModelSentence) { Text("모범 문장 듣기") }
        Text(question.englishSentence, modifier = Modifier.padding(vertical = 8.dp))
    }

    Button(onClick = viewModel::toggleFavorite) {
        Text(if (state.isCurrentFavorite) "★ 즐겨찾기 해제" else "☆ 즐겨찾기")
    }

    Button(onClick = viewModel::nextQuestion) { Text("다음 문제") }
}
