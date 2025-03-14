package com.example.exo4

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.exo4.database.AppDatabase
import com.example.exo4.model.Training
import com.example.exo4.repository.TrainingRepository
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.LegendEntry
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

class StatistiqueActivity : AppCompatActivity() {
    private lateinit var trainingRepository: TrainingRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_statistique)

        val database = AppDatabase.getDatabase(this)
        trainingRepository = TrainingRepository(database.trainingDao())

        // Afficher la date du jour
        displayCurrentDate()
        
        // Charger les statistiques
        loadStatistics()
    }

    private fun displayCurrentDate() {
        val dateText = findViewById<TextView>(R.id.current_date)
        val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale.FRANCE)
        val currentDate = dateFormat.format(Date())
        dateText.text = "Date : $currentDate"
    }

    private fun loadStatistics() {
        lifecycleScope.launch {
            val trainings = trainingRepository.getAllTrainings()
            
            Log.d("StatistiqueActivity", "Nombre d'entraînements chargés : ${trainings.size}")
            
            val totalTrainingsText = findViewById<TextView>(R.id.total_trainings)
            val averageTimeText = findViewById<TextView>(R.id.average_time)
            val bestTimeText = findViewById<TextView>(R.id.best_time)
            val averageMissedText = findViewById<TextView>(R.id.average_missed)

            totalTrainingsText.text = "Nombre total d'entraînements : ${trainings.size}"

            if (trainings.isNotEmpty()) {
                // Calcul des statistiques
                val (runningTime, shootingTime, totalTime) = calculateTotalTimes(trainings)
                val avgTime = trainings.map { it.totalTime }.average()
                val bestTime = trainings.minOf { it.totalTime }
                val avgMissed = trainings.map { it.missedTargets }.average()
                
                // Affichage des statistiques
                averageTimeText.text = "Temps moyen : ${formatTime(avgTime.toLong())}"
                bestTimeText.text = "Meilleur temps : ${formatTime(bestTime)}"
                averageMissedText.text = "Moyenne de cibles manquées : %.1f".format(avgMissed)
                
                // Vérifier les données pour le débogage
                Log.d("StatistiqueActivity", "Temps total de course : ${formatTime(runningTime)}")
                Log.d("StatistiqueActivity", "Temps total de tir : ${formatTime(shootingTime)}")
                
                // Configurer et remplir les graphiques
                setupPieChart(trainings)
                setupBarChart(trainings)
            } else {
                // Afficher un message si aucun entraînement n'est disponible
                val pieChart = findViewById<PieChart>(R.id.chart_time_distribution)
                val barChart = findViewById<BarChart>(R.id.chart_missed_shots)
                
                pieChart.setNoDataText("Aucune donnée disponible")
                barChart.setNoDataText("Aucune donnée disponible")
                
                pieChart.invalidate()
                barChart.invalidate()
            }
        }
    }

    private fun calculateTotalTimes(trainings: List<Training>): Triple<Long, Long, Long> {
        var totalRunningTime = 0L
        var totalShootingTime = 0L
        
        trainings.forEach { training ->
            // Additionner les temps de course positifs uniquement
            training.runningTimes.forEach { time ->
                if (time > 0) totalRunningTime += time
            }
            
            // Additionner les temps de tir positifs uniquement
            training.shootingTimes.forEach { time ->
                if (time > 0) totalShootingTime += time
            }
        }
        
        val totalTime = totalRunningTime + totalShootingTime
        
        Log.d("StatistiqueActivity", "Temps total de course calculé: ${formatTime(totalRunningTime)}")
        Log.d("StatistiqueActivity", "Temps total de tir calculé: ${formatTime(totalShootingTime)}")
        Log.d("StatistiqueActivity", "Temps total calculé: ${formatTime(totalTime)}")
        
        return Triple(totalRunningTime, totalShootingTime, totalTime)
    }

    private fun setupPieChart(trainings: List<Training>) {
        val pieChart = findViewById<PieChart>(R.id.chart_time_distribution)
        
        // Calculer le temps total de course et de tir
        val (totalRunningTime, totalShootingTime, totalTime) = calculateTotalTimes(trainings)
        
        // Vérifier si les données sont valides
        if (totalRunningTime <= 0 && totalShootingTime <= 0) {
            pieChart.setNoDataText("Aucune donnée de temps disponible")
            pieChart.setNoDataTextColor(Color.BLACK)
            pieChart.setNoDataTextTypeface(Typeface.DEFAULT_BOLD)
            pieChart.invalidate()
            return
        }
        
        val total = totalTime
        
        // Créer les entrées pour le graphique
        val entries = ArrayList<PieEntry>()
        
        // Formater les temps pour l'affichage
        val runningTimeFormatted = formatTime(totalRunningTime)
        val shootingTimeFormatted = formatTime(totalShootingTime)
        
        // Calculer les pourcentages pour l'affichage uniquement
        val runningPercentage = if (total > 0) (totalRunningTime.toFloat() / total.toFloat()) * 100f else 0f
        val shootingPercentage = if (total > 0) (totalShootingTime.toFloat() / total.toFloat()) * 100f else 0f
        
        if (totalRunningTime > 0) {
            entries.add(PieEntry(totalRunningTime.toFloat(), ""))
        }
        
        if (totalShootingTime > 0) {
            entries.add(PieEntry(totalShootingTime.toFloat(), ""))
        }
        
        // Configurer le dataset
        val dataSet = PieDataSet(entries, "")
        dataSet.colors = listOf(Color.rgb(76, 175, 80), Color.rgb(244, 67, 54)) // Vert et Rouge
        dataSet.valueTextColor = Color.WHITE
        dataSet.valueTextSize = 14f
        dataSet.sliceSpace = 5f
        dataSet.selectionShift = 10f
        
        // Configurer le graphique
        val pieData = PieData(dataSet)
        pieData.setValueFormatter(object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return formatTime(value.toLong())
            }
        })
        
        pieChart.data = pieData
        pieChart.description.isEnabled = false
        pieChart.centerText = "Répartition\ndu temps"
        pieChart.setCenterTextSize(18f)
        pieChart.setCenterTextTypeface(Typeface.DEFAULT_BOLD)
        pieChart.setUsePercentValues(false)
        
        // Désactiver la légende intégrée
        pieChart.legend.isEnabled = false
        
        // Ajouter les détails dans le TextView
        val detailsText = findViewById<TextView>(R.id.chart_details)
        if (detailsText != null) {
            val sb = StringBuilder()
            sb.appendLine("Détails de la répartition du temps :")
            sb.appendLine("• Course : ${runningTimeFormatted} (${runningPercentage.toInt()}%)")
            sb.appendLine("• Tir : ${shootingTimeFormatted} (${shootingPercentage.toInt()}%)")
            sb.appendLine("• Temps total : ${formatTime(total)}")
            detailsText.text = sb.toString()
        }
        
        // Configuration supplémentaire
        pieChart.setDrawEntryLabels(false) // Désactiver les étiquettes sur les tranches
        pieChart.isDrawHoleEnabled = true
        pieChart.holeRadius = 35f
        pieChart.transparentCircleRadius = 40f
        pieChart.setExtraOffsets(20f, 20f, 20f, 20f)
        
        // Animation
        pieChart.animateY(1500)
        
        // Rafraîchir le graphique
        pieChart.invalidate()
    }

    private fun setupBarChart(trainings: List<Training>) {
        val barChart = findViewById<BarChart>(R.id.chart_missed_shots)
        
        // Créer les entrées pour le graphique
        val entries = ArrayList<BarEntry>()
        
        // Compter le nombre de tirs manqués par catégorie (0, 1, 2, 3, 4, 5)
        val missedCounts = IntArray(6)
        trainings.forEach { training ->
            if (training.missedTargets in 0..5) {
                missedCounts[training.missedTargets]++
            }
        }
        
        // Vérifier si des données sont disponibles
        val hasData = missedCounts.any { it > 0 }
        if (!hasData) {
            barChart.setNoDataText("Aucune donnée de tirs manqués disponible")
            barChart.invalidate()
            return
        }
        
        // Ajouter les données au graphique
        for (i in 0..5) {
            entries.add(BarEntry(i.toFloat(), missedCounts[i].toFloat()))
        }
        
        // Configurer le dataset
        val dataSet = BarDataSet(entries, "Nombre d'entraînements")
        dataSet.colors = listOf(
            Color.rgb(76, 175, 80),  // 0 tirs manqués - Vert
            Color.rgb(139, 195, 74), // 1 tir manqué - Vert clair
            Color.rgb(255, 235, 59), // 2 tirs manqués - Jaune
            Color.rgb(255, 152, 0),  // 3 tirs manqués - Orange
            Color.rgb(255, 87, 34),  // 4 tirs manqués - Orange foncé
            Color.rgb(244, 67, 54)   // 5 tirs manqués - Rouge
        )
        dataSet.valueTextColor = Color.BLACK
        dataSet.valueTextSize = 14f
        
        // Configurer le graphique
        val barData = BarData(dataSet)
        barChart.data = barData
        barChart.description.isEnabled = false
        barChart.xAxis.labelCount = 6
        barChart.xAxis.granularity = 1f
        barChart.xAxis.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return "${value.toInt()} tirs"
            }
        }
        barChart.axisLeft.axisMinimum = 0f
        barChart.axisRight.isEnabled = false
        barChart.legend.isEnabled = true
        barChart.legend.verticalAlignment = Legend.LegendVerticalAlignment.TOP
        barChart.legend.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
        
        // Animation
        barChart.animateY(1000)
        
        // Rafraîchir le graphique
        barChart.invalidate()
    }

    private fun formatTime(timeInMillis: Long): String {
        val minutes = TimeUnit.MILLISECONDS.toMinutes(timeInMillis)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(timeInMillis) % 60
        val millis = timeInMillis % 1000
        return String.format("%02d:%02d.%03d", minutes, seconds, millis)
    }
}
