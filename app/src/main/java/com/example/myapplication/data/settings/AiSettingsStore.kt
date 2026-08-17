package com.example.myapplication.data.settings

import com.example.myapplication.data.remote.AiProvider

interface AiSettingsStore {
    fun getSelectedProvider(): AiProvider
    fun setSelectedProvider(provider: AiProvider)
    fun getApiKey(provider: AiProvider): String?
    fun setApiKey(provider: AiProvider, apiKey: String)
    fun clearApiKey(provider: AiProvider)
    fun getModelPath(): String?
    fun setModelPath(path: String)
}
