package com.example.exo4

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.exo4.model.LaserRunCategory
import com.example.exo4.network.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


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
        setupNavigation()
    }


    private fun setupNavigation() {
        val buttonGps: Button = findViewById(R.id.button_gps)
        val buttonStat: Button = findViewById(R.id.button_stats)
        val buttonCategory: Button = findViewById(R.id.button_category)
        val buttonHistory: Button = findViewById(R.id.button_historique)

        buttonGps.setOnClickListener {
            checkAndRequestPermissionsForGps()
        }

        buttonStat.setOnClickListener {
            val intent = Intent(this, StatistiqueActivity::class.java)
            startActivity(intent)
        }

        buttonCategory.setOnClickListener{
            val intent = Intent(this, CategoryActivity::class.java)
            startActivity(intent)
        }

        buttonHistory.setOnClickListener{
            val intent = Intent(this, HistoryActivity::class.java)
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
