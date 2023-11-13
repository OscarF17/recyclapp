package com.example.proyectofinal

import androidx.room.Database
import androidx.room.RoomDatabase

// Clase abstracta para acceder a la base de datos mediante el DAO
@Database(entities = [Historial::class], version = 1)
abstract class AppDatabase: RoomDatabase() {
    abstract fun historialDao(): HistorialDao
}