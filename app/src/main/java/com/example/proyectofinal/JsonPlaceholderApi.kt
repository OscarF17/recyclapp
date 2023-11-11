package com.example.proyectofinal

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Url

interface JsonPlaceholderApi {
    @GET
    suspend fun getPosts(@Url url: String): Response<Post>
}