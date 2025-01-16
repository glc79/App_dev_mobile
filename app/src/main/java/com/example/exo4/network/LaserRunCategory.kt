package com.example.exo4.model

data class LaserRunCategory(
    val id: String,
    val name: String,
    val initialDistance: Int,
    val lapDistance: Int,
    val lapCount: Int,
    val shootDistance: Int
)
