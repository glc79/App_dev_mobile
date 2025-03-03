package com.example.exo4

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.os.SystemClock
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ZoneDeTirActivity : AppCompatActivity() {
    private lateinit var chronometerText: TextView
    private lateinit var remainingShotsText: TextView
    private lateinit var buttonNext: Button
    private lateinit var buttonReset: Button
    private var timer: CountDownTimer? = null
    private var timeSpentInMillis: Long = 0
    private var currentMissedShots: Int = 0
    private var totalTime: Long = 0
    private var totalMissedShots: Int = 0
    private var totalShootingTime: Long = 0
    private var startTime: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_zone_de_tir)

        totalTime = intent.getLongExtra("TOTAL_TIME", 0)
        totalMissedShots = intent.getIntExtra("TOTAL_MISSED_SHOTS", 0)
        totalShootingTime = intent.getLongExtra("TOTAL_SHOOTING_TIME", 0)
        startTime = SystemClock.elapsedRealtime()

        // Initialisation des vues
        chronometerText = findViewById(R.id.text_chronometer)
        remainingShotsText = findViewById(R.id.text_remaining_shots)
        buttonNext = findViewById(R.id.button_next)
        buttonReset = findViewById(R.id.button_reset)

        setupTimer()
        setupButtons()
    }

    private fun setupTimer() {
        timer = object : CountDownTimer(50000, 100) {
            override fun onTick(millisUntilFinished: Long) {
                timeSpentInMillis = 50000 - millisUntilFinished // Temps réellement passé dans la zone de tir
                val seconds = timeSpentInMillis / 1000
                val tenths = (timeSpentInMillis % 1000) / 100
                chronometerText.text = String.format("%02d:%01d", seconds, tenths)
            }

            override fun onFinish() {
                timeSpentInMillis = 50000 // Temps maximum de tir
                handleTimeUp()
            }
        }.start()
    }

    private fun setupButtons() {
        // Configuration des boutons numérotés
        for (i in 0..5) {
            val buttonId = resources.getIdentifier("button_$i", "id", packageName)
            findViewById<Button>(buttonId).setOnClickListener {
                handleMissedShots(i)
            }
        }

        buttonNext.setOnClickListener {
            finishShooting()
        }

        buttonReset.setOnClickListener {
            resetSelection()
        }
    }

    private fun handleMissedShots(missedShots: Int) {
        currentMissedShots = missedShots
        timer?.cancel()
        
        // Désactiver tous les boutons de tir
        for (i in 0..5) {
            val buttonId = resources.getIdentifier("button_$i", "id", packageName)
            findViewById<Button>(buttonId).isEnabled = false
        }
        
        // Activer les boutons suivant et reset
        buttonNext.isEnabled = true
        buttonReset.isEnabled = true
        
        // Mettre à jour le texte
        remainingShotsText.text = "Tirs manqués : $missedShots"
        
        // Sauvegarder le nombre de tirs manqués
        intent.putExtra("MISSED_SHOTS", missedShots)
    }

    private fun resetSelection() {
        // Réactiver tous les boutons de tir
        for (i in 0..5) {
            val buttonId = resources.getIdentifier("button_$i", "id", packageName)
            findViewById<Button>(buttonId).isEnabled = true
        }
        
        // Désactiver les boutons suivant et reset
        buttonNext.isEnabled = false
        buttonReset.isEnabled = false
        
        // Réinitialiser le texte
        remainingShotsText.text = "Sélectionnez le nombre de tirs manqués"
        
        // Redémarrer le timer si le temps n'est pas écoulé
        if (timeSpentInMillis < 50000) {
            setupTimer()
        }
    }

    private fun handleTimeUp() {
        // Désactiver tous les boutons de tir
        for (i in 0..5) {
            val buttonId = resources.getIdentifier("button_$i", "id", packageName)
            findViewById<Button>(buttonId).isEnabled = false
        }
        buttonNext.isEnabled = true
        remainingShotsText.text = "Temps écoulé !"
    }

    private fun finishShooting() {
        timer?.cancel()
        totalMissedShots += currentMissedShots
        
        val remainingLaps = intent.getIntExtra("REMAINING_LAPS", 1)
        
        if (remainingLaps > 1) {
            val intent = Intent(this, CourirActivity::class.java).apply {
                putExtra("REMAINING_LAPS", remainingLaps - 1)
                putExtra("LAP_COUNT", remainingLaps - 1)
                putExtra("MISSED_SHOTS", totalMissedShots)
                putExtra("TOTAL_SHOOTING_TIME", totalShootingTime + timeSpentInMillis)
                putExtra("SAVED_TOTAL_TIME", totalTime + timeSpentInMillis)
                putExtra("INITIAL_DISTANCE", getIntent().getIntExtra("INITIAL_DISTANCE", 0))
                putExtra("SHOOT_DISTANCE", getIntent().getIntExtra("SHOOT_DISTANCE", 0))
                putExtra("CATEGORY_ID", getIntent().getStringExtra("CATEGORY_ID"))
                putExtra("IS_LAST_LAP_COMPLETE", false)
            }
            startActivity(intent)
        } else {
            // C'est le dernier tour, on retourne à CourirActivity avec le flag
            val intent = Intent(this, CourirActivity::class.java).apply {
                putExtra("REMAINING_LAPS", 1)
                putExtra("LAP_COUNT", 1)
                putExtra("MISSED_SHOTS", totalMissedShots)
                putExtra("TOTAL_SHOOTING_TIME", totalShootingTime + timeSpentInMillis)
                putExtra("SAVED_TOTAL_TIME", totalTime + timeSpentInMillis)
                putExtra("INITIAL_DISTANCE", getIntent().getIntExtra("INITIAL_DISTANCE", 0))
                putExtra("SHOOT_DISTANCE", getIntent().getIntExtra("SHOOT_DISTANCE", 0))
                putExtra("CATEGORY_ID", getIntent().getStringExtra("CATEGORY_ID"))
                putExtra("IS_LAST_LAP_COMPLETE", true)
            }
            startActivity(intent)
        }
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        timer?.cancel()
    }
}
