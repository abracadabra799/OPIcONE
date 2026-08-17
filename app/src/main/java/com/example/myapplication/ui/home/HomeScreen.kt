package com.example.myapplication.ui.home

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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.myapplication.data.model.PracticeCategory
import com.example.myapplication.ui.favorites.FavoritesScreen
import com.example.myapplication.ui.favorites.FavoritesViewModel
import com.example.myapplication.ui.news.NewsScreen
import com.example.myapplication.ui.news.NewsViewModel

enum class MainTab(val title: String, val iconText: String) {
    OPIC_SPEAKING("오픽 1등급", "🎓"),
    WORLD_NEWS("세계 주요뉴스", "🌍"),
    SMART_FAVORITES("6일 즐겨찾기", "⭐")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onCategorySelected: (PracticeCategory) -> Unit,
    onArticleSelected: (String) -> Unit,
    onPracticeFavorite: (Long) -> Unit,
    onOpenSettings: () -> Unit,
    newsViewModel: NewsViewModel,
    favoritesViewModel: FavoritesViewModel,
    modifier: Modifier = Modifier
) {
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("speakMore", fontWeight = FontWeight.Bold)
                        Badge(containerColor = MaterialTheme.colorScheme.primaryContainer) {
                            Text(
                                text = MainTab.entries[selectedTab].title,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                },
                actions = {
                    TextButton(onClick = onOpenSettings) {
                        Text("⚙️ 설정")
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                MainTab.entries.forEachIndexed { index, tab ->
                    NavigationBarItem(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        icon = {
                            Text(tab.iconText, style = MaterialTheme.typography.titleMedium)
                        },
                        label = { Text(tab.title) }
                    )
                }
            }
        }
    ) { innerPadding ->
        when (selectedTab) {
            0 -> OpicCategoriesTab(
                onCategorySelected = onCategorySelected,
                modifier = Modifier.padding(innerPadding)
            )
            1 -> NewsScreen(
                viewModel = newsViewModel,
                onArticleSelected = onArticleSelected,
                modifier = Modifier.padding(innerPadding)
            )
            2 -> FavoritesScreen(
                viewModel = favoritesViewModel,
                onPracticeFavorite = onPracticeFavorite,
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}

@Composable
private fun OpicCategoriesTab(
    onCategorySelected: (PracticeCategory) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "🎓 OPIc AL 1등급 스피킹 마스터",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "8대 핵심 영역별 AL 모범 답변 스크립트와 영어 어순 한국어 청크 힌트로 실전 스피킹을 연습하세요. (1회 5문제)",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }

        items(PracticeCategory.entries) { category ->
            val isSpecial = category == PracticeCategory.LOUNGE_REVIEW
            Card(
                onClick = { onCategorySelected(category) },
                colors = CardDefaults.cardColors(
                    containerColor = if (isSpecial) {
                        MaterialTheme.colorScheme.tertiaryContainer
                    } else {
                        MaterialTheme.colorScheme.surfaceVariant
                    }
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = category.koreanLabel,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (isSpecial) MaterialTheme.colorScheme.onTertiaryContainer else MaterialTheme.colorScheme.onSurface
                            )
                            if (isSpecial) {
                                Badge(containerColor = MaterialTheme.colorScheme.primary) {
                                    Text(
                                        text = "NEW 수강 복습",
                                        color = MaterialTheme.colorScheme.onPrimary,
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                }
                            }
                        }
                        Text(
                            text = category.promptTopic,
                            style = MaterialTheme.typography.bodySmall,
                            color = if (isSpecial) MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Text("▶ 시작", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
