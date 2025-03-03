package com.example.exo4

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.exo4.database.AppDatabase
import com.example.exo4.repository.TrainingRepository
import kotlinx.coroutines.launch

class StatistiqueActivity : AppCompatActivity() {
    private lateinit var trainingRepository: TrainingRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_statistique)

        val database = AppDatabase.getDatabase(this)
        trainingRepository = TrainingRepository(database.trainingDao())

        loadStatistics()
    }

    private fun loadStatistics() {
        lifecycleScope.launch {
            val trainings = trainingRepository.getAllTrainings()

            val totalTrainingsText = findViewById<TextView>(R.id.total_trainings)
            val averageTimeText = findViewById<TextView>(R.id.average_time)
            val bestTimeText = findViewById<TextView>(R.id.best_time)
            val averageMissedText = findViewById<TextView>(R.id.average_missed)

            totalTrainingsText.text = "Nombre total d'entraînements : ${trainings.size}"

            if (trainings.isNotEmpty()) {
                val avgTime = trainings.map { it.totalTime }.average()
                val bestTime = trainings.minOf { it.totalTime }
                val avgMissed = trainings.map { it.missedTargets }.average()

                averageTimeText.text = "Temps moyen : ${formatTime(avgTime.toLong())}"
                bestTimeText.text = "Meilleur temps : ${formatTime(bestTime)}"
                averageMissedText.text = "Moyenne de cibles manquées : %.1f".format(avgMissed)
            }
        }
    }

    private fun formatTime(timeInMillis: Long): String {
        val minutes = timeInMillis / 60000
        val seconds = (timeInMillis % 60000) / 1000
        return String.format("%02d:%02d", minutes, seconds)
    }
}
