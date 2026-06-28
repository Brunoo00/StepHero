package com.example.stephero.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.example.stephero.auth.UsuarioAuth
import com.example.stephero.dao.UsuarioDAO
import com.example.stephero.databinding.ActivityCadastroBinding
import com.example.stephero.model.Usuario

class CadastroActivity : BaseActivity() {
    private lateinit var binding: ActivityCadastroBinding
    private val auth = UsuarioAuth()
    private val usuarioDAO = UsuarioDAO()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCadastroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Criar conta"

        binding.btnCadastrar.setOnClickListener {
            cadastrar()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    private fun cadastrar() {
        val username = binding.edtUsername.text.toString()
        val email = binding.edtEmail.text.toString()
        val senha = binding.edtSenha.text.toString()
        val confirmarSenha = binding.edtConfirmarSenha.text.toString()

        if (username.isEmpty() || email.isEmpty() || senha.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
            return
        }

        if (senha != confirmarSenha) {
            Toast.makeText(this, "As senhas não coincidem", Toast.LENGTH_SHORT).show()
            return
        }

        auth.cadastrar(
            email = email,
            senha = senha,
            onSucesso = {
                val usuario = Usuario(
                    email = email,
                    username = username,
                    nivel = 1,
                    xp = 0
                )
                usuarioDAO.salvarUsuario(
                    usuario = usuario,
                    onSucesso = {
                        startActivity(Intent(this, HomeActivity::class.java))
                        finish()
                    },
                    onErro = {
                        Toast.makeText(this, "Erro ao salvar perfil", Toast.LENGTH_SHORT).show()
                    }
                )
            },
            onErro = {
                Toast.makeText(this, "Erro ao criar conta", Toast.LENGTH_SHORT).show()
            }
        )
    }
}