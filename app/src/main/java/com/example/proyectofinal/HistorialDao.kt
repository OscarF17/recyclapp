package com.example.proyectofinal

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
// DAO para definir los métodos disponibles en la base de datos
@Dao
interface HistorialDao {
    // Definir funciones CRUD básicas
    @Insert
    fun insert(historial: Historial): Long

    @Query("SELECT * FROM Historial")
    fun getAllHistorial(): List<Historial>

    @Update
    fun updateHistorial(historial: Historial): Int

    @Delete
    fun deleteHistorial(historial: Historial): Int

    // Agregar queries adicionales a la base de datos para realizar acciones específicas

    // Buscar historial de un item en específico
    @Query("SELECT * FROM Historial WHERE id LIKE :historialId")
    fun getHistorialById(historialId: String): List<Historial>

    // Borrar toda la base de datos (usado para realizar pruebas)
    @Query("DELETE FROM Historial")
    fun deleteEverything()


}