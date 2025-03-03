package com.example.exo4.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "trainings")
data class Training(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val categoryId: String,
    val date: Long,
    val totalTime: Long,
    val runningTimes: List<Long>,
    val shootingTimes: List<Long>,
    val missedTargets: Int,
    val gpsPoints: List<GpsPoint>? = null
)

data class GpsPoint(
    val latitude: Double,
    val longitude: Double,
    val timestamp: Long
)

data class ShootingSession(
    val timeSpent: Long,
    val targetsHit: Int
) 