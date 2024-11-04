package com.example.psycoprofiler.services

import android.content.Intent
import android.accessibilityservice.AccessibilityService
import android.os.CountDownTimer
import android.view.KeyEvent
import android.view.accessibility.AccessibilityEvent
import android.widget.Toast
import com.example.psycoprofiler.SolucionesActivity

class VolumeButtonAccessibilityService : AccessibilityService() {

    private var volumeButtonPressed = false
    private var countDownTimer: CountDownTimer? = null
    private val holdDuration = 5000L // 5 segundos

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // No es necesario manejar eventos de accesibilidad en este caso
    }

    override fun onKeyEvent(event: KeyEvent): Boolean {
        // Solo manejamos el botón de bajar volumen
        if (event.keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            when (event.action) {
                KeyEvent.ACTION_DOWN -> {
                    if (!volumeButtonPressed) {
                        volumeButtonPressed = true
                        startHoldTimer()
                    }
                }
                KeyEvent.ACTION_UP -> {
                    volumeButtonPressed = false
                    resetTimer()
                }
            }
            return true
        }
        return super.onKeyEvent(event)
    }

    override fun onServiceConnected() {
        Toast.makeText(this, "Servicio de accesibilidad activo", Toast.LENGTH_SHORT).show()
    }

    override fun onInterrupt() {
        // Este método se invoca si el servicio es interrumpido
    }

    private fun startHoldTimer() {
        countDownTimer = object : CountDownTimer(holdDuration, 100) {
            override fun onTick(millisUntilFinished: Long) {
                // Puedes mostrar un progreso visual aquí si es necesario
            }

            override fun onFinish() {
                if (volumeButtonPressed) {
                    // Aquí lanzamos la actividad SolucionesActivity
                    val intent = Intent(this@VolumeButtonAccessibilityService, SolucionesActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK) // Asegúrate de que se abra correctamente
                    startActivity(intent)
                }
                resetTimer()
            }
        }.start()
    }

    private fun resetTimer() {
        countDownTimer?.cancel()
        volumeButtonPressed = false
    }
}