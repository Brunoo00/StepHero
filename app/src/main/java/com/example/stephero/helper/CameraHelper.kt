package com.example.stephero.helper

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.provider.MediaStore
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity

class CameraHelper(
    activity: AppCompatActivity,
    private val callback: Callback
) {
    interface Callback {
        fun onFotoRecebida(bitmap: Bitmap)
    }

    private val launcher = activity.registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val bitmap: Bitmap? = try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    result.data?.extras?.getParcelable("data", Bitmap::class.java)
                } else {
                    @Suppress("DEPRECATION")
                    result.data?.extras?.get("data") as? Bitmap
                }
            } catch (e: Exception) {
                null
            }

            if (bitmap != null) {
                val copia = bitmap.copy(Bitmap.Config.ARGB_8888, true)
                callback.onFotoRecebida(copia)
            }
        }
    }

    fun tirarFoto() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        launcher.launch(intent)
    }
}