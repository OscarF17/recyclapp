package com.example.proyectofinal

import androidx.room.Entity
import androidx.room.PrimaryKey

// Clase para definir tabla Historial en la base de datos
// Usada para guardar localmente los productos escaneados
@Entity(tableName = "Historial")
data class Historial(
    @PrimaryKey
    val id: String,
    val producto: String,
    val tipo: String,
    val tips: String,
    val img: String
)
