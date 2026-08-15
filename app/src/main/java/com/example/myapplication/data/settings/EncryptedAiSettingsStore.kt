package com.example.myapplication.data.settings

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.example.myapplication.data.remote.AiProvider

private const val PREFS_FILE_NAME = "opic_practice_secure_prefs"
private const val KEY_SELECTED_PROVIDER = "selected_ai_provider"
private const val KEY_LEGACY_CLAUDE_API_KEY = "anthropic_api_key"
private const val KEY_OPENAI_API_KEY = "openai_api_key"

class EncryptedAiSettingsStore internal constructor(
    private val prefs: SharedPreferences
) : AiSettingsStore {

    constructor(context: Context) : this(createEncryptedPrefs(context))

    override fun getSelectedProvider(): AiProvider =
        prefs.getString(KEY_SELECTED_PROVIDER, null)
            ?.let { stored -> AiProvider.entries.firstOrNull { it.name == stored } }
            ?: AiProvider.CLAUDE

    override fun setSelectedProvider(provider: AiProvider) {
        prefs.edit().putString(KEY_SELECTED_PROVIDER, provider.name).apply()
    }

    override fun getApiKey(provider: AiProvider): String? =
        prefs.getString(keyFor(provider), null)

    override fun setApiKey(provider: AiProvider, apiKey: String) {
        prefs.edit().putString(keyFor(provider), apiKey).apply()
    }

    override fun clearApiKey(provider: AiProvider) {
        prefs.edit().remove(keyFor(provider)).apply()
    }

    private fun keyFor(provider: AiProvider) = when (provider) {
        AiProvider.CLAUDE -> KEY_LEGACY_CLAUDE_API_KEY
        AiProvider.OPENAI -> KEY_OPENAI_API_KEY
    }
}

internal fun createEncryptedPrefs(context: Context): SharedPreferences {
    val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    return EncryptedSharedPreferences.create(
        context,
        PREFS_FILE_NAME,
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )
}
