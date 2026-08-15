package com.example.myapplication.data.settings

import com.example.myapplication.data.remote.AiProvider

interface AiSettingsStore {
    fun getSelectedProvider(): AiProvider
    fun setSelectedProvider(provider: AiProvider)
    fun getApiKey(provider: AiProvider): String?
    fun setApiKey(provider: AiProvider, apiKey: String)
    fun clearApiKey(provider: AiProvider)
}

/**
 * Temporary bridge for call sites that will migrate to [AiSettingsStore].
 *
 * Its no-argument API always addresses Claude's legacy API-key slot.
 */
@Deprecated("Use AiSettingsStore with an explicit AiProvider")
interface ApiKeyStore {
    fun getApiKey(): String?
    fun setApiKey(apiKey: String)
    fun clearApiKey()
}
