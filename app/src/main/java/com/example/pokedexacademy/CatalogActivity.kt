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
import kotlinx.coroutines.launch

class CatalogActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var query by remember { mutableStateOf("") }
            var pokemon by remember { mutableStateOf<List<PokemonSummary>>(emptyList()) }
            var loading by remember { mutableStateOf(true) }
            var error by remember { mutableStateOf<String?>(null) }
            var retry by remember { mutableIntStateOf(0) }
            LaunchedEffect(retry) {
                loading = true; error = null
                try { pokemon = PokemonRepository.list(151) }
                catch (e: Exception) { error = e.localizedMessage ?: "Error de red" }
                finally { loading = false }
            }
            MaterialTheme {
                Column(Modifier.fillMaxSize().padding(16.dp)) {
                    Text("Pokédex · Catálogo", style = MaterialTheme.typography.headlineMedium)
                    Spacer(Modifier.height(12.dp))
                    OutlinedTextField(value = query, onValueChange = { query = it },
                        label = { Text("Buscar por nombre o número") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                    Spacer(Modifier.height(8.dp))
                    when {
                        loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
                        error != null -> Column { Text("Error: $error"); Button(onClick = { retry++ }) { Text("Reintentar") } }
                        else -> {
                            val filtered = pokemon.filter { it.name.contains(query.trim(), ignoreCase = true) || it.id.toString().contains(query.trim()) }
                            Text("${filtered.size} resultados")
                            LazyColumn(contentPadding = PaddingValues(vertical = 8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(filtered, key = { it.id }) { p ->
                                    Card(Modifier.fillMaxWidth().clickable {
                                        startActivity(Intent(this@CatalogActivity, DetailActivity::class.java).putExtra("pokemon_id", p.id))
                                    }) {
                                        Row(Modifier.fillMaxWidth().padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                                            AsyncImage(model = p.image, contentDescription = p.name, modifier = Modifier.size(74.dp))
                                            Spacer(Modifier.width(16.dp))
                                            Column { Text(p.name.pretty(), style = MaterialTheme.typography.titleMedium); Text("#${p.id.toString().padStart(3, '0')}") }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
