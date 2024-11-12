package com.fsa.leaf_logic

import java.io.Serializable
import java.util.Date

data class Hint(
    val id: Int = 0, // O ID será gerado pelo servidor
    val especie: String,
    val conteudo: String
): Serializable
