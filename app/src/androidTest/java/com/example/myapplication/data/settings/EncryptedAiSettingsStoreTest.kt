package com.example.myapplication.data.settings

import android.content.Context
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

    private lateinit var store: EncryptedAiSettingsStore

    @Before
    fun setUp() {
        store = EncryptedAiSettingsStore(ApplicationProvider.getApplicationContext<Context>())
        AiProvider.entries.forEach { provider -> store.clearApiKey(provider) }
        store.setSelectedProvider(AiProvider.CLAUDE)
    }

    @After
    fun tearDown() {
        AiProvider.entries.forEach { provider -> store.clearApiKey(provider) }
        store.setSelectedProvider(AiProvider.CLAUDE)
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
