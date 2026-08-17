package com.example.myapplication.data.remote

import android.content.Context
import com.google.mediapipe.tasks.genai.llminference.LlmInference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

interface OnDeviceLlmEngine {
    fun isModelReady(): Boolean
    suspend fun generate(prompt: String): String
}

class MediaPipeLlmEngine(
    private val context: Context,
    private val modelPathProvider: () -> String?
) : OnDeviceLlmEngine {

    private var llmInference: LlmInference? = null
    private var loadedModelPath: String? = null

    override fun isModelReady(): Boolean {
        val path = modelPathProvider()?.trim() ?: return false
        if (path.isEmpty()) return false
        val file = File(path)
        return file.exists() && file.canRead()
    }

    override suspend fun generate(prompt: String): String = withContext(Dispatchers.IO) {
        val path = modelPathProvider()?.trim()
            ?: throw IllegalStateException("모델 파일 경로가 설정되지 않았습니다.")
        val file = File(path)
        if (!file.exists()) {
            throw IllegalStateException("지정된 경로에 모델 파일이 없습니다: $path")
        }

        val engine = synchronized(this) {
            if (llmInference == null || loadedModelPath != path) {
                try {
                    llmInference?.close()
                } catch (_: Exception) {}

                val options = LlmInference.LlmInferenceOptions.builder()
                    .setModelPath(path)
                    .setMaxTokens(1024)
                    .build()
                llmInference = LlmInference.createFromOptions(context, options)
                loadedModelPath = path
            }
            llmInference!!
        }

        engine.generateResponse(prompt)
    }
}
