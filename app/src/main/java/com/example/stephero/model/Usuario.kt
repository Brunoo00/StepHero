package com.example.stephero.model

data class Usuario(
    val email: String = "",
    val username: String = "",
    val nivel: Int = 1,
    val xp: Int = 0,
    val fotoPerfil: String = ""
)