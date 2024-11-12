package com.fsa.leaf_logic

import java.io.Serializable
import java.util.Date

data class Notificacao(
    val id: Int = 0, // O ID será gerado pelo servidor
    val dever: String,
    val descricao: String,
    var concluida: String,
    val dataConclusao: String,
    val dataEmissicao: String
): Serializable
