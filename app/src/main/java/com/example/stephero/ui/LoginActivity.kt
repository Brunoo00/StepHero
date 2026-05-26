package com.example.stephero.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.stephero.auth.UsuarioAuth
import com.example.stephero.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val auth = UsuarioAuth()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (auth.estaLogado()) {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }

        binding.btnEntrar.setOnClickListener {
            fazerLogin()
        }

        binding.btnCriarConta.setOnClickListener {
            startActivity(Intent(this, CadastroActivity::class.java))
        }
    }

    private fun fazerLogin() {
        val email = binding.edtEmail.text.toString()
        val senha = binding.edtSenha.text.toString()

        if (email.isEmpty() || senha.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
            return
        }

        auth.login(
            email = email,
            senha = senha,
            onSucesso = {
                startActivity(Intent(this, HomeActivity::class.java))
                finish()
            },
            onErro = {
                Toast.makeText(this, "E-mail ou senha inválidos", Toast.LENGTH_SHORT).show()
            }
        )
    }
}