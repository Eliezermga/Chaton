package com.mecatrogenie.chaton

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.switchmaterial.SwitchMaterial

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val notificationSwitch: SwitchMaterial = findViewById(R.id.notification_switch)

        val sharedPref = getSharedPreferences("settings", Context.MODE_PRIVATE)
        val notificationsEnabled = sharedPref.getBoolean("notifications_enabled", true)
        notificationSwitch.isChecked = notificationsEnabled

        notificationSwitch.setOnCheckedChangeListener { _, isChecked ->
            with(sharedPref.edit()) {
                putBoolean("notifications_enabled", isChecked)
                apply()
            }
        }
    }
}
