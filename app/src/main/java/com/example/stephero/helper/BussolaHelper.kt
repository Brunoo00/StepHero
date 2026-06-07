package com.example.stephero.helper

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager

class BussolaHelper(
    private val context: Context,
    private val callback: Callback
) : SensorEventListener {

    interface Callback {
        fun onDirecaoAtualizada(graus: Float, direcao: String)
        fun onErro(mensagem: String)
    }

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val gravity = FloatArray(3)
    private val geomagnetic = FloatArray(3)

    fun iniciar() {
        val acelerometro = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        val magnetometro = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)

        if (acelerometro != null && magnetometro != null) {
            sensorManager.registerListener(this, acelerometro, SensorManager.SENSOR_DELAY_UI)
            sensorManager.registerListener(this, magnetometro, SensorManager.SENSOR_DELAY_UI)
        } else {
            callback.onErro("Magnetômetro não disponível neste dispositivo")
        }
    }

    fun parar() {
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent) {
        when (event.sensor.type) {
            Sensor.TYPE_ACCELEROMETER -> gravity.apply {
                this[0] = event.values[0]
                this[1] = event.values[1]
                this[2] = event.values[2]
            }
            Sensor.TYPE_MAGNETIC_FIELD -> geomagnetic.apply {
                this[0] = event.values[0]
                this[1] = event.values[1]
                this[2] = event.values[2]
            }
        }

        val R = FloatArray(9)
        val I = FloatArray(9)
        if (SensorManager.getRotationMatrix(R, I, gravity, geomagnetic)) {
            val orientation = FloatArray(3)
            SensorManager.getOrientation(R, orientation)
            val graus = Math.toDegrees(orientation[0].toDouble()).toFloat()
            val grausPositivos = (graus + 360) % 360
            val direcao = obterDirecao(grausPositivos)
            callback.onDirecaoAtualizada(grausPositivos, direcao)
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    private fun obterDirecao(graus: Float): String {
        return when {
            graus < 22.5 || graus >= 337.5  -> "N"
            graus < 67.5  -> "NE"
            graus < 112.5 -> "L"
            graus < 157.5 -> "SE"
            graus < 202.5 -> "S"
            graus < 247.5 -> "SO"
            graus < 292.5 -> "O"
            else           -> "NO"
        }
    }
}