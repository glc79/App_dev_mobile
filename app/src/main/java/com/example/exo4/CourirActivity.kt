package com.example.exo4

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.exo4.utils.ChronometreManager

class CourirActivity : AppCompatActivity() {
    private lateinit var chronoManager: ChronometreManager
    private lateinit var buttonNextPhase: Button
    private lateinit var buttonFinish: Button
    private var remainingLaps: Int = 0
    private var totalMissedShots = 0
    private var totalShootingTime: Long = 0
    private var savedTotalTime: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_courir)

        // Récupération des données
        remainingLaps = intent.getIntExtra("LAP_COUNT", 0)
        val initialDistance = intent.getIntExtra("INITIAL_DISTANCE", 0)
        val shootDistance = intent.getIntExtra("SHOOT_DISTANCE", 0)
        
        // Récupérer les données du tir précédent
        totalMissedShots = intent.getIntExtra("MISSED_SHOTS", 0)
        totalShootingTime = intent.getLongExtra("TOTAL_SHOOTING_TIME", 0)
        savedTotalTime = intent.getLongExtra("SAVED_TOTAL_TIME", 0)
        val isLastLapComplete = intent.getBooleanExtra("IS_LAST_LAP_COMPLETE", false)

        // Initialisation des vues
        buttonNextPhase = findViewById(R.id.button_next_phase)
        buttonFinish = findViewById(R.id.button_finish)

        // Initialisation du chronomètre
        chronoManager = ChronometreManager()
        chronoManager.setDisplayView(findViewById(R.id.text_chronometer))
        if (savedTotalTime > 0) {
            chronoManager.setInitialTime(savedTotalTime)
        }

        setupUI(remainingLaps, initialDistance, shootDistance)
        setupButtons(isLastLapComplete)
        startTraining()
    }

    private fun setupUI(laps: Int, initialDistance: Int, shootDistance: Int) {
        findViewById<TextView>(R.id.lap_count_text_view).text = "Tours restants : $laps"
        findViewById<TextView>(R.id.distance_text_view).text = "Distance initiale : $initialDistance m"
        findViewById<TextView>(R.id.shoot_distance_text_view).text = "Distance de tir : $shootDistance m"

        updateUIForPhase()
    }

    private fun setupButtons(isLastLapComplete: Boolean) {
        buttonNextPhase.setOnClickListener {
            handleNextPhase()
        }

        buttonFinish.setOnClickListener {
            handleFinish()
        }

        // Afficher le bouton Terminer uniquement après le dernier tir
        buttonFinish.visibility = if (remainingLaps == 1 && isLastLapComplete) View.VISIBLE else View.GONE
        buttonNextPhase.visibility = if (remainingLaps == 1 && isLastLapComplete) View.GONE else View.VISIBLE
    }

    private fun handleNextPhase() {
        val currentTotalTime = chronoManager.getTotalTime()
        chronoManager.stop() // Arrêter le chronomètre avant de passer à la zone de tir
        savedTotalTime = currentTotalTime // Sauvegarder le temps actuel
        
        val intent = Intent(this, ZoneDeTirActivity::class.java).apply {
            putExtra("REMAINING_LAPS", remainingLaps)
            putExtra("TOTAL_TIME", currentTotalTime)
            putExtra("CATEGORY_ID", getIntent().getStringExtra("CATEGORY_ID"))
            putExtra("TOTAL_MISSED_SHOTS", totalMissedShots)
            putExtra("TOTAL_SHOOTING_TIME", totalShootingTime)
            putExtra("INITIAL_DISTANCE", getIntent().getIntExtra("INITIAL_DISTANCE", 0))
            putExtra("SHOOT_DISTANCE", getIntent().getIntExtra("SHOOT_DISTANCE", 0))
            putExtra("SAVED_TOTAL_TIME", currentTotalTime)
        }
        startActivity(intent)
        finish()
    }

    private fun handleFinish() {
        chronoManager.stop()
        val finalTime = chronoManager.getTotalTime()
        savedTotalTime = finalTime // Sauvegarder le temps final
        
        val intent = Intent(this, ResultActivity::class.java).apply {
            putExtra("TOTAL_TIME", finalTime)
            putExtra("MISSED_SHOTS", totalMissedShots)
            putExtra("TOTAL_SHOOTING_TIME", totalShootingTime)
            putExtra("CATEGORY_ID", getIntent().getStringExtra("CATEGORY_ID"))
        }
        startActivity(intent)
        finish()
    }

    private fun startTraining() {
        if (savedTotalTime > 0) {
            chronoManager.setInitialTime(savedTotalTime)
        }
        chronoManager.start()
    }

    private fun updateUIForPhase() {
        val nextButton: Button = findViewById(R.id.button_next_phase)
        nextButton.text = "Commencer le tir"
    }

    override fun onDestroy() {
        super.onDestroy()
        chronoManager.stop()
    }
}