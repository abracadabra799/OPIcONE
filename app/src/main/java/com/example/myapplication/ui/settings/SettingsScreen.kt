package com.example.myapplication.ui.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.myapplication.data.remote.AiProvider

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
        Column(
            modifier = Modifier.padding(innerPadding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = uiState.selectedProvider == AiProvider.CLAUDE,
                    onClick = { viewModel.selectProvider(AiProvider.CLAUDE) },
                    label = { Text(AiProvider.CLAUDE.displayName) },
                    modifier = Modifier.testTag("providerClaude")
                )
                FilterChip(
                    selected = uiState.selectedProvider == AiProvider.OPENAI,
                    onClick = { viewModel.selectProvider(AiProvider.OPENAI) },
                    label = { Text(AiProvider.OPENAI.displayName) },
                    modifier = Modifier.testTag("providerOpenAi")
                )
            }
            Text("${uiState.selectedProvider.displayName} API 키")
            Text(
                if (uiState.hasStoredKey) {
                    "${uiState.selectedProvider.displayName} API 키가 등록되어 있습니다."
                } else {
                    "${uiState.selectedProvider.displayName} API 키가 등록되어 있지 않습니다."
                }
            )
            OutlinedTextField(
                value = uiState.apiKeyInput,
                onValueChange = viewModel::onApiKeyChanged,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    autoCorrectEnabled = false,
                    imeAction = ImeAction.Done
                ),
                singleLine = true,
                modifier = Modifier.fillMaxWidth().testTag("apiKeyInput")
            )
            Button(onClick = viewModel::saveApiKey) {
                Text("저장")
            }
            if (uiState.hasStoredKey) {
                Button(
                    onClick = viewModel::clearApiKey,
                    modifier = Modifier.testTag("clearApiKey")
                ) {
                    Text("API 키 삭제")
                }
            }
            uiState.savedProvider?.let { provider ->
                Text("${provider.displayName} API 키를 저장했습니다.")
            }
        }
    }
}
