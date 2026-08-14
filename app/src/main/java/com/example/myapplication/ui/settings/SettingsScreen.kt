package com.example.myapplication.ui.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        modifier = modifier,
        topBar = { TopAppBar(title = { Text("설정") }) }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).padding(16.dp)) {
            Text("Anthropic API 키")
            OutlinedTextField(
                value = uiState.apiKeyInput,
                onValueChange = viewModel::onApiKeyChanged,
                modifier = Modifier.fillMaxWidth().testTag("apiKeyInput")
            )
            Button(
                onClick = viewModel::saveApiKey,
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text("저장")
            }
            if (uiState.isSaved) {
                Text("저장되었습니다.")
            }
        }
    }
}
