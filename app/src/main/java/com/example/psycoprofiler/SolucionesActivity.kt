package com.example.psycoprofiler

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.TextView
import android.widget.Toast

class SolucionesActivity : AppCompatActivity() {

    private lateinit var tvSoluciones: TextView
    private lateinit var btnRecomponerme: Button
    private lateinit var btnAyuda: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_soluciones)

        tvSoluciones = findViewById(R.id.tvSoluciones)
        btnRecomponerme = findViewById(R.id.btnRecomponerme)
        btnAyuda = findViewById(R.id.btnAyuda)

        // Simular la obtención de soluciones de la IA
        tvSoluciones.text = "Solución 1: Respira profundamente...\nSolución 2: Concéntrate en un objeto cercano..."

        btnRecomponerme.setOnClickListener {
            Toast.makeText(this, "Genial! Estaré atenta para ayudarte", Toast.LENGTH_SHORT).show()
            finish()
        }

        btnAyuda.setOnClickListener {
            // Aquí enviarás el mensaje de alerta con los datos del usuario y ubicación
            enviarAlerta()
        }
    }

    private fun enviarAlerta() {
        // Simula el envío de una alerta
        Toast.makeText(this, "Alerta enviada con tus datos y ubicación", Toast.LENGTH_SHORT).show()
        // Aquí puedes cerrar la aplicación o redirigir al usuario a otra pantalla
        finish()
    }
}
