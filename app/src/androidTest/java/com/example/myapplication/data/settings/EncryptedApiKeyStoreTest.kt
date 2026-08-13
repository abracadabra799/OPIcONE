package com.example.myapplication.data.settings

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EncryptedApiKeyStoreTest {

    private lateinit var store: EncryptedApiKeyStore

    @Before
    fun setUp() {
        store = EncryptedApiKeyStore(ApplicationProvider.getApplicationContext())
        store.clearApiKey()
    }

    @After
    fun tearDown() {
        store.clearApiKey()
    }

    @Test
    fun getApiKey_returnsNullWhenNothingStored() {
        assertNull(store.getApiKey())
    }

    @Test
    fun setApiKey_thenGetApiKey_returnsStoredValue() {
        store.setApiKey("sk-ant-test-123")

        assertEquals("sk-ant-test-123", store.getApiKey())
    }

    @Test
    fun clearApiKey_removesStoredValue() {
        store.setApiKey("sk-ant-test-123")

        store.clearApiKey()

        assertNull(store.getApiKey())
    }
}
