package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "expenses")
data class Expense(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val amount: Double,
    val categoryId: Long,
    val categoryName: String,
    val categoryIcon: String,
    val categoryColor: Long,
    val isOnline: Boolean, // true = Online, false = Offline
    val timestamp: Long = System.currentTimeMillis(),
    val note: String = ""
)
