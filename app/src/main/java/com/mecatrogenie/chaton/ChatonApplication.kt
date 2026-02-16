package com.mecatrogenie.chaton

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate

class ChatonApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        val sharedPref = getSharedPreferences("settings", Context.MODE_PRIVATE)
        val theme = sharedPref.getInt("theme", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        AppCompatDelegate.setDefaultNightMode(theme)
    }
}
