package com.example.stephero.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.stephero.auth.UsuarioAuth
import com.example.stephero.dao.ConquistaDAO
import com.example.stephero.dao.MissaoDAO
import com.example.stephero.dao.UsuarioDAO
import com.example.stephero.databinding.ActivityFotoMarcoBinding
import com.example.stephero.helper.Base64Converter
import com.example.stephero.helper.CameraHelper
import com.example.stephero.helper.ConquistaHelper
import com.example.stephero.helper.NotificacaoHelper

class FotoMarcoActivity : BaseActivity(), CameraHelper.Callback {
    private lateinit var binding: ActivityFotoMarcoBinding
    private lateinit var cameraHelper: CameraHelper
    private lateinit var notificacaoHelper: NotificacaoHelper
    private lateinit var conquistaHelper: ConquistaHelper
    private val auth = UsuarioAuth()
    private val missaoDAO = MissaoDAO()
    private val usuarioDAO = UsuarioDAO()
    private val conquistaDAO = ConquistaDAO()
    private var fotoBitmap: Bitmap? = null
    private var missaoId = ""
    private var passosAtuais = 0
    private var ehConclusao = false
    private val REQUEST_CAMERA = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFotoMarcoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        missaoId = intent.getStringExtra("missaoId") ?: ""
        passosAtuais = intent.getIntExtra("passos", 0)
        ehConclusao = intent.getBooleanExtra("ehConclusao", false)

        supportActionBar?.title = if (ehConclusao) "Foto Final" else "Registrar Marco"

        cameraHelper = CameraHelper(this, this)
        notificacaoHelper = NotificacaoHelper(this)
        conquistaHelper = ConquistaHelper(conquistaDAO, missaoDAO)

        binding.btnTirarFoto.setOnClickListener {
            tirarFoto()
        }

        binding.btnSalvarFoto.setOnClickListener {
            Log.d("FotoMarco", "Botão salvar clicado, bitmap: $fotoBitmap")
            salvarFoto()
        }

