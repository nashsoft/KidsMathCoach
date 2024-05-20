package com.example.kidsmathcoach.repository

import android.content.Context
import com.example.kidsmathcoach.enums.Operation
import com.example.kidsmathcoach.models.Settings
import com.google.gson.Gson
import java.io.File

class SettingsRepositoryImpl(private val context: Context) : SettingsRepository {
    private val settingsFilePath: String
        get() = File(context.filesDir, "settings.json").absolutePath

    override fun saveSettings(settings: Settings) {
        val json = Gson().toJson(settings)
        File(settingsFilePath).writeText(json)
    }

    override fun loadSettings(): Settings {
        val file = File(settingsFilePath)
        return if (file.exists()) {
            val json = file.readText()
            Gson().fromJson(json, Settings::class.java)
        } else {
            //Сохранение настроек по-умолчанию (Первый запуск или после очистки кеша)
            Settings("Username", 1, Operation.ADD, 0, 0)
        }
    }
}

