package com.example.psycoprofiler

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class RegistroActivity : AppCompatActivity() {

    private lateinit var etNombre: EditText
    private lateinit var etApellidos: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var etFechaNacimiento: EditText
    private lateinit var etGenero: EditText
    private lateinit var etUbicacion: EditText
    private lateinit var etPadecimiento: EditText
    private lateinit var btnRegistro: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)

        etNombre = findViewById(R.id.etNombre)
        etApellidos = findViewById(R.id.etApellidos)
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        etFechaNacimiento = findViewById(R.id.etFechaNacimiento)
        etGenero = findViewById(R.id.etGenero)
        etUbicacion = findViewById(R.id.etUbicacion)
        etPadecimiento = findViewById(R.id.etPadecimiento)
        btnRegistro = findViewById(R.id.btnRegistro)

        btnRegistro.setOnClickListener {
            val nombre = etNombre.text.toString()
            val apellidos = etApellidos.text.toString()
            val email = etEmail.text.toString()
            val password = etPassword.text.toString()
            val fechaNacimiento = etFechaNacimiento.text.toString()
            val genero = etGenero.text.toString()
            val ubicacion = etUbicacion.text.toString()
            val padecimiento = etPadecimiento.text.toString()

            if (nombre.isNotEmpty() && apellidos.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()
                && fechaNacimiento.isNotEmpty() && genero.isNotEmpty() && ubicacion.isNotEmpty() && padecimiento.isNotEmpty()) {
                // Aquí deberías implementar la lógica para registrar el usuario en la base de datos

                // Simulación de registro exitoso
                Toast.makeText(this, "Registro exitoso", Toast.LENGTH_SHORT).show()

                // Redirigir al usuario a la pantalla de Login después del registro
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
            }
        }


    }
}
