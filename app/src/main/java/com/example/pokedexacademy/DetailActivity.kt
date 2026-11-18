package com.example.pokedexacademy

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import coil.load
import com.example.pokedexacademy.databinding.ActivityDetailBinding
import kotlinx.coroutines.launch

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater); setContentView(binding.root)
        val id = intent.getIntExtra("pokemon_id", -1)
        if (id <= 0) { finish(); return }
        val store = FavoritesStore(this)
        fun updateFavorite() { binding.favoriteButton.text = if (store.contains(id)) "♥ Quitar de favoritos" else "♡ Agregar a favoritos" }
        updateFavorite()
        binding.favoriteButton.setOnClickListener { store.toggle(id); updateFavorite() }
        binding.title.text = "Cargando Pokémon #$id..."
        lifecycleScope.launch {
            try {
                val p = PokemonRepository.detail(id)
                binding.title.text = "#${p.id} ${p.name.pretty()}"
                binding.hero.load(p.image)
                binding.types.text = "Tipos: ${p.types.joinToString { it.pretty() }}"
                binding.abilities.text = "Habilidades: ${p.abilities.joinToString { it.pretty() }}"
                binding.dimensions.text = "Altura: ${p.heightDm / 10f} m  ·  Peso: ${p.weightHg / 10f} kg"
                binding.stats.text = p.stats.entries.joinToString("\n") { "${it.key.pretty()}: ${it.value}" }
                binding.hpBar.progress = (p.stats["hp"] ?: 0).coerceIn(0, 255)
                binding.attackBar.progress = (p.stats["attack"] ?: 0).coerceIn(0, 255)
                binding.defenseBar.progress = (p.stats["defense"] ?: 0).coerceIn(0, 255)
            } catch (e: Exception) { binding.title.text = "No se pudo cargar: ${e.localizedMessage}" }
        }
    }
}
