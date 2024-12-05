package com.example.psycoprofiler

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

class RegistroActivity : AppCompatActivity() {

    private val client = OkHttpClient()

    // Definir constantes para URL y MediaType
    private val url = "http://18.118.234.197:5000/registro"
    private val JSON = "application/json; charset=utf-8".toMediaType()

    private lateinit var etNombre: EditText
    private lateinit var etApellidos: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var etFechaNacimiento: EditText
    private lateinit var etGenero: EditText
    private lateinit var etUbicacion: EditText
    private lateinit var etPadecimiento: EditText
    private lateinit var spinnerPadecimiento: Spinner
    private lateinit var btnRegistro: Button
    private lateinit var btnReturn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)

        // Inicializar vistas
        spinnerPadecimiento = findViewById(R.id.spinnerPadecimiento)

        // Opciones del Spinner
        val padecimientos = listOf("Ansiedad", "Estrés", "Depresión", "TDHA")

        // Configurar el Adapter del Spinner
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            padecimientos
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerPadecimiento.adapter = adapter

        // Usar apply para inicializar vistas
        apply {
            etNombre = findViewById(R.id.etNombre)
            etApellidos = findViewById(R.id.etApellidos)
            etEmail = findViewById(R.id.etEmail)
            etPassword = findViewById(R.id.etPassword)
            etFechaNacimiento = findViewById(R.id.etFechaNacimiento)
            etGenero = findViewById(R.id.etGenero)
            etUbicacion = findViewById(R.id.etUbicacion)
            btnRegistro = findViewById(R.id.btnRegistro)
            btnReturn = findViewById(R.id.btnReturn)
        }

        btnRegistro.setOnClickListener {
            if (validarCampos()) {
                // Crear un objeto User con los datos del formulario
                val padecimientoSeleccionado = spinnerPadecimiento.selectedItem.toString()
                println(padecimientoSeleccionado)
                val user = User(
                    etNombre.text.toString(),
                    etApellidos.text.toString(),
                    etEmail.text.toString(),
                    etPassword.text.toString(),
                    etFechaNacimiento.text.toString(),
                    etGenero.text.toString(),
                    etUbicacion.text.toString(),
                    padecimientoSeleccionado
                )
                println(user)
                registrarUsuario(user)
            } else {
                Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
            }
        }

        btnReturn.setOnClickListener {
            startActivity(Intent(this@RegistroActivity, LoginActivity::class.java))
            finish()
        }
    }

    // Función para validar los campos
    private fun validarCampos(): Boolean {
        return listOf(
            etNombre.text, etApellidos.text, etEmail.text, etPassword.text,
            etFechaNacimiento.text, etGenero.text, etUbicacion.text
        ).all { it.isNotEmpty() } && spinnerPadecimiento.selectedItemPosition != -1
    }

    private fun registrarUsuario(user: User) {
        // Crear el JSON utilizando los datos del objeto User
        val json = JSONObject().apply {
            put("nombre", user.nombre)
            put("apellido", user.apellido)
            put("email", user.email)
            put("password", user.password)
            put("fechaNacimiento", user.fechaNacimiento)
            put("genero", user.genero)
            put("ubicacion", user.ubicacion)
            put("padecimiento", user.padecimiento)
        }

        // Crear el body para la solicitud POST
        val body = json.toString().toRequestBody(JSON)

        // Crear la solicitud POST
        val request = Request.Builder()
            .url(url)
            .post(body)
            .build()

        // Ejecutar la solicitud
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@RegistroActivity, "Error al registrar usuario", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                runOnUiThread {
                    if (response.isSuccessful) {
                        Toast.makeText(this@RegistroActivity, "Registro exitoso", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@RegistroActivity, LoginActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(this@RegistroActivity, "Error: ${response.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }

}
