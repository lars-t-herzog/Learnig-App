package com.example.learnigapp

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Vocab(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,   // 0 = "database, assign a number"
    val word: String,
    val translation: String,

    // learning progress (for the algorithm)
    val repetitions: Int = 0,          // 0 = new, 1..6 = learning, 7+ = learned
    val intervalDays: Int = 0,         // current gap between reviews in days

    // when to revise next: days since 1970-01-01
    val dueDay: Long = System.currentTimeMillis() / 86_400_000L
)
