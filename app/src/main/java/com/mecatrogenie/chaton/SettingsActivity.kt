package com.mecatrogenie.chaton

import android.content.Context
import android.os.Bundle
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.switchmaterial.SwitchMaterial

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val toolbar: MaterialToolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val notificationSwitch: SwitchMaterial = findViewById(R.id.notification_switch)
        val themeRadioGroup: RadioGroup = findViewById(R.id.theme_radio_group)

        val sharedPref = getSharedPreferences("settings", Context.MODE_PRIVATE)

        // Notifications
        val notificationsEnabled = sharedPref.getBoolean("notifications_enabled", true)
        notificationSwitch.isChecked = notificationsEnabled

        notificationSwitch.setOnCheckedChangeListener { _, isChecked ->
            with(sharedPref.edit()) {
                putBoolean("notifications_enabled", isChecked)
                apply()
            }
        }

        // Theme
        val currentTheme = sharedPref.getInt("theme", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        when (currentTheme) {
            AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM -> findViewById<RadioButton>(R.id.theme_system).isChecked = true
            AppCompatDelegate.MODE_NIGHT_NO -> findViewById<RadioButton>(R.id.theme_light).isChecked = true
            AppCompatDelegate.MODE_NIGHT_YES -> findViewById<RadioButton>(R.id.theme_dark).isChecked = true
        }

        themeRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            val newTheme = when (checkedId) {
                R.id.theme_system -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                R.id.theme_light -> AppCompatDelegate.MODE_NIGHT_NO
                R.id.theme_dark -> AppCompatDelegate.MODE_NIGHT_YES
                else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            }
            AppCompatDelegate.setDefaultNightMode(newTheme)
            with(sharedPref.edit()) {
                putInt("theme", newTheme)
                apply()
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}
