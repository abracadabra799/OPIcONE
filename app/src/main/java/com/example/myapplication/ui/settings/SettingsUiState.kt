package com.example.myapplication.ui.settings

import com.example.myapplication.data.remote.AiProvider

data class SettingsUiState(
    val selectedProvider: AiProvider = AiProvider.CLAUDE,
    val apiKeyInput: String = "",
    val hasStoredKey: Boolean = false,
    val savedProvider: AiProvider? = null
)
