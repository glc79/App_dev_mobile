package com.example.exo4

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupSpinner()
        setupNavigation()
    }

    private fun setupSpinner() {
        val spinner: Spinner = findViewById(R.id.spinner)

        // Charger les données depuis le fichier JSON
        val laserrunItems = loadJsonFromFile("laserrun.json")

        // Extraire uniquement les `id` des objets pour afficher dans le Spinner
        val ids = mutableListOf("Veuillez sélectionner une catégorie") // Message par défaut
        ids.addAll(laserrunItems.map { it.id })

        // Créer un ArrayAdapter pour le Spinner
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item, // Layout simple pour l'élément
            ids // Utiliser les `id` comme éléments à afficher
        ).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) // Layout pour le menu déroulant
        }

        // Associer l'adaptateur au Spinner
        spinner.adapter = adapter

        // Gérer la sélection d'éléments dans le Spinner
        spinner.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>, view: android.view.View?, position: Int, id: Long) {
                if (position == 0) {
                    Toast.makeText(applicationContext, "Veuillez sélectionner une catégorie", Toast.LENGTH_SHORT).show()
                } else {
                    val selectedItem = parent.getItemAtPosition(position).toString()
                    Toast.makeText(applicationContext, "Sélection : $selectedItem", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onNothingSelected(parent: android.widget.AdapterView<*>) {
                Toast.makeText(applicationContext, "Aucune sélection", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupNavigation() {
        val buttonGps: Button = findViewById(R.id.button_gps)
        val buttonStat: Button = findViewById(R.id.button_stats)

        // Navigation vers GPSActivity
        buttonGps.setOnClickListener {
            val intent = Intent(this, GPSActivity::class.java)
            startActivity(intent)
        }

        // Navigation vers StatistiqueActivity
        buttonStat.setOnClickListener {
            val intent = Intent(this, StatistiqueActivity::class.java)
            startActivity(intent)
        }
    }

    // Fonction pour lire et parser le fichier JSON
    private fun loadJsonFromFile(filename: String): List<LaserrunItem> {
        val jsonString = assets.open(filename).bufferedReader().use { it.readText() }
        val gson = Gson()
        return gson.fromJson(jsonString, Array<LaserrunItem>::class.java).toList()
    }
}

// Modèle de données correspondant au JSON
data class LaserrunItem(
    val id: String,
    val name: String,
    val initialDistance: Int,
    val lapDistance: Int,
    val lapCount: Int,
    val shootDistance: Int
)
