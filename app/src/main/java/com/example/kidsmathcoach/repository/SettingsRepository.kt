package com.example.kidsmathcoach.repository

import com.example.kidsmathcoach.models.Settings

interface SettingsRepository {
    fun saveSettings(settings: Settings)
    fun loadSettings(): Settings
}