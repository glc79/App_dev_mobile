package com.example.exo4.database

import androidx.room.*
import com.example.exo4.model.Training

@Dao
interface TrainingDao {
    @Query("SELECT * FROM trainings ORDER BY date DESC")
    suspend fun getAllTrainings(): List<Training>

    @Insert
    suspend fun insertTraining(training: Training)

    @Query("SELECT * FROM trainings WHERE categoryId = :categoryId")
    suspend fun getTrainingsByCategory(categoryId: String): List<Training>
} 