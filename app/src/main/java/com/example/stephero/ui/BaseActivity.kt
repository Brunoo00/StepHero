package com.example.stephero.ui

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.example.stephero.helper.ThemeHelper
import com.google.android.material.button.MaterialButton

open class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        try {
            aplicarTema()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun aplicarTema() {
        val corVariante = ThemeHelper.getCorVariante(this)
        supportActionBar?.setBackgroundDrawable(ColorDrawable(corVariante))
    }
}