package com.example.stephero.model

import com.google.firebase.Timestamp

data class Missao(
    val id: String = "",
    val usuarioEmail: String = "",
    val data: Timestamp = Timestamp.now(),
    val passos: Int = 0,
    val metaPassos: Int = 6000,
    val concluida: Boolean = false,
    val fotoMarco: String = "",
    val fotoConclusao: String = ""
)