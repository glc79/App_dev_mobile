package com.example.exo4.repository

import com.example.exo4.database.TrainingDao
import com.example.exo4.model.Training
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TrainingRepository(private val trainingDao: TrainingDao) {
    
    suspend fun insertTraining(training: Training) = withContext(Dispatchers.IO) {
        trainingDao.insertTraining(training)
    }

    suspend fun getAllTrainings(): List<Training> = withContext(Dispatchers.IO) {
        trainingDao.getAllTrainings()
    }

    suspend fun getTrainingsByCategory(categoryId: String): List<Training> = withContext(Dispatchers.IO) {
        trainingDao.getTrainingsByCategory(categoryId)
    }
} 