        binding.btnPularFoto.setOnClickListener {
            if (ehConclusao) {
                concluirMissaoSemFoto()
            } else {
                notificacaoHelper.notificarMarcoRegistrado()
                finish()
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    private fun tirarFoto() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_GRANTED) {
            cameraHelper.tirarFoto()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                REQUEST_CAMERA
            )
        }
    }

    override fun onFotoRecebida(bitmap: Bitmap) {
        Log.d("FotoMarco", "Foto recebida! Bitmap: ${bitmap.width}x${bitmap.height}")
        fotoBitmap = bitmap
        binding.imgFoto.setImageBitmap(bitmap)
        Toast.makeText(this, "Foto capturada!", Toast.LENGTH_SHORT).show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CAMERA &&
            grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            cameraHelper.tirarFoto()
        } else {
            Toast.makeText(this, "Permissão da câmera negada", Toast.LENGTH_SHORT).show()
        }
    }

    private fun salvarFoto() {
        if (fotoBitmap == null) {
            Toast.makeText(this, "Tire uma foto primeiro!", Toast.LENGTH_SHORT).show()
            return
        }

        binding.btnSalvarFoto.isEnabled = false
        binding.btnSalvarFoto.text = "Salvando..."

        val fotoString = try {
            Base64Converter.bitmapToString(fotoBitmap!!)
        } catch (e: Exception) {
            binding.btnSalvarFoto.isEnabled = true
            binding.btnSalvarFoto.text = "Salvar e Continuar"
            Toast.makeText(this, "Erro ao processar foto", Toast.LENGTH_SHORT).show()
            return
        }

        if (ehConclusao) {
            concluirMissaoComFoto(fotoString)
        } else {
            salvarFotoMarco(fotoString)
        }
    }

    private fun salvarFotoMarco(fotoString: String) {
        if (missaoId.isEmpty()) {
            Toast.makeText(this, "Missão não encontrada", Toast.LENGTH_SHORT).show()
            binding.btnSalvarFoto.isEnabled = true
            binding.btnSalvarFoto.text = "Salvar e Continuar"
            finish()
            return
        }

        missaoDAO.buscarMissoesDoUsuario(
            email = auth.emailAtual(),
            onSucesso = { missoes ->
                val missao = missoes.firstOrNull { it.id == missaoId }
                if (missao != null) {
                    val missaoAtualizada = missao.copy(
                        fotoMarco = fotoString,
                        passos = passosAtuais
                    )
                    missaoDAO.salvarMissao(
                        missao = missaoAtualizada,
                        onSucesso = {
                            notificacaoHelper.notificarMarcoRegistrado()
                            Toast.makeText(this, "Marco salvo!", Toast.LENGTH_SHORT).show()
                            finish()
                        },
                        onErro = {
                            binding.btnSalvarFoto.isEnabled = true
                            binding.btnSalvarFoto.text = "Salvar e Continuar"
                            Toast.makeText(this, "Erro ao salvar marco", Toast.LENGTH_SHORT).show()
                        }
                    )
                } else {
                    // Missão não encontrada — finaliza mesmo assim
                    binding.btnSalvarFoto.isEnabled = true
                    binding.btnSalvarFoto.text = "Salvar e Continuar"
                    Toast.makeText(this, "Missão não encontrada", Toast.LENGTH_SHORT).show()
                    finish()
                }
            },
            onErro = {
                binding.btnSalvarFoto.isEnabled = true
                binding.btnSalvarFoto.text = "Salvar e Continuar"
                Toast.makeText(this, "Erro ao buscar missão", Toast.LENGTH_SHORT).show()
            }
        )
    }

    private fun concluirMissaoComFoto(fotoString: String) {
        if (missaoId.isEmpty()) {
            irParaConclusao()
            return
        }

        missaoDAO.buscarMissoesDoUsuario(
            email = auth.emailAtual(),
            onSucesso = { missoes ->
                val missao = missoes.firstOrNull { it.id == missaoId }
                if (missao != null) {
                    val missaoFinalizada = missao.copy(
                        fotoConclusao = fotoString,
                        passos = passosAtuais,
                        concluida = true
                    )
                    missaoDAO.salvarMissao(
                        missao = missaoFinalizada,
                        onSucesso = { atualizarXPEConquistas() },
                        onErro = {
                            binding.btnSalvarFoto.isEnabled = true
                            binding.btnSalvarFoto.text = "Salvar e Continuar"
                            Toast.makeText(this, "Erro ao concluir missão", Toast.LENGTH_SHORT).show()
                        }
                    )
                } else {
                    irParaConclusao()
                }
            },
            onErro = {
                binding.btnSalvarFoto.isEnabled = true
                binding.btnSalvarFoto.text = "Salvar e Continuar"
                Toast.makeText(this, "Erro ao buscar missão", Toast.LENGTH_SHORT).show()
            }
        )
    }

    private fun concluirMissaoSemFoto() {
        if (missaoId.isEmpty()) {
            irParaConclusao()
            return
        }

        missaoDAO.buscarMissoesDoUsuario(
            email = auth.emailAtual(),
            onSucesso = { missoes ->
                val missao = missoes.firstOrNull { it.id == missaoId }
                if (missao != null) {
                    val missaoFinalizada = missao.copy(
                        passos = passosAtuais,
                        concluida = true
                    )
                    missaoDAO.salvarMissao(
                        missao = missaoFinalizada,
                        onSucesso = { atualizarXPEConquistas() },
                        onErro = {
                            Toast.makeText(this, "Erro ao concluir missão", Toast.LENGTH_SHORT).show()
                        }
                    )
                } else {
                    irParaConclusao()
                }
            },
            onErro = {
                Toast.makeText(this, "Erro ao buscar missão", Toast.LENGTH_SHORT).show()
            }
        )
    }

    private fun atualizarXPEConquistas() {
        val email = auth.emailAtual()
        val xpGanho = 50

        usuarioDAO.atualizarXP(
            email = email,
            xpGanho = xpGanho,
            onSucesso = {
                conquistaHelper.verificarConquistas(
                    email = email,
                    onConcluido = {
                        missaoDAO.buscarMissoesDoUsuario(
                            email = email,
                            onSucesso = { missoes ->
                                val totalMissoes = missoes.count { it.concluida }
                                notificacaoHelper.notificarMissaoConcluida(passosAtuais)
                                irParaConclusao(xpGanho, totalMissoes)
                            },
                            onErro = {
                                irParaConclusao(xpGanho, 0)
                            }
                        )
                    }
                )
            },
            onErro = {
                irParaConclusao(xpGanho, 0)
            }
        )
    }

    private fun irParaConclusao(xpGanho: Int = 50, totalMissoes: Int = 0) {
        val intent = Intent(this, ConclusaoMissaoActivity::class.java).apply {
            putExtra("passos", passosAtuais)
            putExtra("xpGanho", xpGanho)
            putExtra("totalMissoes", totalMissoes)
        }
        startActivity(intent)
        finish()
    }
}