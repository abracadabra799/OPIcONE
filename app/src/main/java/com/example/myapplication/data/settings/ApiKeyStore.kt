package com.example.myapplication.data.settings

interface ApiKeyStore {
    fun getApiKey(): String?
    fun setApiKey(apiKey: String)
    fun clearApiKey()
}
