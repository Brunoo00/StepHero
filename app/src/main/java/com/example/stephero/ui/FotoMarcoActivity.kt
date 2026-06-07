package com.example.stephero.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.stephero.auth.UsuarioAuth
import com.example.stephero.dao.MissaoDAO
import com.example.stephero.databinding.ActivityFotoMarcoBinding
import com.example.stephero.helper.Base64Converter
import com.example.stephero.helper.CameraHelper
import com.example.stephero.helper.NotificacaoHelper

class FotoMarcoActivity : AppCompatActivity(), CameraHelper.Callback {
    private lateinit var binding: ActivityFotoMarcoBinding
    private lateinit var cameraHelper: CameraHelper
    private lateinit var notificacaoHelper: NotificacaoHelper
    private val auth = UsuarioAuth()
    private val missaoDAO = MissaoDAO()
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

        binding.btnTirarFoto.setOnClickListener {
            tirarFoto()
        }

        binding.btnSalvarFoto.setOnClickListener {
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
        fotoBitmap = bitmap
        binding.imgFoto.setImageBitmap(bitmap)
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
            Toast.makeText(this, "Tire uma foto primeiro", Toast.LENGTH_SHORT).show()
            return
        }

        val fotoString = Base64Converter.bitmapToString(fotoBitmap!!)

        if (ehConclusao) {
            concluirMissaoComFoto(fotoString)
        } else {
            salvarFotoMarco(fotoString)
        }
    }

    private fun salvarFotoMarco(fotoString: String) {
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
                            Toast.makeText(this, "Erro ao salvar marco", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            },
            onErro = {}
        )
    }

    private fun concluirMissaoComFoto(fotoString: String) {
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
                        onSucesso = {
                            atualizarXPEConquistas()
                        },
                        onErro = {
                            Toast.makeText(this, "Erro ao concluir missão", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            },
            onErro = {}
        )
    }

    private fun concluirMissaoSemFoto() {
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
                        onSucesso = {
                            atualizarXPEConquistas()
                        },
                        onErro = {
                            Toast.makeText(this, "Erro ao concluir missão", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            },
            onErro = {}
        )
    }

    private fun atualizarXPEConquistas() {
        val email = auth.emailAtual()
        val xpGanho = 50

        missaoDAO.buscarMissoesDoUsuario(
            email = email,
            onSucesso = { missoes ->
                val totalMissoes = missoes.count { it.concluida }
                val intent = Intent(this, ConclusaoMissaoActivity::class.java).apply {
                    putExtra("passos", passosAtuais)
                    putExtra("xpGanho", xpGanho)
                    putExtra("totalMissoes", totalMissoes)
                }
                notificacaoHelper.notificarMissaoConcluida(passosAtuais)
                startActivity(intent)
                finish()
            },
            onErro = {}
        )
    }
}