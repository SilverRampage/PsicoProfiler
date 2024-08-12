package com.example.psycoprofiler.network

import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.Call

data class SolucionesRequest(val userId: Int, val padecimiento: String)
data class SolucionesResponse(val soluciones: List<String>)

interface ApiService {

    @POST("soluciones")
    fun obtenerSoluciones(@Body request: SolucionesRequest): Call<SolucionesResponse>

    // Puedes definir otros métodos para diferentes rutas API aquí
}
