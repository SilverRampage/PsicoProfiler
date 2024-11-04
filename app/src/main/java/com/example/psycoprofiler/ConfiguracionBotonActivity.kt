package com.example.psycoprofiler

import android.app.Notification
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
import android.Manifest
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.psycoprofiler.R
import com.example.psycoprofiler.SolucionesActivity
import com.example.psycoprofiler.services.VolumeButtonAccessibilityService

class ConfiguracionBotonActivity : AppCompatActivity() {

    private val OVERLAY_PERMISSION_REQ_CODE = 1234
    private val CHANNEL_ID = "OverlayNotificationChannel"

    private val NOTIFICATION_PERMISSION_CODE = 5678

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_configuracion_boton)

        // Verificar y solicitar el permiso en Android 13 o superior
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    NOTIFICATION_PERMISSION_CODE
                )
            }
        }

        val btnConfigurar: Button = findViewById(R.id.btnConfigurar)
        btnConfigurar.setOnClickListener {
            if (!Settings.canDrawOverlays(this)) {
                val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))
                startActivityForResult(intent, OVERLAY_PERMISSION_REQ_CODE)
                startActivityForResult(intent, OVERLAY_PERMISSION_REQ_CODE)
            } else {
                showOverlayNotification()
                configurarBotonFisico()
            }
        }
    }

    // Maneja la respuesta de solicitud de permisos
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == NOTIFICATION_PERMISSION_CODE) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                Toast.makeText(this, "Permiso de notificación concedido", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Permiso de notificación denegado", Toast.LENGTH_SHORT).show()
            }
        }
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == OVERLAY_PERMISSION_REQ_CODE) {
            if (Settings.canDrawOverlays(this)) {
                Toast.makeText(this, "Permiso de superposición concedido", Toast.LENGTH_SHORT).show()
                showOverlayNotification()
                configurarBotonFisico()
            } else {
                Toast.makeText(this, "Permiso de superposición denegado", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun configurarBotonFisico() {
        Toast.makeText(this, "Botón configurado correctamente", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, SolucionesActivity::class.java)
        startActivity(intent)
        finish()
    }



    private fun lanzarPruebaBoton() {
        val intent = Intent(this, VolumeButtonAccessibilityService::class.java)
        startActivity(intent)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Overlay Service"
            val descriptionText = "Notificación de superposición activa"
            val importance = NotificationManager.IMPORTANCE_LOW
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun showOverlayNotification() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {

            val builder = NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification) // Aquí usas el nuevo ícono creado
                .setContentTitle("Psicoprofiler Activo")
                .setContentText("El permiso de superposición está activo.")
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setOngoing(true)

            with(NotificationManagerCompat.from(this)) {
                notify(OVERLAY_PERMISSION_REQ_CODE, builder.build())
            }
        }
    }


}

