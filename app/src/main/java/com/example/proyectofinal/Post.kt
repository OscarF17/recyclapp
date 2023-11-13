package com.example.proyectofinal

// Estructura del JSON recibido por el API
// Mismos atributos que Historial pero sin ser definida como tabla de BD
// Se usó una clase diferente para evitar conflictos
data class Post (
    val id: String,
    val img: String,
    val name: String,
    val tips: String,
    val type: String
)