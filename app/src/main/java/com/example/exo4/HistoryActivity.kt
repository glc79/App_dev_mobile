package com.example.exo4

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.exo4.adapter.TrainingHistoryAdapter
import com.example.exo4.database.AppDatabase
import com.example.exo4.repository.TrainingRepository
import kotlinx.coroutines.launch

class HistoryActivity : AppCompatActivity() {
    private lateinit var trainingRepository: TrainingRepository
    private lateinit var adapter: TrainingHistoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        // Initialiser le RecyclerView
        val recyclerView = findViewById<RecyclerView>(R.id.history_recycler_view)
        adapter = TrainingHistoryAdapter()
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Initialiser le repository
        val database = AppDatabase.getDatabase(this)
        trainingRepository = TrainingRepository(database.trainingDao())

        loadTrainings()
    }

    private fun loadTrainings() {
        lifecycleScope.launch {
            val trainings = trainingRepository.getAllTrainings()
            adapter.updateTrainings(trainings)
        }
    }
}