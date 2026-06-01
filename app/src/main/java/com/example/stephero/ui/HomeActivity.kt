package com.example.stephero.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.stephero.auth.UsuarioAuth
import com.example.stephero.dao.MissaoDAO
import com.example.stephero.dao.UsuarioDAO
import com.example.stephero.databinding.ActivityHomeBinding
import com.example.stephero.model.Missao
import com.google.firebase.Timestamp

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private val auth = UsuarioAuth()
    private val usuarioDAO = UsuarioDAO()
    private val missaoDAO = MissaoDAO()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        carregarDadosUsuario()
        carregarMissaoDoDia()

        binding.btnIniciarMissao.setOnClickListener {
            startActivity(Intent(this, CaminhadaActivity::class.java))
        }

        binding.btnPerfil.setOnClickListener {
            startActivity(Intent(this, PerfilActivity::class.java))
        }

        binding.btnConquistas.setOnClickListener {
            startActivity(Intent(this, ConquistasActivity::class.java))
        }

        binding.btnSair.setOnClickListener {
            auth.logout()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        carregarDadosUsuario()
        carregarMissaoDoDia()
    }

    private fun carregarDadosUsuario() {
        val email = auth.emailAtual()
        usuarioDAO.buscarUsuario(
            email = email,
            onSucesso = { usuario ->
                binding.txtSaudacao.text = "Olá, ${usuario.username}!"
                binding.txtNivel.text = "Nível ${usuario.nivel}"
                binding.txtXP.text = "${usuario.xp} XP"
                val progressoNivel = calcularProgressoNivel(usuario.xp, usuario.nivel)
                binding.progressXP.progress = progressoNivel
            },
            onErro = {}
        )
    }

    private fun carregarMissaoDoDia() {
        val email = auth.emailAtual()
        missaoDAO.buscarMissoesDoUsuario(
            email = email,
            onSucesso = { missoes ->
                val missaoHoje = missoes.firstOrNull { !it.concluida }
                if (missaoHoje != null) {
                    atualizarCardMissao(missaoHoje)
                } else {
                    criarNovaMissao(email)
                }
            },
            onErro = {}
        )
    }

    private fun criarNovaMissao(email: String) {
        val novaMissao = Missao(
            id = missaoDAO.gerarIdMissao(),
            usuarioEmail = email,
            data = Timestamp.now(),
            passos = 0,
            metaPassos = 6000,
            concluida = false
        )
        missaoDAO.salvarMissao(
            missao = novaMissao,
            onSucesso = { atualizarCardMissao(novaMissao) },
            onErro = {}
        )
    }

    private fun atualizarCardMissao(missao: Missao) {
        binding.txtMissao.text = "Andar ${missao.metaPassos} passos"
        binding.txtPassosAtuais.text = "${missao.passos} passos"
        val progresso = ((missao.passos.toFloat() / missao.metaPassos) * 100).toInt()
        binding.progressMissao.progress = progresso
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