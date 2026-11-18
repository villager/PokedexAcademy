package com.example.pokedexacademy

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

data class PokemonSummary(val id: Int, val name: String) {
    val image: String get() = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/$id.png"
}
data class PokemonDetails(
    val id: Int, val name: String, val heightDm: Int, val weightHg: Int,
    val types: List<String>, val abilities: List<String>, val stats: Map<String, Int>
) { val image: String get() = PokemonSummary(id, name).image }

object PokemonRepository {
    private const val BASE = "https://pokeapi.co/api/v2/pokemon"
    private suspend fun getJson(url: String): JSONObject = withContext(Dispatchers.IO) {
        val conn = URL(url).openConnection() as HttpURLConnection
        conn.connectTimeout = 12000; conn.readTimeout = 12000
        try {
            if (conn.responseCode !in 200..299) error("HTTP ${conn.responseCode}")
            JSONObject(conn.inputStream.bufferedReader().use { it.readText() })
        } finally { conn.disconnect() }
    }
    suspend fun list(limit: Int = 151): List<PokemonSummary> {
        val entries = getJson("$BASE?limit=$limit&offset=0").getJSONArray("results")
        return (0 until entries.length()).map { idx ->
            val obj = entries.getJSONObject(idx)
            val id = obj.getString("url").trimEnd('/').substringAfterLast('/').toInt()
            PokemonSummary(id, obj.getString("name"))
        }
    }
    suspend fun detail(id: Int): PokemonDetails {
        val json = getJson("$BASE/$id")
        fun nestedNames(key: String, inner: String): List<String> {
            val a = json.getJSONArray(key)
            return (0 until a.length()).map { a.getJSONObject(it).getJSONObject(inner).getString("name") }
        }
        val a = json.getJSONArray("stats")
        val stats = (0 until a.length()).associate { i ->
            val s = a.getJSONObject(i)
            s.getJSONObject("stat").getString("name") to s.getInt("base_stat")
        }
        return PokemonDetails(id, json.getString("name"), json.getInt("height"), json.getInt("weight"),
            nestedNames("types", "type"), nestedNames("abilities", "ability"), stats)
    }
}

fun String.pretty(): String = replaceFirstChar { it.uppercase() }.replace('-', ' ')
