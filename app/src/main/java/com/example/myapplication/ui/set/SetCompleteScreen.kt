package com.example.myapplication.ui.set

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myapplication.data.session.CompletedPracticeItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetCompleteScreen(
    state: SetCompleteUiState,
    onBackToHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        topBar = { TopAppBar(title = { Text("세트 완료") }) }
    ) { innerPadding ->
        when (state) {
            SetCompleteUiState.Empty -> EmptySetCompleteContent(
                onBackToHome = onBackToHome,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp)
            )

            is SetCompleteUiState.Summary -> SummarySetCompleteContent(
                state = state,
                onBackToHome = onBackToHome,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            )
        }
    }
}

@Composable
private fun EmptySetCompleteContent(
    onBackToHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text("완료한 연습 세트가 없습니다.")
        Button(
            onClick = onBackToHome,
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("홈으로")
        }
    }
}

@Composable
private fun SummarySetCompleteContent(
    state: SetCompleteUiState.Summary,
    onBackToHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "오늘 ${state.items.size}문장을 연습했습니다.",
                style = MaterialTheme.typography.titleLarge
            )
        }
        item {
            Text("즐겨찾기 ${state.favoriteCount}개")
        }
        items(state.items) { item ->
            CompletedPracticeItemCard(item)
        }
        item {
            Button(
                onClick = onBackToHome,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("홈으로")
            }
        }
    }
}

@Composable
private fun CompletedPracticeItemCard(item: CompletedPracticeItem) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = item.question.opicQuestion,
                style = MaterialTheme.typography.titleMedium
            )
            Text(item.question.koreanHint)
            Text(item.question.englishSentence)
            if (item.isFavorite) {
                Text("★ 즐겨찾기")
            }
        }
    }
}
