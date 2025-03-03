package com.example.exo4

import android.os.Bundle
import android.widget.TextView
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
        val lapTimes = intent.getLongArrayExtra("LAP_TIMES")?.toList() ?: emptyList()
        val shootingTimes = intent.getLongArrayExtra("SHOOTING_TIMES")?.toList() ?: emptyList()

        // Sauvegarder l'entraînement
        lifecycleScope.launch {
            val training = Training(
                categoryId = intent.getStringExtra("CATEGORY_ID") ?: "",
                date = System.currentTimeMillis(),
                totalTime = totalTime,
                runningTimes = lapTimes,
                shootingTimes = shootingTimes,
                missedTargets = intent.getIntExtra("MISSED_TARGETS", 0)
            )
            trainingRepository.insertTraining(training)
        }

        displayResults(totalTime, lapTimes, shootingTimes)
    }

    private fun displayResults(totalTime: Long, lapTimes: List<Long>, shootingTimes: List<Long>) {
        findViewById<TextView>(R.id.total_time_text).text = formatTime(totalTime)
        
        val avgLapTime = lapTimes.average()
        findViewById<TextView>(R.id.running_stats_text).text = 
            "Temps moyen par tour : ${formatTime(avgLapTime.toLong())}"

        val shootingStats = "Tir - Min: ${formatTime(shootingTimes.minOrNull() ?: 0)}\n" +
                          "Max: ${formatTime(shootingTimes.maxOrNull() ?: 0)}\n" +
                          "Moyenne: ${formatTime(shootingTimes.average().toLong())}"
        findViewById<TextView>(R.id.shooting_stats_text).text = shootingStats
    }

    private fun formatTime(timeInMillis: Long): String {
        val minutes = TimeUnit.MILLISECONDS.toMinutes(timeInMillis)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(timeInMillis) % 60
        val millis = timeInMillis % 1000 / 10
        return String.format("%02d:%02d:%02d", minutes, seconds, millis)
    }
}