package com.example.myapplication.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
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
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("AI 문제 생성 엔진 선택", style = MaterialTheme.typography.titleMedium)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = uiState.selectedProvider == AiProvider.LOCAL_BANK,
                    onClick = { viewModel.selectProvider(AiProvider.LOCAL_BANK) },
                    label = { Text("내장 마스터 뱅크") },
                    modifier = Modifier.testTag("providerLocalBank")
                )
                FilterChip(
                    selected = uiState.selectedProvider == AiProvider.ON_DEVICE_LLM,
                    onClick = { viewModel.selectProvider(AiProvider.ON_DEVICE_LLM) },
                    label = { Text("온디바이스 LLM") },
                    modifier = Modifier.testTag("providerOnDeviceLlm")
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
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

            when (uiState.selectedProvider) {
                AiProvider.LOCAL_BANK -> {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("✨ 제로 API 키 & 제로 레이턴시", style = MaterialTheme.typography.titleSmall)
                            Text(
                                "내장 OPIc AL 마스터 뱅크가 활성화되어 있습니다.\n" +
                                "API 키 입력 및 네트워크 연결 없이 8개 영역 실전 문제를 즉시 0초 로딩으로 무제한 연습할 수 있습니다.",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }

                AiProvider.ON_DEVICE_LLM -> {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("📱 On-Device SLM (MediaPipe / LiteRT)", style = MaterialTheme.typography.titleSmall)
                            Text(
                                "기기 로컬에 다운로드된 Gemma 2B 또는 Llama 3.2 모델 파일(.bin, .task)을 지정해 GPU/NPU 가속으로 온디바이스 생성을 수행합니다. (미설정 시 내장 뱅크 자동 연동)",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }

                    Text("모델 파일 절대 경로 (.bin / .task)")
                    OutlinedTextField(
                        value = uiState.modelPathInput,
                        onValueChange = viewModel::onModelPathChanged,
                        placeholder = { Text("/sdcard/Download/gemma-2b-it-gpu-int4.bin") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("modelPathInput")
                    )
                    Button(onClick = viewModel::saveModelPath) {
                        Text("경로 저장")
                    }
                    if (uiState.isModelSaved && !uiState.storedModelPath.isNullOrBlank()) {
                        Text("모델 경로가 설정되었습니다: ${uiState.storedModelPath}")
                    }
                }

                AiProvider.CLAUDE, AiProvider.OPENAI -> {
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
    }
}
