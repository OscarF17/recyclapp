package com.example.proyectofinal

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Url

// Interfaz para llamar al API de nuestro servidor web
interface JsonPlaceholderApi {
    @GET
    suspend fun getPosts(@Url url: String): Response<Post>
}