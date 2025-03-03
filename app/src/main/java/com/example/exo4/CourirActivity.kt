package com.example.exo4

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.exo4.utils.ChronometreManager

class CourirActivity : AppCompatActivity() {
    private lateinit var chronoManager: ChronometreManager
    private var remainingLaps: Int = 0
    private var isInShootingPhase = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_courir)

        // Récupération des données
        remainingLaps = intent.getIntExtra("LAP_COUNT", 0)
        val initialDistance = intent.getIntExtra("INITIAL_DISTANCE", 0)
        val shootDistance = intent.getIntExtra("SHOOT_DISTANCE", 0)

        // Initialisation du chronomètre
        chronoManager = ChronometreManager()
        chronoManager.setDisplayView(findViewById(R.id.text_chronometer))

        setupUI(remainingLaps, initialDistance, shootDistance)
        startTraining()
    }

    private fun setupUI(laps: Int, initialDistance: Int, shootDistance: Int) {
        findViewById<TextView>(R.id.lap_count_text_view).text = "Tours restants : $laps"
        findViewById<TextView>(R.id.distance_text_view).text = "Distance initiale : $initialDistance m"
        findViewById<TextView>(R.id.shoot_distance_text_view).text = "Distance de tir : $shootDistance m"

        val nextButton: Button = findViewById(R.id.button_next_phase)
        nextButton.setOnClickListener {
            handleNextPhase()
        }
    }

    private fun startTraining() {
        chronoManager.start()
    }

    private fun handleNextPhase() {
        if (isInShootingPhase) {
            // Fin de la phase de tir
            val shootingSuccess = chronoManager.endShootingSession(5) // À adapter selon les cibles touchées
            if (!shootingSuccess) {
                // Alerter l'utilisateur du dépassement de temps
            }
            
            remainingLaps--
            if (remainingLaps <= 0) {
                finishTraining()
            }
        } else {
            // Début de la phase de tir
            chronoManager.recordLapTime()
            chronoManager.startShootingSession()
        }
        isInShootingPhase = !isInShootingPhase
        updateUIForPhase()
    }

    private fun updateUIForPhase() {
        val nextButton: Button = findViewById(R.id.button_next_phase)
        nextButton.text = if (isInShootingPhase) "Terminer le tir" else "Commencer le tir"
    }

    private fun finishTraining() {
        val results = chronoManager.getResults()
        val intent = Intent(this, ResultActivity::class.java).apply {
            putExtra("TOTAL_TIME", results.totalTime)
            putExtra("LAP_TIMES", results.lapTimes.toLongArray())
            putExtra("SHOOTING_TIMES", results.shootingTimes.toLongArray())
        }
        startActivity(intent)
        finish()
    }
}