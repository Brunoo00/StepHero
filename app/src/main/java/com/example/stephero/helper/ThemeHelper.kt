package com.example.stephero.helper

import android.content.Context
import androidx.core.content.ContextCompat
import com.example.stephero.R
import java.util.Calendar

object ThemeHelper {

    enum class PeriodoDia {
        DIA, TARDE, NOITE
    }

    fun getPeriodoDia(): PeriodoDia {
        val hora = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        return when {
            hora in 6..11  -> PeriodoDia.DIA
            hora in 12..17 -> PeriodoDia.TARDE
            else           -> PeriodoDia.NOITE
        }
    }

    fun getCorPrimaria(context: Context): Int {
        return when (getPeriodoDia()) {
            PeriodoDia.DIA   -> ContextCompat.getColor(context, R.color.dia_primaria)
            PeriodoDia.TARDE -> ContextCompat.getColor(context, R.color.tarde_primaria)
            PeriodoDia.NOITE -> ContextCompat.getColor(context, R.color.noite_primaria)
        }
    }

    fun getCorVariante(context: Context): Int {
        return when (getPeriodoDia()) {
            PeriodoDia.DIA   -> ContextCompat.getColor(context, R.color.dia_variante)
            PeriodoDia.TARDE -> ContextCompat.getColor(context, R.color.tarde_variante)
            PeriodoDia.NOITE -> ContextCompat.getColor(context, R.color.noite_variante)
        }
    }

    fun getSaudacao(): String {
        return when (getPeriodoDia()) {
            PeriodoDia.DIA   -> "Bom dia"
            PeriodoDia.TARDE -> "Boa tarde"
            PeriodoDia.NOITE -> "Boa noite"
        }
    }

    fun getEmoji(): String {
        return when (getPeriodoDia()) {
            PeriodoDia.DIA   -> "☀️"
            PeriodoDia.TARDE -> "🌅"
            PeriodoDia.NOITE -> "🌙"
        }
    }
}