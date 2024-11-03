package com.example.psycoprofiler

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import kotlinx.coroutines.*
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import org.json.JSONObject
import java.io.IOException

class LoginActivity : AppCompatActivity() {

    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var btnRegister: Button

    // Reutilizar la instancia de OkHttpClient
    private val client by lazy { OkHttpClient() }

    // Constante de la URL
    private val url = "http://10.0.2.2:5000/api/users/login"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
        btnRegister = findViewById(R.id.btnRegister)

        // Verificar si ya está logueado el usuario
        val sharedPref = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        val isLoggedIn = sharedPref.getBoolean("isLoggedIn", false)

        if (isLoggedIn) {
            // Si el usuario ya está logueado, redirigir a la siguiente actividad
            val intent = Intent(this, ConfiguracionBotonActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Boton de inicio de sesion
        btnLogin.setOnClickListener {
            val email = etEmail.text.toString()
            val password = etPassword.text.toString()

            if (isValidEmail(email) && password.isNotEmpty()) {
                iniciarSesion(email, password)
            } else {
                Toast.makeText(this, getString(R.string.error_fields), Toast.LENGTH_SHORT).show()
            }
        }

        // Boton para ir a la ventana de registro
        btnRegister.setOnClickListener {
            val intent = Intent(this, RegistroActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    // Valida si el correo es correcto
    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    // Funcion para inicio de sesion
    private fun iniciarSesion(email: String, password: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val loginData = JSONObject().apply {
                put("email", email)
                put("password", password)
            }

            val body = RequestBody.create("application/json; charset=utf-8".toMediaType(), loginData.toString())
            val request = Request.Builder()
                .url(url)
                .post(body)
                .build()

            try {
                val response = client.newCall(request).execute()
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {

                        // Guardar el estado de sesión en SharedPreferences
                        val sharedPref = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
                        val editor = sharedPref.edit()
                        // Guardar un flag de sesión o un token
                        editor.putBoolean("isLoggedIn", true)
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
