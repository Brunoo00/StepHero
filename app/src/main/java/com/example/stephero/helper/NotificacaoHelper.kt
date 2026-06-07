package com.example.stephero.helper

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.stephero.R

class NotificacaoHelper(private val context: Context) {

    private val canalID = "stephero_canal"
    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    init {
        criarCanal()
    }

    private fun criarCanal() {
        val canal = NotificationChannel(
            canalID,
            "StepHero Notificações",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        canal.description = "Notificações do StepHero"
        notificationManager.createNotificationChannel(canal)
    }

    fun notificarMissaoConcluida(passos: Int) {
        val notificacao = NotificationCompat.Builder(context, canalID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Missão concluída! 🏆")
            .setContentText("Parabéns! Você andou $passos passos e ganhou 50 XP!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(1, notificacao)
    }

    fun notificarMarcoRegistrado() {
        val notificacao = NotificationCompat.Builder(context, canalID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Marco registrado! 📸")
            .setContentText("Foto do marco salva com sucesso!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(2, notificacao)
    }
}