package com.example.stephero.ui

import android.os.Bundle
import com.example.stephero.auth.UsuarioAuth
import com.example.stephero.dao.MissaoDAO
import com.example.stephero.dao.UsuarioDAO
import com.example.stephero.databinding.ActivityPerfilBinding

class PerfilActivity : BaseActivity() {
    private lateinit var binding: ActivityPerfilBinding
    private val auth = UsuarioAuth()
    private val usuarioDAO = UsuarioDAO()
    private val missaoDAO = MissaoDAO()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPerfilBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Meu Perfil"

        carregarPerfil()

        binding.btnVoltar.setOnClickListener {
            finish()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    private fun carregarPerfil() {
        val email = auth.emailAtual()

        usuarioDAO.buscarUsuario(
            email = email,
            onSucesso = { usuario ->
                binding.txtUsername.text = usuario.username
                binding.txtNivel.text = "Nível ${usuario.nivel}"
                binding.txtXP.text = "${usuario.xp} XP"
                binding.progressXP.progress = calcularProgressoNivel(usuario.xp, usuario.nivel)
            },
            onErro = {}
        )

        missaoDAO.buscarMissoesDoUsuario(
            email = email,
            onSucesso = { missoes ->
                val concluidas = missoes.count { it.concluida }
                val totalPassos = missoes.sumOf { it.passos }
                binding.txtTotalMissoes.text = concluidas.toString()
                binding.txtTotalPassos.text = totalPassos.toString()
            },
            onErro = {}
        )
    }

    private fun calcularProgressoNivel(xp: Int, nivel: Int): Int {
        val xpAtual = when (nivel) {
            1 -> xp
            2 -> xp - 100
            3 -> xp - 250
            4 -> xp - 500
            5 -> xp - 800
            else -> xp - 1200
        }
        val xpProximoNivel = when (nivel) {
            1 -> 100
            2 -> 150
            3 -> 250
            4 -> 300
            5 -> 400
            else -> 500
        }
        return ((xpAtual.toFloat() / xpProximoNivel) * 100).toInt().coerceIn(0, 100)
    }
}