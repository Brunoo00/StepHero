package com.example.stephero.model

import com.google.firebase.Timestamp

data class Conquista(
    val id: String = "",
    val usuarioEmail: String = "",
    val titulo: String = "",
    val descricao: String = "",
    val data: Timestamp = Timestamp.now()
)