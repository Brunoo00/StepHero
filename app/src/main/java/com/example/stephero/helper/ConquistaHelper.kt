package com.example.stephero.helper

import com.example.stephero.dao.ConquistaDAO
import com.example.stephero.dao.MissaoDAO
import com.example.stephero.model.Conquista
import com.google.firebase.Timestamp

class ConquistaHelper(
    private val conquistaDAO: ConquistaDAO,
    private val missaoDAO: MissaoDAO
) {

    fun verificarConquistas(email: String, onConcluido: () -> Unit) {
        missaoDAO.buscarMissoesDoUsuario(
            email = email,
            onSucesso = { missoes ->
                conquistaDAO.buscarConquistasDoUsuario(
                    email = email,
                    onSucesso = { conquistasExistentes ->
                        val titulosExistentes = conquistasExistentes.map { it.titulo }
                        val novasConquistas = mutableListOf<Conquista>()
                        val missoesConcluidas = missoes.count { it.concluida }
                        val totalPassos = missoes.sumOf { it.passos }

                        // Primeira missão
                        if (missoesConcluidas >= 1 && "Primeiro Passo" !in titulosExistentes) {
                            novasConquistas.add(
                                Conquista(
                                    id = conquistaDAO.gerarIdConquista(),
                                    usuarioEmail = email,
                                    titulo = "Primeiro Passo",
                                    descricao = "Complete sua primeira missão",
                                    data = Timestamp.now()
                                )
                            )
                        }

                        // 5 missões
                        if (missoesConcluidas >= 5 && "Guerreiro" !in titulosExistentes) {
                            novasConquistas.add(
                                Conquista(
                                    id = conquistaDAO.gerarIdConquista(),
                                    usuarioEmail = email,
                                    titulo = "Guerreiro",
                                    descricao = "Complete 5 missões",
                                    data = Timestamp.now()
                                )
                            )
                        }

                        // 10 missões
                        if (missoesConcluidas >= 10 && "Herói" !in titulosExistentes) {
                            novasConquistas.add(
                                Conquista(
                                    id = conquistaDAO.gerarIdConquista(),
                                    usuarioEmail = email,
                                    titulo = "Herói",
                                    descricao = "Complete 10 missões",
                                    data = Timestamp.now()
                                )
                            )
                        }

                        // 10.000 passos totais
                        if (totalPassos >= 10000 && "Andarilho" !in titulosExistentes) {
                            novasConquistas.add(
                                Conquista(
                                    id = conquistaDAO.gerarIdConquista(),
                                    usuarioEmail = email,
                                    titulo = "Andarilho",
                                    descricao = "Acumule 10.000 passos no total",
                                    data = Timestamp.now()
                                )
                            )
                        }

                        // 50.000 passos totais
                        if (totalPassos >= 50000 && "Lendário" !in titulosExistentes) {
                            novasConquistas.add(
                                Conquista(
                                    id = conquistaDAO.gerarIdConquista(),
                                    usuarioEmail = email,
                                    titulo = "Lendário",
                                    descricao = "Acumule 50.000 passos no total",
                                    data = Timestamp.now()
                                )
                            )
                        }

                        salvarNovasConquistas(novasConquistas, onConcluido)
                    },
                    onErro = { onConcluido() }
                )
            },
            onErro = { onConcluido() }
        )
    }

    private fun salvarNovasConquistas(
        conquistas: List<Conquista>,
        onConcluido: () -> Unit
    ) {
        if (conquistas.isEmpty()) {
            onConcluido()
            return
        }

        var salvas = 0
        conquistas.forEach { conquista ->
            conquistaDAO.salvarConquista(
                conquista = conquista,
                onSucesso = {
                    salvas++
                    if (salvas == conquistas.size) onConcluido()
                },
                onErro = {
                    salvas++
                    if (salvas == conquistas.size) onConcluido()
                }
            )
        }
    }
}