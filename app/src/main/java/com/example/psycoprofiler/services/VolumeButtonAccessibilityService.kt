package com.example.psycoprofiler.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.accessibilityservice.AccessibilityService
import android.view.KeyEvent
import android.widget.Toast
import android.os.CountDownTimer
import android.content.Intent
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import androidx.core.app.NotificationCompat
import com.example.psycoprofiler.R
import com.example.psycoprofiler.SolucionesActivity

class VolumeButtonAccessibilityService : AccessibilityService() {

    private var volumeButtonPressed = false
    private var countDownTimer: CountDownTimer? = null
    private val holdDuration = 5000L // 5 segundos
    private val notificationChannelId = "volume_service_channel"
    private var timeRemaining: Long = holdDuration

    override fun onServiceConnected() {
        // Crear el canal de notificación
        createNotificationChannel()
        // Iniciar el servicio en primer plano
        startForegroundService()
        Toast.makeText(this, "Servicio de accesibilidad activo", Toast.LENGTH_SHORT).show()
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // Este servicio no maneja eventos de accesibilidad
        Log.d("VolumeButtonService", "Evento de accesibilidad detectado: $event")
    }

    override fun onKeyEvent(event: KeyEvent): Boolean {
        // Detecta el botón de bajar volumen específicamente
        if (event.keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            when (event.action) {
                KeyEvent.ACTION_DOWN -> {
                    Log.d("VolumeButton", "Botón de bajar volumen presionado")
                    if (!volumeButtonPressed) {
                        volumeButtonPressed = true
                        startHoldTimer() // Inicia el temporizador
                        showPressAlert() // Muestra alerta de que el botón está presionado
                    }
                }
                KeyEvent.ACTION_UP -> {
                    Log.d("VolumeButton", "Botón de bajar volumen liberado")
                    volumeButtonPressed = false
                    resetTimer() // Resetea el temporizador
                }
            }
            return true // Indica que este evento fue manejado
        }
        return super.onKeyEvent(event) // Otros eventos se manejan normalmente
    }


    private fun showPressAlert() {
        Toast.makeText(
            this,
            "Botón presionado, Manténgalo para activar la App.",
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun onInterrupt() {
        // Método obligatorio para servicios de accesibilidad
    }

    private fun startHoldTimer() {
        countDownTimer = object : CountDownTimer(holdDuration, 1000) { // Conteo cada segundo
            override fun onTick(millisUntilFinished: Long) {
                timeRemaining = millisUntilFinished / 1000 // Convertir a segundos
                Log.d("VolumeButton", "Segundos restantes: $timeRemaining")
            }

            override fun onFinish() {
                if (volumeButtonPressed) {
                    triggerAlert() // Acciona si el botón fue mantenido por 5 segundos
                }
                resetTimer() // Reinicia el estado del temporizador
            }
        }.start()
    }

    // Iniciar una actividad o ejecutar una acción
    private fun triggerAlert() {
        val intent = Intent(this, SolucionesActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
    }

    private fun resetTimer() {
        countDownTimer?.cancel()
        volumeButtonPressed = false
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                notificationChannelId,
                "Volume Service",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Servicio para detectar botones de volumen"
            }
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    private fun startForegroundService() {
        val notification: Notification = NotificationCompat.Builder(this, notificationChannelId)
            .setContentTitle("Servicio de botones de volumen")
            .setContentText("El servicio está ejecutándose")
            .setSmallIcon(R.drawable.ic_notification) // Reemplaza con un ícono válido
            .build()

        startForeground(1, notification)
    }
}
