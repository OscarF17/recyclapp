package com.example.proyectofinal

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Historial")
data class Historial(
    @PrimaryKey
    val id: String,
    val producto: String,
    val tipo: String,
    val img: String
)
