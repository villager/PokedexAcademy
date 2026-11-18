package com.example.pokedexacademy

import android.content.Context

class FavoritesStore(context: Context) {
    private val prefs = context.getSharedPreferences("pokedex_favorites", Context.MODE_PRIVATE)
    fun ids(): Set<Int> = prefs.getStringSet("ids", emptySet()).orEmpty().mapNotNull { it.toIntOrNull() }.toSet()
    fun contains(id: Int) = id in ids()
    fun toggle(id: Int): Boolean {
        val next = ids().toMutableSet()
        val added = if (id in next) { next.remove(id); false } else { next.add(id); true }
        prefs.edit().putStringSet("ids", next.map { it.toString() }.toSet()).apply()
        return added
    }
}
