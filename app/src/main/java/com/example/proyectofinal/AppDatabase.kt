package com.example.proyectofinal

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Historial::class], version = 1)
abstract class AppDatabase: RoomDatabase() {
    abstract fun historialDao(): HistorialDao
}