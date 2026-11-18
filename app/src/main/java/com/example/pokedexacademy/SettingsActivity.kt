package com.example.pokedexacademy

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.pokedexacademy.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val b = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(b.root)
        val prefs = getSharedPreferences("pokedex_settings", MODE_PRIVATE)
        b.switchNotifications.isChecked = prefs.getBoolean("notifications", false)
        b.checkboxCompact.isChecked = prefs.getBoolean("compact", false)
        b.seekVolume.progress = prefs.getInt("volume", 50)
        if (prefs.getString("sort", "id") == "name") b.radioName.isChecked = true else b.radioId.isChecked = true
        b.btnSave.setOnClickListener {
            prefs.edit().putBoolean("notifications", b.switchNotifications.isChecked)
                .putBoolean("compact", b.checkboxCompact.isChecked)
                .putInt("volume", b.seekVolume.progress)
                .putString("sort", if (b.radioName.isChecked) "name" else "id").apply()
            Toast.makeText(this, "Preferencias guardadas (demostración)", Toast.LENGTH_SHORT).show()
        }
    }
}
