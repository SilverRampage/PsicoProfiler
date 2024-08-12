package com.example.psycoprofiler

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.psycoprofiler.R
import com.example.psycoprofiler.SolucionesActivity

class ConfiguracionBotonActivity : AppCompatActivity() {

    private val OVERLAY_PERMISSION_REQ_CODE = 1234

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_configuracion_boton)

        val btnConfigurar: Button = findViewById(R.id.btnConfigurar)

        btnConfigurar.setOnClickListener {
            if (!Settings.canDrawOverlays(this)) {
                val intent = Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:$packageName")
                )
                startActivityForResult(intent, OVERLAY_PERMISSION_REQ_CODE)
            } else {
                Toast.makeText(this, "Permiso de superposición concedido", Toast.LENGTH_SHORT).show()
                configurarBotonFisico()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == OVERLAY_PERMISSION_REQ_CODE) {
            if (Settings.canDrawOverlays(this)) {
                Toast.makeText(this, "Permiso de superposición concedido", Toast.LENGTH_SHORT).show()
                configurarBotonFisico()
            } else {
                Toast.makeText(this, "Permiso de superposición denegado", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun configurarBotonFisico() {
        // Aquí puedes configurar la funcionalidad del botón físico
        Toast.makeText(this, "Botón configurado correctamente", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, SolucionesActivity::class.java)
        startActivity(intent)
        finish()
    }
}

