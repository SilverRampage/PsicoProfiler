package com.example.psycoprofiler

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.psycoprofiler.services.VolumeButtonAccessibilityService

class ConfiguracionBotonActivity : AppCompatActivity() {

    private val OVERLAY_PERMISSION_REQ_CODE = 1234
    private val CHANNEL_ID = "OverlayNotificationChannel"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_configuracion_boton)

        val btnConfigurar: Button = findViewById(R.id.btnConfigurar)
        val btnTestFisico: Button = findViewById(R.id.testBtnFisico)
        val accessibilityButton: Button = findViewById(R.id.accessibility_button)


        // Verifica si el permiso de superposición está activo
        if (Settings.canDrawOverlays(this)) {
            btnConfigurar.visibility = Button.GONE
            btnTestFisico.visibility = Button.VISIBLE
        } else {
            btnConfigurar.visibility = Button.VISIBLE
            btnTestFisico.visibility = Button.GONE
        }

        // Configura el permiso de superposición
        btnConfigurar.setOnClickListener {
            if (!Settings.canDrawOverlays(this)) {
                val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))
                startActivityForResult(intent, OVERLAY_PERMISSION_REQ_CODE)
            } else {
                showOverlayNotification()
                toggleButtonsVisibility(btnConfigurar, btnTestFisico)
            }
        }

        // Configura el botón físico
        btnTestFisico.setOnClickListener {
            configurarBotonFisico()
        }

        accessibilityButton.setOnClickListener {
            openAccessibilitySettings()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == OVERLAY_PERMISSION_REQ_CODE && Settings.canDrawOverlays(this)) {
            Toast.makeText(this, "Permiso de superposición concedido", Toast.LENGTH_SHORT).show()
            showOverlayNotification()
            val btnConfigurar: Button = findViewById(R.id.btnConfigurar)
            val btnTestFisico: Button = findViewById(R.id.testBtnFisico)
            toggleButtonsVisibility(btnConfigurar, btnTestFisico)
        }
    }

    private fun toggleButtonsVisibility(configurar: Button, testFisico: Button) {
        configurar.visibility = Button.GONE
        testFisico.visibility = Button.VISIBLE
    }

    private fun configurarBotonFisico() {
        // Inicia el servicio para monitorear el botón físico
        val intent = Intent(this, VolumeButtonAccessibilityService::class.java)
        ContextCompat.startForegroundService(this, intent)
    }

    private fun abrirSolucionesManual(){
        val solucionesIntent = Intent(this, SolucionesActivity::class.java)
        startActivity(solucionesIntent)
    }

    private fun openAccessibilitySettings() {
        try {
            val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
            // Manejo de errores, por ejemplo, si la configuración no puede abrirse
        }
    }

    private fun showOverlayNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelName = "Overlay Service"
            val descriptionText = "Notificación de superposición activa"
            val importance = NotificationManager.IMPORTANCE_LOW
            val channel = NotificationChannel(CHANNEL_ID, channelName, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Psicoprofiler Activo")
            .setContentText("El permiso de superposición está activo.")
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .build()


    }
}
