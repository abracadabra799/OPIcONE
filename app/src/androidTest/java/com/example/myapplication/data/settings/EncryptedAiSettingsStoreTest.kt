package com.example.myapplication.data.settings

import android.content.Context
import android.content.SharedPreferences
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.myapplication.data.remote.AiProvider
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EncryptedAiSettingsStoreTest {

    private lateinit var prefs: SharedPreferences
    private lateinit var store: EncryptedAiSettingsStore

    @Before
    fun setUp() {
        prefs = createEncryptedPrefs(ApplicationProvider.getApplicationContext<Context>())
        prefs.edit().remove("selected_ai_provider").commit()
        store = EncryptedAiSettingsStore(prefs)
        AiProvider.entries.forEach { provider -> store.clearApiKey(provider) }
    }

    @After
    fun tearDown() {
        AiProvider.entries.forEach { provider -> store.clearApiKey(provider) }
        prefs.edit().remove("selected_ai_provider").commit()
    }

    @Test
    fun defaultsToClaude() {
        assertEquals(AiProvider.CLAUDE, store.getSelectedProvider())
    }

    @Test
    fun storesKeysIndependently() {
        store.setApiKey(AiProvider.CLAUDE, "claude-key")
        store.setApiKey(AiProvider.OPENAI, "openai-key")

        assertEquals("claude-key", store.getApiKey(AiProvider.CLAUDE))
        assertEquals("openai-key", store.getApiKey(AiProvider.OPENAI))
    }

    @Test
    fun persistsOpenAiSelectionAcrossStoreInstances() {
        store.setSelectedProvider(AiProvider.OPENAI)

        val recreatedStore = EncryptedAiSettingsStore(prefs)

        assertEquals(AiProvider.OPENAI, recreatedStore.getSelectedProvider())
    }

    @Test
    fun clearingOpenAiDoesNotClearClaude() {
        store.setApiKey(AiProvider.CLAUDE, "claude-key")
        store.setApiKey(AiProvider.OPENAI, "openai-key")

        store.clearApiKey(AiProvider.OPENAI)

        assertEquals("claude-key", store.getApiKey(AiProvider.CLAUDE))
        assertNull(store.getApiKey(AiProvider.OPENAI))
    }

    @Test
    fun readsLegacyClaudeApiKey() {
        val prefs = createEncryptedPrefs(ApplicationProvider.getApplicationContext<Context>())
        prefs.edit().putString("anthropic_api_key", "legacy-claude-key").commit()
        val legacyStore = EncryptedAiSettingsStore(prefs)

        assertEquals("legacy-claude-key", legacyStore.getApiKey(AiProvider.CLAUDE))
    }
}
