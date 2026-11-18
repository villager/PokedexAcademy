package com.example.pokedexacademy

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.pokedexacademy.databinding.ActivityMainBinding
import com.example.pokedexacademy.databinding.ItemPokemonBinding
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater); setContentView(binding.root)
        binding.btnCatalog.setOnClickListener { startActivity(Intent(this, CatalogActivity::class.java)) }
        binding.btnFavorites.setOnClickListener { startActivity(Intent(this, FavoritesActivity::class.java)) }
        binding.btnSettings.setOnClickListener { startActivity(Intent(this, SettingsActivity::class.java)) }
        binding.featuredList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.txtStatus.text = "Cargando Pokémon destacados..."
        lifecycleScope.launch {
            try {
                val pokemon = PokemonRepository.list(12)
                binding.featuredList.adapter = PokemonAdapter(pokemon) { openDetail(it.id) }
                binding.txtStatus.text = "Descubre los primeros 12 Pokémon"
            } catch (ex: Exception) {
                binding.txtStatus.text = "No se pudo cargar: ${ex.localizedMessage ?: "error de conexión"}"
            }
        }
    }
    private fun openDetail(id: Int) { startActivity(Intent(this, DetailActivity::class.java).putExtra("pokemon_id", id)) }
}

class PokemonAdapter(private val items: List<PokemonSummary>, private val click: (PokemonSummary) -> Unit) : RecyclerView.Adapter<PokemonAdapter.Holder>() {
    class Holder(val binding: ItemPokemonBinding) : RecyclerView.ViewHolder(binding.root)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder = Holder(ItemPokemonBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    override fun getItemCount() = items.size
    override fun onBindViewHolder(holder: Holder, position: Int) {
        val item = items[position]
        holder.binding.pokemonName.text = "#${item.id} ${item.name.pretty()}"
        holder.binding.pokemonImage.load(item.image)
        holder.binding.root.setOnClickListener { click(item) }
    }
}
