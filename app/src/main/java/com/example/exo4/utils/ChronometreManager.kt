package com.example.exo4.utils

import android.os.Handler
import android.os.Looper
import android.widget.TextView
import java.util.concurrent.TimeUnit
import android.os.SystemClock
import java.util.Timer
import java.util.TimerTask

class ChronometreManager {
    private var startTime: Long = 0
    private var isRunning = false
    private val handler = Handler(Looper.getMainLooper())
    private val updateRunnable = object : Runnable {
        override fun run() {
            if (isRunning) {
                updateDisplay()
                handler.postDelayed(this, 10) // Mise à jour toutes les 10ms
            }
        }
    }

    private var displayView: TextView? = null
    private val lapTimes = mutableListOf<Long>()
    private val shootingTimes = mutableListOf<Long>()
    private var shootingStartTime: Long = 0
    private var timer: Timer? = null
    private var totalTime: Long = 0
    private var currentElapsedTime: Long = 0

    fun setDisplayView(view: TextView) {
        displayView = view
        updateDisplay()
    }

    fun start() {
        if (!isRunning) {
            startTime = SystemClock.elapsedRealtime()
            isRunning = true
            startTimer()
        }
    }

    fun stop() {
        if (isRunning) {
            totalTime += SystemClock.elapsedRealtime() - startTime
            isRunning = false
            timer?.cancel()
            timer = null
            updateDisplay()
        }
    }

    fun startShootingSession() {
        shootingStartTime = System.currentTimeMillis()
    }

    fun endShootingSession(targetsHit: Int): Boolean {
        val shootingTime = System.currentTimeMillis() - shootingStartTime
        shootingTimes.add(shootingTime)
        return shootingTime <= TimeUnit.SECONDS.toMillis(50)
    }

    fun recordLapTime() {
        if (isRunning) {
            val lapTime = SystemClock.elapsedRealtime() - startTime
            lapTimes.add(lapTime)
            totalTime += lapTime
            startTime = SystemClock.elapsedRealtime() // Réinitialiser pour le prochain tour
        }
    }

    fun getTotalTime(): Long {
        return if (isRunning) {
            totalTime + (SystemClock.elapsedRealtime() - startTime)
        } else {
            totalTime
        }
    }

    fun getLapTimes(): List<Long> {
        return lapTimes.toList()
    }

    private fun startTimer() {
        timer = Timer()
        timer?.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                currentElapsedTime = if (isRunning) {
                    totalTime + (SystemClock.elapsedRealtime() - startTime)
                } else {
                    totalTime
                }
                updateDisplay()
            }
        }, 0, 10)
    }

    fun setInitialTime(time: Long) {
        totalTime = time
        updateDisplay()
    }

    private fun updateDisplay() {
        val timeToDisplay = currentElapsedTime
        val minutes = (timeToDisplay / 60000).toInt()
        val seconds = ((timeToDisplay % 60000) / 1000).toInt()
        val milliseconds = (timeToDisplay % 1000).toInt()

        displayView?.post {
            displayView?.text = String.format("%02d:%02d.%03d", minutes, seconds, milliseconds)
        }
    }

    fun getResults(): TrainingResults {
        return TrainingResults(
            totalTime = getTotalTime(),
            lapTimes = getLapTimes(),
            shootingTimes = shootingTimes.toList()
        )
    }
}

data class TrainingResults(
    val totalTime: Long,
    val lapTimes: List<Long>,
    val shootingTimes: List<Long>
) 