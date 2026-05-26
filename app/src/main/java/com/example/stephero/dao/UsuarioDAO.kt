package com.example.stephero.dao

import com.example.stephero.model.Usuario
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class UsuarioDAO {
    private val db = Firebase.firestore

    fun salvarUsuario(
        usuario: Usuario,
        onSucesso: () -> Unit,
        onErro: (Exception) -> Unit
    ) {
        db.collection("usuarios")
            .document(usuario.email)
            .set(usuario)
            .addOnSuccessListener { onSucesso() }
            .addOnFailureListener { erro -> onErro(erro) }
    }

    fun buscarUsuario(
        email: String,
        onSucesso: (Usuario) -> Unit,
        onErro: (Exception) -> Unit
    ) {
        db.collection("usuarios")
            .document(email)
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val usuario = document.toObject(Usuario::class.java)
                    if (usuario != null) onSucesso(usuario)
                }
            }
            .addOnFailureListener { erro -> onErro(erro) }
    }

    fun atualizarXP(
        email: String,
        xpGanho: Int,
        onSucesso: () -> Unit,
        onErro: (Exception) -> Unit
    ) {
        buscarUsuario(
            email = email,
            onSucesso = { usuario ->
                val novoXP = usuario.xp + xpGanho
                val novoNivel = calcularNivel(novoXP)
                db.collection("usuarios")
                    .document(email)
                    .update(mapOf("xp" to novoXP, "nivel" to novoNivel))
                    .addOnSuccessListener { onSucesso() }
                    .addOnFailureListener { erro -> onErro(erro) }
            },
            onErro = onErro
        )
    }

    private fun calcularNivel(xp: Int): Int {
        return when {
            xp < 100  -> 1
            xp < 250  -> 2
            xp < 500  -> 3
            xp < 800  -> 4
            xp < 1200 -> 5
            xp < 1700 -> 6
            xp < 2300 -> 7
            xp < 3000 -> 8
            xp < 4000 -> 9
            else      -> 10
        }
    }
}
