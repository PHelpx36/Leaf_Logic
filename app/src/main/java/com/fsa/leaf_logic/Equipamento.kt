package com.fsa.leaf_logic

import java.io.Serializable

data class Equipamento(
    val id: Int = 0, // O ID será gerado pelo servidor
    val nome: String,
    val descricao: String
): Serializable
