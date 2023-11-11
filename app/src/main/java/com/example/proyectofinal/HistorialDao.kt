package com.example.proyectofinal

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface HistorialDao {
    @Insert
    fun insert(historial: Historial): Long

    @Query("SELECT * FROM Historial")
    fun getAllHistorial(): List<Historial>

    @Update
    fun updateHistorial(historial: Historial): Int

    @Delete
    fun deleteHistorial(historial: Historial): Int

    @Query("DELETE FROM Historial")
    fun deleteEverything()
}