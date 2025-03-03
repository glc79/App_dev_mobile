package com.example.exo4

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.exo4.model.LaserRunCategory
import com.example.exo4.network.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CategoryActivity : AppCompatActivity() {

    // Déclarez une variable pour stocker les catégories
    private lateinit var categories: List<LaserRunCategory>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category)

        val spinner: Spinner = findViewById(R.id.spinner)
        val descriptionText: TextView = findViewById(R.id.description_text)
        val detailsText: TextView = findViewById(R.id.details_text)

        // Charge les catégories et configure le spinner
        fetchCategories { categoriesList ->
            categories = categoriesList // Affectez les catégories ici
            setupSpinner(spinner, categories) { selectedCategory ->
                updateDetails(selectedCategory, descriptionText, detailsText)
            }
        }
        setupNavigation()
    }

    private fun fetchCategories(onResult: (List<LaserRunCategory>) -> Unit) {
        RetrofitInstance.apiService.getLaserrunItems()
            .enqueue(object : Callback<List<LaserRunCategory>> {
                override fun onResponse(
                    call: Call<List<LaserRunCategory>>,
                    response: Response<List<LaserRunCategory>>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        onResult(response.body()!!)
                    } else {
                        Toast.makeText(
                            this@CategoryActivity,
                            "Erreur de chargement des catégories",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<List<LaserRunCategory>>, t: Throwable) {
                    Toast.makeText(
                        this@CategoryActivity,
                        "Erreur réseau : ${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    private fun setupSpinner(
        spinner: Spinner,
        categories: List<LaserRunCategory>,
        onItemSelected: (LaserRunCategory) -> Unit
    ) {
        val categoryNames = categories.map { it.name }
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categoryNames).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        spinner.adapter = adapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedCategory = categories[position]
                onItemSelected(selectedCategory)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Gestion si nécessaire
            }
        }
    }

    private fun updateDetails(category: LaserRunCategory, descriptionText: TextView, detailsText: TextView) {
        descriptionText.text = "Catégorie : ${category.name}"
        detailsText.text = """
            Distance initiale : ${category.initialDistance} m
            Distance par tour : ${category.lapDistance} m
            Nombre de tours : ${category.lapCount}
            Distance de tir : ${category.shootDistance} m
        """.trimIndent()
    }

    private fun setupNavigation() {
        val buttonCourir: Button = findViewById(R.id.button_courir)
        val spinner: Spinner = findViewById(R.id.spinner)

        buttonCourir.setOnClickListener {
            // Récupère la position de l'élément sélectionné dans le spinner
            val selectedCategoryPosition = spinner.selectedItemPosition
            // Vérifie si une catégorie est sélectionnée
            if (selectedCategoryPosition != AdapterView.INVALID_POSITION) {
                // Récupère l'objet LaserRunCategory associé à la position
                val selectedCategory = categories[selectedCategoryPosition] // Utilise la variable de classe

                // Passe les données de la catégorie via Intent
                val intent = Intent(this, CourirActivity::class.java)
                intent.putExtra("LAP_COUNT", selectedCategory.lapCount) // Nombre de tours
                intent.putExtra("INITIAL_DISTANCE", selectedCategory.initialDistance) // Distance initiale
                intent.putExtra("SHOOT_DISTANCE", selectedCategory.shootDistance) // Distance de tir
                startActivity(intent)
            } else {
                Toast.makeText(this, "Veuillez sélectionner une catégorie.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
