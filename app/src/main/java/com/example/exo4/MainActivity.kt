// MainActivity.kt
package com.example.exo4

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.exo4.model.LaserRunCategory
import com.example.exo4.network.RetrofitInstance
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// MainActivity.kt

class MainActivity : AppCompatActivity() {
    // Propriété pour gérer la demande de permission
    private lateinit var locationPermissionRequest: ActivityResultLauncher<Array<String>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialisation du launcher de permission
        locationPermissionRequest = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            handlePermissionsResult(permissions)
        }

        fetchLaserrunItems()
    }

    private fun fetchLaserrunItems() {
        RetrofitInstance.apiService.getLaserrunItems().enqueue(object : Callback<List<LaserRunCategory>> {
            override fun onResponse(call: Call<List<LaserRunCategory>>, response: Response<List<LaserRunCategory>>) {
                if (response.isSuccessful) {
                    val categories = response.body()
                    if (categories != null) {
                        updateSpinnerWithCategories(categories)
                    }
                } else {
                    Toast.makeText(applicationContext,
                        "Erreur: ${response.code()} - ${response.message()}",
                        Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<LaserRunCategory>>, t: Throwable) {
                Toast.makeText(applicationContext,
                    "Erreur réseau: ${t.message}",
                    Toast.LENGTH_SHORT).show()
            }
        })
        setupNavigation()
    }

    private fun updateSpinnerWithCategories(categories: List<LaserRunCategory>) {
        val spinner: Spinner = findViewById(R.id.spinner)
        val categoryNames = categories.map { it.name }

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            categoryNames
        ).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

        spinner.adapter = adapter
    }

    private fun setupNavigation() {
        val buttonGps: Button = findViewById(R.id.button_gps)
        val buttonStat: Button = findViewById(R.id.button_stats)

        buttonGps.setOnClickListener {
            checkAndRequestPermissionsForGps()
        }

        buttonStat.setOnClickListener {
            val intent = Intent(this, StatistiqueActivity::class.java)
            startActivity(intent)
        }
    }

    private fun checkAndRequestPermissionsForGps() {
        if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
            checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        ) {
            navigateToGpsActivity()
        } else {
            locationPermissionRequest.launch(
                arrayOf(
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    private fun handlePermissionsResult(permissions: Map<String, Boolean>) {
        if (permissions[android.Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            permissions[android.Manifest.permission.ACCESS_COARSE_LOCATION] == true
        ) {
            Toast.makeText(this, "Permission accordée", Toast.LENGTH_SHORT).show()
            navigateToGpsActivity()
        } else {
            Toast.makeText(
                this,
                "Permission refusée. Impossible d'accéder à la carte.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun navigateToGpsActivity() {
        val intent = Intent(this, GPSActivity::class.java)
        startActivity(intent)
    }
}
