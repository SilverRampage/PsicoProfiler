package com.example.psycoprofiler

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.psycoprofiler.adapters.SolucionesAdapter
import kotlinx.coroutines.*
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

class SolucionesActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var btnRecomponerme: Button
    private lateinit var btnAyuda: Button
    private lateinit var btnTop3Soluciones: Button


    private val client = OkHttpClient()
    private val apiUrl = "http://18.118.234.197:5000"
    private val urlTop3Soluciones = apiUrl + "/gettop3soluciones"
    private val urlSoluciones = apiUrl + "/soluciones"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_soluciones)

        recyclerView = findViewById(R.id.tvSoluciones)  // Asegúrate de que este ID sea correcto
        btnRecomponerme = findViewById(R.id.btnRecomponerme)
        btnAyuda = findViewById(R.id.btnAyuda)
        btnTop3Soluciones = findViewById(R.id.btnTop3Soluciones)

        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager

        btnTop3Soluciones.setOnClickListener {
            obtenerSoluciones()
        }

        btnRecomponerme.setOnClickListener {
            Toast.makeText(this, "Genial! Estaré atenta para ayudarte", Toast.LENGTH_SHORT).show()
            finish()
        }

        btnAyuda.setOnClickListener {
            enviarAlerta()
        }
    }

    private fun obtenerSoluciones() {
        val sharedPref = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        val userId = sharedPref.getInt("Id", -1)

        if (userId != -1) {
            CoroutineScope(Dispatchers.IO).launch {
                val requestData = JSONObject().apply {
                    put("Id", userId)
                }

                val body = RequestBody.create("application/json; charset=utf-8".toMediaType(), requestData.toString())
                val request = Request.Builder()
                    .url(urlSoluciones)
                    .post(body)
                    .build()

                try {
                    val response = client.newCall(request).execute()
                    withContext(Dispatchers.Main) {
                        if (response.isSuccessful) {
                            val responseData = response.body?.string()
                            if (!responseData.isNullOrEmpty()) {
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
        val solucionesList = mutableListOf<String>()
        for (i in 0 until soluciones.length()) {
            solucionesList.add("Solución ${i + 1}: ${soluciones.getString(i)}")
        }

        // Configura el Adapter del RecyclerView
        val adapter = SolucionesAdapter(solucionesList)
        recyclerView.adapter = adapter
    }

    private fun enviarAlerta() {
        Toast.makeText(this, "Alerta enviada con tus datos y ubicación", Toast.LENGTH_SHORT).show()
        finish()
    }
}

