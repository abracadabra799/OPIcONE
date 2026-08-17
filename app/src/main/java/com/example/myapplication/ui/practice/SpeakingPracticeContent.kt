package com.example.myapplication.ui.practice

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.myapplication.audio.SpeakingEvaluationResult
import com.example.myapplication.audio.SpeechAvailability

private const val LEGACY_QUESTION_FALLBACK = "저장된 질문 없음 — 힌트를 보고 답해보세요."

@Composable
fun SpeakingPracticeContent(
    opicQuestion: String?,
    koreanHint: String,
    englishSentence: String,
    isRecording: Boolean,
    hasRecording: Boolean,
    isFavorite: Boolean,
    speechAvailability: SpeechAvailability,
    canRecordAudio: Boolean,
    recordAudioPermissionDenied: Boolean,
    onToggleRecording: () -> Unit,
    onRequestRecordAudioPermission: () -> Unit,
    onPlayRecording: () -> Unit,
    onPlayModelSentence: () -> Unit,
    onToggleFavorite: (() -> Unit)?,
    onNext: () -> Unit,
    nextLabel: String,
    evaluationResult: SpeakingEvaluationResult? = null,
    isAnswerRevealed: Boolean = false,
    onRevealAnswer: (() -> Unit)? = null
) {
    val isModelAnswerVisible = isAnswerRevealed ||
        hasRecording ||
        recordAudioPermissionDenied ||
        speechAvailability == SpeechAvailability.Unavailable

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = opicQuestion?.takeIf(String::isNotBlank) ?: LEGACY_QUESTION_FALLBACK,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text("🇰🇷 영어 어순 한국어 힌트", style = MaterialTheme.typography.labelMedium)
                Text(
                    text = koreanHint,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = {
                    when {
                        isRecording -> onToggleRecording()
                        canRecordAudio -> onToggleRecording()
                        else -> onRequestRecordAudioPermission()
                    }
                }
            ) {
                Text(if (isRecording) "🎙 녹음 중지" else "🎙 녹음 시작")
            }

            if (!isModelAnswerVisible && onRevealAnswer != null) {
                OutlinedButton(onClick = onRevealAnswer) {
                    Text("👀 답변 바로 보기")
                }
            }

            if (hasRecording) {
                Button(onClick = onPlayRecording) {
                    Text("🎧 내 녹음 듣기")
                }
            }
        }

        if (recordAudioPermissionDenied) {
            Text("마이크 권한 없이 모범 문장으로 연습할 수 있습니다.")
        }

        if (isModelAnswerVisible) {
            Button(
                onClick = onPlayModelSentence,
                enabled = speechAvailability == SpeechAvailability.Available
            ) {
                Text("🔊 모범 문장 듣기 (TTS)")
            }
            if (speechAvailability == SpeechAvailability.Unavailable) {
                Text("이 기기에서는 영어 음성 재생을 사용할 수 없습니다.")
            }

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("🇺🇸 AL 모범 문장", style = MaterialTheme.typography.labelMedium)
                    Text(
                        text = englishSentence,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            evaluationResult?.let { eval ->
                if (eval.spokenText.isNotBlank()) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.tertiaryContainer
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("📊 실시간 발화 분석 (온디바이스 STT)", style = MaterialTheme.typography.labelMedium)
                            Text("인식된 발화: \"${eval.spokenText}\"", style = MaterialTheme.typography.bodyMedium)
                            Text("일치율: ${eval.accuracyScore}% — ${eval.feedbackMessage}", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            onToggleFavorite?.let { toggleFavorite ->
                Button(onClick = toggleFavorite) {
                    Text(if (isFavorite) "★ 즐겨찾기 해제" else "☆ 즐겨찾기")
                }
            }

            Button(onClick = onNext) {
                Text(nextLabel)
            }
        }
    }
}
