package com.fsa.leaf_logic

import java.io.Serializable

data class Planta(
    val id: Int = 0, // O ID será gerado pelo servidor
    val usuarioId: Int,
    var equipamentoId: Int?,
    val nome: String,
    val especie: String,
    val descricao: String,
    val imagem: String,
    val dataCadastro: String,
    val dataUpdate: String
): Serializable
