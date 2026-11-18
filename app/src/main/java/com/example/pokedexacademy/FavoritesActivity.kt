package com.example.pokedexacademy

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

class FavoritesActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val store = FavoritesStore(this)
        setContent {
            var ids by remember { mutableStateOf(store.ids()) }
            MaterialTheme {
                Column(Modifier.fillMaxSize().padding(16.dp)) {
                    Text("Mis favoritos ♥", style = MaterialTheme.typography.headlineMedium)
                    Spacer(Modifier.height(16.dp))
                    if (ids.isEmpty()) Text("Todavía no guardaste Pokémon. Abre el catálogo y agrega uno.")
                    else LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(ids.sorted(), key = { it }) { id ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                AsyncImage(model = PokemonSummary(id, "").image, contentDescription = "Pokémon $id", modifier = Modifier.size(72.dp))
                                Text("Pokémon #$id", modifier = Modifier.weight(1f).clickable {
                                    startActivity(Intent(this@FavoritesActivity, DetailActivity::class.java).putExtra("pokemon_id", id))
                                })
                                TextButton(onClick = { store.toggle(id); ids = store.ids() }) { Text("Quitar") }
                            }
                        }
                    }
                }
            }
        }
    }
    override fun onResume() { super.onResume() }
}
