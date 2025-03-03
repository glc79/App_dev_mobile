package com.example.exo4.utils

import android.os.Handler
import android.os.Looper
import android.widget.TextView
import java.util.concurrent.TimeUnit

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

    private var chronoTextView: TextView? = null
    private val lapTimes = mutableListOf<Long>()
    private val shootingTimes = mutableListOf<Long>()
    private var shootingStartTime: Long = 0

    fun setDisplayView(textView: TextView) {
        chronoTextView = textView
    }

    fun start() {
        if (!isRunning) {
            startTime = System.currentTimeMillis()
            isRunning = true
            handler.post(updateRunnable)
        }
    }

    fun stop() {
        isRunning = false
        handler.removeCallbacks(updateRunnable)
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
        lapTimes.add(System.currentTimeMillis() - startTime)
    }

    private fun updateDisplay() {
        val elapsedTime = System.currentTimeMillis() - startTime
        val minutes = TimeUnit.MILLISECONDS.toMinutes(elapsedTime)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(elapsedTime) % 60
        val milliseconds = elapsedTime % 1000 / 10
        chronoTextView?.text = String.format("%02d:%02d:%02d", minutes, seconds, milliseconds)
    }

    fun getResults(): TrainingResults {
        return TrainingResults(
            totalTime = System.currentTimeMillis() - startTime,
            lapTimes = lapTimes.toList(),
            shootingTimes = shootingTimes.toList()
        )
    }
}

data class TrainingResults(
    val totalTime: Long,
    val lapTimes: List<Long>,
    val shootingTimes: List<Long>
) 