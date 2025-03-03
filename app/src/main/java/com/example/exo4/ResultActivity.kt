package com.example.exo4

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import java.util.concurrent.TimeUnit
import com.example.exo4.database.AppDatabase
import com.example.exo4.model.Training
import com.example.exo4.repository.TrainingRepository
import kotlinx.coroutines.launch

class ResultActivity : AppCompatActivity() {
    private lateinit var trainingRepository: TrainingRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        // Initialiser le repository
        val database = AppDatabase.getDatabase(this)
        trainingRepository = TrainingRepository(database.trainingDao())

        // Récupérer les données de l'intent
        val totalTime = intent.getLongExtra("TOTAL_TIME", 0)
        val missedShots = intent.getIntExtra("MISSED_SHOTS", 0)
        val shootingTime = intent.getLongExtra("TOTAL_SHOOTING_TIME", 0)
        val runningTime = totalTime - shootingTime

        // Afficher les résultats
        displayResults(totalTime, runningTime, shootingTime, missedShots)

        // Configuration du bouton retour à l'accueil
        findViewById<Button>(R.id.button_home).setOnClickListener {
            // Créer un nouvel intent pour MainActivity
            val intent = Intent(this, MainActivity::class.java)
            // Effacer la pile d'activités précédentes
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        // Sauvegarder l'entraînement
        lifecycleScope.launch {
            val training = Training(
                categoryId = intent.getStringExtra("CATEGORY_ID") ?: "default",
                date = System.currentTimeMillis(),
                totalTime = totalTime,
                runningTimes = listOf(runningTime),
                shootingTimes = listOf(shootingTime),
                missedTargets = missedShots
            )
            trainingRepository.insertTraining(training)
        }
    }

    private fun displayResults(totalTime: Long, runningTime: Long, shootingTime: Long, missedShots: Int) {
        val totalTimeText = findViewById<TextView>(R.id.total_time_text)
        val runningStatsText = findViewById<TextView>(R.id.running_stats_text)
        val shootingStatsText = findViewById<TextView>(R.id.shooting_stats_text)

        // Formater le temps total
        val totalMinutes = TimeUnit.MILLISECONDS.toMinutes(totalTime)
        val totalSeconds = TimeUnit.MILLISECONDS.toSeconds(totalTime) % 60
        val totalMillis = totalTime % 1000

        // Formater le temps de course
        val runMinutes = TimeUnit.MILLISECONDS.toMinutes(runningTime)
        val runSeconds = TimeUnit.MILLISECONDS.toSeconds(runningTime) % 60
        val runMillis = runningTime % 1000

        // Formater le temps de tir
        val shootMinutes = TimeUnit.MILLISECONDS.toMinutes(shootingTime)
        val shootSeconds = TimeUnit.MILLISECONDS.toSeconds(shootingTime) % 60
        val shootMillis = shootingTime % 1000

        totalTimeText.text = String.format(
            "Temps total : %02d:%02d.%03d",
            totalMinutes, totalSeconds, totalMillis
        )

        runningStatsText.text = String.format(
            "Temps de course : %02d:%02d.%03d",
            runMinutes, runSeconds, runMillis
        )

        shootingStatsText.text = String.format(
            """
            Statistiques de tir :
            - Temps de tir : %02d:%02d.%03d
            - Tirs manqués : %d
            """.trimIndent(),
            shootMinutes, shootSeconds, shootMillis, missedShots
        )
    }
}