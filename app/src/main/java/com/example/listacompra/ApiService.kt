package com.example.listacompra

import retrofit2.Response
import retrofit2.http.*

data class LoginRequest(val email: String, val senha: String)
data class LoginResponse(val token: String, val usuario: Usuario)
data class Usuario(val id: Int, val nome: String, val email: String)
data class CadastroRequest(val nome: String, val email: String, val senha: String)

interface ApiService {

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("auth/register")
    suspend fun register(@Body request: CadastroRequest): Response<Unit>

    // --- Shopping List Items ---
    @GET("shopping-list")
    suspend fun getItems(@Header("Authorization") token: String): Response<List<ItemLista>>

    @POST("shopping-list")
    suspend fun addItem(
        @Header("Authorization") token: String,
        @Body item: ItemLista
    ): Response<ItemLista>

    @PUT("shopping-list/{id}")
    suspend fun updateItem(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Body item: Map<String, Any>
    ): Response<Unit>

    @PATCH("shopping-list/{id}/toggle")
    suspend fun toggleItem(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<ItemLista>

    @DELETE("shopping-list/{id}")
    suspend fun deleteItem(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<Unit>

    // --- Categories Management ---
    @GET("categories")
    suspend fun getCategories(
        @Header("Authorization") token: String,
        @Query("group") group: String 
    ): Response<List<String>>

    @POST("categories")
    suspend fun addCategory(
        @Header("Authorization") token: String,
        @Body body: Map<String, String>
    ): Response<Unit>

    // --- Groups Management ---
    @GET("groups")
    suspend fun getGroups(@Header("Authorization") token: String): Response<List<String>>

    @POST("groups")
    suspend fun addGroup(
        @Header("Authorization") token: String,
        @Body body: Map<String, String>
    ): Response<Unit>

    // --- Products Management ---
    @GET("products")
    suspend fun getProducts(
        @Header("Authorization") token: String,
        @Query("categoria") category: String,
        @Query("group") group: String
    ): Response<List<String>>

    @POST("products")
    suspend fun addProduct(
        @Header("Authorization") token: String,
        @Body body: Map<String, String>
    ): Response<Unit>
}