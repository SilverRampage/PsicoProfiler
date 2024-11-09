package com.example.psycoprofiler

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import kotlinx.coroutines.*
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONObject
import java.io.IOException

class LoginActivity : AppCompatActivity() {

    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var btnRegister: Button

    private val client by lazy { OkHttpClient() }
    private val url = "http://18.118.234.197:5000/login"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
        btnRegister = findViewById(R.id.btnRegister)

        val sharedPref = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        val userId = sharedPref.getInt("Id", -1) // Cambiar a getInt y proporcionar un valor predeterminado

        if (userId != -1) { // Verifica si el usuario ya está registrado
            val intent = Intent(this, ConfiguracionBotonActivity::class.java)
            startActivity(intent)
            finish()
        }

        btnLogin.setOnClickListener {
            val email = etEmail.text.toString()
            val password = etPassword.text.toString()

            if (isValidEmail(email) && password.isNotEmpty()) {
                iniciarSesion(email, password)
            } else {
                Toast.makeText(this, getString(R.string.error_fields), Toast.LENGTH_SHORT).show()
            }
        }

        btnRegister.setOnClickListener {
            val intent = Intent(this, RegistroActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun iniciarSesion(email: String, password: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val loginData = JSONObject().apply {
                put("email", email)
                put("contrasena", password)
            }

            val body = RequestBody.create("application/json; charset=utf-8".toMediaTypeOrNull(), loginData.toString())
            val request = Request.Builder()
                .url(url)
                .post(body)
                .build()

            try {
                val response = client.newCall(request).execute()
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val responseData = response.body?.string() // Obtiene el cuerpo de la respuesta como String
                        val jsonResponse = JSONObject(responseData) // Convierte el String a JSON

                        // Extrae el objeto 'usuario' y luego el campo 'Id' dentro de 'usuario'
                        val usuarioObject = jsonResponse.getJSONObject("usuario")
                        val userId = usuarioObject.getInt("Id")

                        // Guardar el estado de sesión en SharedPreferences
                        val sharedPref = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
                        val editor = sharedPref.edit()
                        editor.putBoolean("isLoggedIn", true)
                        editor.putInt("Id", userId) // Guardar el ID del usuario como Int
                        editor.apply()

                        Toast.makeText(this@LoginActivity, getString(R.string.login_success), Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@LoginActivity, ConfiguracionBotonActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this@LoginActivity, getString(R.string.error_login, response.message), Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: IOException) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@LoginActivity, getString(R.string.network_error, e.message), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
