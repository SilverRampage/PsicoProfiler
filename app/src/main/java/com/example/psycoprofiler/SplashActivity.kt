package com.example.psycoprofiler

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Muestra la pantalla de inicio durante 2 segundos y luego redirige al Login/Registro
        val splashScreenTime: Long = 2000
        android.os.Handler().postDelayed({
            val intent = Intent(this@SplashActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }, splashScreenTime)
    }
}
