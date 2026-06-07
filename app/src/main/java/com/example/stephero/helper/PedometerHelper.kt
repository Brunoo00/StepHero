package com.example.stephero.helper

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager

class PedometerHelper(
    private val context: Context,
    private val callback: Callback
) : SensorEventListener {

    interface Callback {
        fun onPassoContado(totalPassos: Int)
        fun onErro(mensagem: String)
    }

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private var passos = 0
    private var ultimaAceleracao = 0f
    private val limiarPasso = 12f

    fun iniciar() {
        val acelerometro = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        if (acelerometro != null) {
            sensorManager.registerListener(this, acelerometro, SensorManager.SENSOR_DELAY_NORMAL)
        } else {
            callback.onErro("Acelerômetro não disponível neste dispositivo")
        }
    }

    fun parar() {
        sensorManager.unregisterListener(this)
    }

    fun resetar() {
        passos = 0
        ultimaAceleracao = 0f
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]
            val aceleracao = Math.sqrt((x * x + y * y + z * z).toDouble()).toFloat()
            val delta = Math.abs(aceleracao - ultimaAceleracao)
            if (delta > limiarPasso) {
                passos++
                callback.onPassoContado(passos)
            }
            ultimaAceleracao = aceleracao
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}