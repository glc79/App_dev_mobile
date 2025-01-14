package com.example.exo4

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupSpinner()
        setupNavigation()
    }

    private fun setupSpinner() {
        val spinner: Spinner = findViewById(R.id.spinner)
        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.categories_array,
            android.R.layout.simple_spinner_item
        ).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        spinner.adapter = adapter

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
        val buttonStat: Button = findViewById(R.id.button2)

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
}
