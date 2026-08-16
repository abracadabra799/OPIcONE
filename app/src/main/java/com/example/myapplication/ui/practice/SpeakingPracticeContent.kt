package com.example.myapplication.ui.practice

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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
    nextLabel: String
) {
    Column {
        Text(opicQuestion?.takeIf(String::isNotBlank) ?: LEGACY_QUESTION_FALLBACK)
        Text(koreanHint, modifier = Modifier.padding(vertical = 16.dp))

        Button(
            onClick = {
                when {
                    isRecording -> onToggleRecording()
                    canRecordAudio -> onToggleRecording()
                    else -> onRequestRecordAudioPermission()
                }
            }
        ) {
            Text(if (isRecording) "녹음 중지" else "녹음 시작")
        }

        if (recordAudioPermissionDenied) {
            Text("마이크 권한 없이 모범 문장으로 연습할 수 있습니다.")
        }

        if (hasRecording) {
            Button(onClick = onPlayRecording) { Text("내 녹음 듣기") }
        }

        if (
            hasRecording ||
            recordAudioPermissionDenied ||
            speechAvailability == SpeechAvailability.Unavailable
        ) {
            Button(
                onClick = onPlayModelSentence,
                enabled = speechAvailability == SpeechAvailability.Available
            ) {
                Text("모범 문장 듣기")
            }
            if (speechAvailability == SpeechAvailability.Unavailable) {
                Text("이 기기에서는 영어 음성 재생을 사용할 수 없습니다.")
            }
            Text(englishSentence, modifier = Modifier.padding(vertical = 8.dp))
        }

        onToggleFavorite?.let { toggleFavorite ->
            Button(onClick = toggleFavorite) {
                Text(if (isFavorite) "★ 즐겨찾기 해제" else "☆ 즐겨찾기")
            }
        }

        Button(onClick = onNext) { Text(nextLabel) }
    }
}
