package com.example.stephero.dao

import com.example.stephero.model.Conquista
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ConquistaDAO {
    private val db = Firebase.firestore

    fun salvarConquista(
        conquista: Conquista,
        onSucesso: () -> Unit,
        onErro: (Exception) -> Unit
    ) {
        db.collection("conquistas")
            .document(conquista.id)
            .set(conquista)
            .addOnSuccessListener { onSucesso() }
            .addOnFailureListener { erro -> onErro(erro) }
    }

    fun buscarConquistasDoUsuario(
        email: String,
        onSucesso: (List<Conquista>) -> Unit,
        onErro: (Exception) -> Unit
    ) {
        db.collection("conquistas")
            .whereEqualTo("usuarioEmail", email)
            .get()
            .addOnSuccessListener { documentos ->
                val conquistas = documentos.mapNotNull { it.toObject(Conquista::class.java) }
                onSucesso(conquistas)
            }
            .addOnFailureListener { erro -> onErro(erro) }
    }

    fun gerarIdConquista(): String {
        return db.collection("conquistas").document().id
    }
}
