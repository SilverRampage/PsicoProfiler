package com.example.psycoprofiler

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import kotlinx.coroutines.*
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

class SolucionesActivity : AppCompatActivity() {

    private lateinit var tvSoluciones: TextView
    private lateinit var btnRecomponerme: Button
    private lateinit var btnAyuda: Button
    private lateinit var btnTop3Soluciones: Button

    private val client = OkHttpClient()
    private val apiUrl = "http://10.0.2.2:5000" // Cambia esto por la URL de tu API
    private val urlTop3Soluciones = apiUrl + "/gettop3soluciones"
    private val urlSolucionUnica = apiUrl + "/soluciones"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_soluciones)

        tvSoluciones = findViewById(R.id.tvSoluciones)
        btnRecomponerme = findViewById(R.id.btnRecomponerme)
        btnAyuda = findViewById(R.id.btnAyuda)
        btnTop3Soluciones = findViewById(R.id.btnTop3Soluciones)

        // Configurar el botón para obtener soluciones
        btnTop3Soluciones.setOnClickListener {
            obtenerSoluciones()
        }

        btnRecomponerme.setOnClickListener {
            Toast.makeText(this, "Genial! Estaré atenta para ayudarte", Toast.LENGTH_SHORT).show()
            finish()
        }

        btnAyuda.setOnClickListener {
            // Aquí enviarás el mensaje de alerta con los datos del usuario y ubicación
            enviarAlerta()
        }
    }

    private fun obtenerSoluciones() {
        // Obtener el ID del usuario de SharedPreferences
        val sharedPref = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        val userId = sharedPref.getInt("Id", -1) // Cambiado a getInt y valor predeterminado -1

        if (userId != -1) { // Verifica si el Id fue encontrado
            CoroutineScope(Dispatchers.IO).launch {
                // Crear el objeto JSON con el ID del usuario
                val requestData = JSONObject().apply {
                    put("Id", userId)
                }

                val body = RequestBody.create("application/json; charset=utf-8".toMediaType(), requestData.toString())
                val request = Request.Builder()
                    .url(urlTop3Soluciones) // Usar la URL sin agregar el ID en la URL
                    .post(body) // Enviar el cuerpo JSON
                    .build()

                try {
                    val response = client.newCall(request).execute()
                    withContext(Dispatchers.Main) {
                        if (response.isSuccessful) {
                            // Obtener la respuesta y actualizar el TextView
                            val responseData = response.body?.string()
                            if (!responseData.isNullOrEmpty()) {
                                // Asegurarse de que el JSON contenga el array "soluciones"
                                try {
                                    val jsonResponse = JSONObject(responseData)
                                    if (jsonResponse.has("soluciones")) {
                                        val soluciones = jsonResponse.getJSONArray("soluciones")
                                        mostrarSoluciones(soluciones)
                                    } else {
                                        Toast.makeText(this@SolucionesActivity, "No se encontraron soluciones.", Toast.LENGTH_SHORT).show()
                                    }
                                } catch (e: Exception) {
                                    Toast.makeText(this@SolucionesActivity, "Error al procesar la respuesta.", Toast.LENGTH_SHORT).show()
                                }
                            }
                        } else {
                            Toast.makeText(this@SolucionesActivity, "Error: ${response.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: IOException) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@SolucionesActivity, "Error de red: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        } else {
            Toast.makeText(this, "No se encontró el ID del usuario", Toast.LENGTH_SHORT).show()
        }
    }


    private fun mostrarSoluciones(soluciones: JSONArray) {
        val solucionesList = StringBuilder()
        for (i in 0 until soluciones.length()) {
            solucionesList.append("Solución ${i + 1}: ${soluciones.getString(i)}\n")
        }
        tvSoluciones.text = solucionesList.toString()
    }

    private fun enviarAlerta() {
        // Simula el envío de una alerta
        Toast.makeText(this, "Alerta enviada con tus datos y ubicación", Toast.LENGTH_SHORT).show()
        finish()
    }
}
