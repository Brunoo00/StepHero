package com.example.stephero.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.stephero.auth.UsuarioAuth
import com.example.stephero.dao.MissaoDAO
import com.example.stephero.databinding.ActivityCaminhadaBinding
import com.example.stephero.helper.BussolaHelper
import com.example.stephero.helper.NotificacaoHelper
import com.example.stephero.helper.PedometerHelper

class CaminhadaActivity : BaseActivity(),
    PedometerHelper.Callback,
    BussolaHelper.Callback {

    private lateinit var binding: ActivityCaminhadaBinding
    private val auth = UsuarioAuth()
    private val missaoDAO = MissaoDAO()
    private lateinit var pedometerHelper: PedometerHelper
    private lateinit var bussolaHelper: BussolaHelper
    private lateinit var notificacaoHelper: NotificacaoHelper
    private var passosAtuais = 0
    private var metaPassos = 6000
    private var missaoId = ""
    private var missaoConcluida = false
    private var missaoCarregada = false
    private val REQUEST_NOTIFICACAO = 1002

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCaminhadaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Missão Ativa"

        pedometerHelper = PedometerHelper(this, this)
        bussolaHelper = BussolaHelper(this, this)
        notificacaoHelper = NotificacaoHelper(this)

        // Desabilita botões até a missão carregar
        binding.btnRegistrarMarco.isEnabled = false
        binding.btnConcluirMissao.isEnabled = false

        solicitarPermissaoNotificacao()
        carregarMissaoAtiva()

        binding.btnRegistrarMarco.setOnClickListener {
            if (!missaoCarregada) {
                Toast.makeText(this, "Aguarde carregar a missão...", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val intent = Intent(this, FotoMarcoActivity::class.java)
            intent.putExtra("missaoId", missaoId)
            intent.putExtra("passos", passosAtuais)
            startActivity(intent)
        }

        binding.btnConcluirMissao.setOnClickListener {
            if (!missaoCarregada) {
                Toast.makeText(this, "Aguarde carregar a missão...", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            concluirMissao()
        }
    }

    override fun onResume() {
        super.onResume()
        pedometerHelper.iniciar()
        bussolaHelper.iniciar()
    }

    override fun onPause() {
        super.onPause()
        pedometerHelper.parar()
        bussolaHelper.parar()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    private fun solicitarPermissaoNotificacao() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    REQUEST_NOTIFICACAO
                )
            }
        }
    }

    private fun carregarMissaoAtiva() {
        val email = auth.emailAtual()
        missaoDAO.buscarMissoesDoUsuario(
            email = email,
            onSucesso = { missoes ->
                val missaoAtiva = missoes.firstOrNull { !it.concluida }
                if (missaoAtiva != null) {
                    missaoId = missaoAtiva.id
                    metaPassos = missaoAtiva.metaPassos
                    passosAtuais = missaoAtiva.passos
                    missaoCarregada = true
                    binding.txtMeta.text = "Meta: $metaPassos passos"
                    binding.btnRegistrarMarco.isEnabled = true
                    binding.btnConcluirMissao.isEnabled = true
                    atualizarUI()
                } else {
                    Toast.makeText(
                        this,
                        "Nenhuma missão ativa. Volte à tela inicial.",
                        Toast.LENGTH_LONG
                    ).show()
                    finish()
                }
            },
            onErro = {
                Toast.makeText(this, "Erro ao carregar missão", Toast.LENGTH_SHORT).show()
                finish()
            }
        )
    }

    private fun concluirMissao() {
        val intent = Intent(this, FotoMarcoActivity::class.java)
        intent.putExtra("missaoId", missaoId)
        intent.putExtra("passos", passosAtuais)
        intent.putExtra("ehConclusao", true)
        startActivity(intent)
    }

    private fun atualizarUI() {
        binding.txtPassos.text = passosAtuais.toString()
        val progresso = ((passosAtuais.toFloat() / metaPassos) * 100).toInt().coerceIn(0, 100)
        binding.progressMissao.progress = progresso

        if (passosAtuais >= metaPassos && !missaoConcluida) {
            missaoConcluida = true
            Toast.makeText(this, "Meta atingida! Tire a foto final!", Toast.LENGTH_LONG).show()
        }
    }

    override fun onPassoContado(totalPassos: Int) {
        passosAtuais = totalPassos
        runOnUiThread { atualizarUI() }
    }

    override fun onDirecaoAtualizada(graus: Float, direcao: String) {
        runOnUiThread {
            binding.txtDirecao.text = direcao
            binding.txtGraus.text = "${graus.toInt()}°"
        }
    }

    override fun onErro(mensagem: String) {
        runOnUiThread {
            Toast.makeText(this, mensagem, Toast.LENGTH_SHORT).show()
        }
    }
}