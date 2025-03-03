package com.example.exo4.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.exo4.R
import com.example.exo4.model.Training
import java.text.SimpleDateFormat
import java.util.*

class TrainingHistoryAdapter : RecyclerView.Adapter<TrainingHistoryAdapter.TrainingViewHolder>() {
    private var trainings: List<Training> = emptyList()

    class TrainingViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val dateText: TextView = view.findViewById(R.id.date_text)
        val categoryText: TextView = view.findViewById(R.id.category_text)
        val totalTimeText: TextView = view.findViewById(R.id.total_time_text)
        val performanceText: TextView = view.findViewById(R.id.performance_text)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrainingViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_training_history, parent, false)
        return TrainingViewHolder(view)
    }

    override fun onBindViewHolder(holder: TrainingViewHolder, position: Int) {
        val training = trainings[position]
        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        
        holder.dateText.text = "Date: ${dateFormat.format(Date(training.date))}"
        holder.categoryText.text = "Catégorie: ${training.categoryId}"
        holder.totalTimeText.text = "Temps total: ${formatTime(training.totalTime)}"
        holder.performanceText.text = "Cibles manquées: ${training.missedTargets}"
    }

    override fun getItemCount() = trainings.size

    fun updateTrainings(newTrainings: List<Training>) {
        trainings = newTrainings
        notifyDataSetChanged()
    }

    private fun formatTime(timeInMillis: Long): String {
        val minutes = timeInMillis / 60000
        val seconds = (timeInMillis % 60000) / 1000
        return String.format("%02d:%02d", minutes, seconds)
    }
} 