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

        try {
            if (auth.estaLogado()) {
                startActivity(Intent(this, HomeActivity::class.java))
                finish()
                return
            }
        } catch (e: Exception) { }

        binding.btnEntrar.setOnClickListener {
            fazerLogin()
        }

        binding.btnCriarConta.setOnClickListener {
            startActivity(Intent(this, CadastroActivity::class.java))
        }
    }

    private fun fazerLogin() {
        val email = binding.edtEmail.text.toString().trim()
        val senha = binding.edtSenha.text.toString()

        if (email.isEmpty() || senha.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
            return
        }

        binding.btnEntrar.isEnabled = false

        auth.login(
            email = email,
            senha = senha,
            onSucesso = {
                startActivity(Intent(this, HomeActivity::class.java))
                finish()
            },
            onErro = {
                binding.btnEntrar.isEnabled = true
                Toast.makeText(this, "E-mail ou senha inválidos", Toast.LENGTH_SHORT).show()
            }
        )
    }
}