package com.example.myapplication.data.settings

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

private const val PREFS_FILE_NAME = "opic_practice_secure_prefs"
private const val KEY_API_KEY = "anthropic_api_key"

class EncryptedApiKeyStore(context: Context) : ApiKeyStore {

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val prefs = EncryptedSharedPreferences.create(
        context,
        PREFS_FILE_NAME,
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    override fun getApiKey(): String? = prefs.getString(KEY_API_KEY, null)

    override fun setApiKey(apiKey: String) {
        prefs.edit().putString(KEY_API_KEY, apiKey).apply()
    }

    override fun clearApiKey() {
        prefs.edit().remove(KEY_API_KEY).apply()
    }
}
