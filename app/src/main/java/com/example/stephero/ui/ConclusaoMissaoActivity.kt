package com.example.stephero.ui

import android.content.Intent
import android.os.Bundle
import com.example.stephero.auth.UsuarioAuth
import com.example.stephero.dao.UsuarioDAO
import com.example.stephero.databinding.ActivityConclusaoMissaoBinding

class ConclusaoMissaoActivity : BaseActivity() {
    private lateinit var binding: ActivityConclusaoMissaoBinding
    private val auth = UsuarioAuth()
    private val usuarioDAO = UsuarioDAO()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityConclusaoMissaoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val passos = intent.getIntExtra("passos", 0)
        val xpGanho = intent.getIntExtra("xpGanho", 50)
        val totalMissoes = intent.getIntExtra("totalMissoes", 0)

        binding.txtPassosFinal.text = passos.toString()
        binding.txtMissoesFinal.text = totalMissoes.toString()
        binding.txtXPFinal.text = "+$xpGanho"
        binding.txtXPGanho.text = "+$xpGanho XP"

        atualizarXP(xpGanho)

        binding.btnVoltarHome.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
            finish()
        }
    }

    private fun atualizarXP(xpGanho: Int) {
        val email = auth.emailAtual()
        usuarioDAO.atualizarXP(
            email = email,
            xpGanho = xpGanho,
            onSucesso = {},
            onErro = {}
        )
    }
}