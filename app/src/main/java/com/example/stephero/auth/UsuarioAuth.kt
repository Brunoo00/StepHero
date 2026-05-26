package com.example.stephero.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class UsuarioAuth {
    private val auth = FirebaseAuth.getInstance()

    fun usuarioAtual(): FirebaseUser? = auth.currentUser

    fun emailAtual(): String = auth.currentUser?.email ?: ""

    fun estaLogado(): Boolean = auth.currentUser != null

    fun login(
        email: String,
        senha: String,
        onSucesso: () -> Unit,
        onErro: (Exception) -> Unit
    ) {
        auth.signInWithEmailAndPassword(email, senha)
            .addOnSuccessListener { onSucesso() }
            .addOnFailureListener { erro -> onErro(erro) }
    }

    fun cadastrar(
        email: String,
        senha: String,
        onSucesso: () -> Unit,
        onErro: (Exception) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, senha)
            .addOnSuccessListener { onSucesso() }
            .addOnFailureListener { erro -> onErro(erro) }
    }

    fun logout() {
        auth.signOut()
    }
}