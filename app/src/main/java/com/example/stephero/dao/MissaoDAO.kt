package com.example.stephero.dao

import com.example.stephero.model.Missao
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MissaoDAO {
    private val db = Firebase.firestore

    fun salvarMissao(
        missao: Missao,
        onSucesso: () -> Unit,
        onErro: (Exception) -> Unit
    ) {
        db.collection("missoes")
            .document(missao.id)
            .set(missao)
            .addOnSuccessListener { onSucesso() }
            .addOnFailureListener { erro -> onErro(erro) }
    }

    fun buscarMissoesDoUsuario(
        email: String,
        onSucesso: (List<Missao>) -> Unit,
        onErro: (Exception) -> Unit
    ) {
        db.collection("missoes")
            .whereEqualTo("usuarioEmail", email)
            .get()
            .addOnSuccessListener { documentos ->
                val missoes = documentos.mapNotNull { it.toObject(Missao::class.java) }
                    .sortedByDescending { it.data }
                onSucesso(missoes)
            }
            .addOnFailureListener { erro -> onErro(erro) }
    }

    fun gerarIdMissao(): String {
        return db.collection("missoes").document().id
    }
}