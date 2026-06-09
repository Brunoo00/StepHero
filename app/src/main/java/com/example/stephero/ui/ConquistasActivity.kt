package com.example.stephero.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.stephero.adapter.ConquistaAdapter
import com.example.stephero.auth.UsuarioAuth
import com.example.stephero.dao.ConquistaDAO
import com.example.stephero.databinding.ActivityConquistasBinding

class ConquistasActivity : AppCompatActivity() {
    private lateinit var binding: ActivityConquistasBinding
    private val auth = UsuarioAuth()
    private val conquistaDAO = ConquistaDAO()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityConquistasBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Conquistas"

        carregarConquistas()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    private fun carregarConquistas() {
        val email = auth.emailAtual()
        conquistaDAO.buscarConquistasDoUsuario(
            email = email,
            onSucesso = { conquistas ->
                if (conquistas.isEmpty()) {
                    Toast.makeText(
                        this,
                        "Nenhuma conquista ainda. Complete missões!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                val adapter = ConquistaAdapter(conquistas)
                binding.recyclerConquistas.apply {
                    layoutManager = LinearLayoutManager(this@ConquistasActivity)
                    this.adapter = adapter
                }
            },
            onErro = {
                Toast.makeText(this, "Erro ao carregar conquistas", Toast.LENGTH_SHORT).show()
            }
        )
    }
}