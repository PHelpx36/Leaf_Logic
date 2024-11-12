package com.fsa.leaf_logic

import java.io.Serializable
import java.util.Date

data class HintImagem(
    val id: Int = 0, // O ID será gerado pelo servidor
    val hintId: String,
    val ordemImagem: String,
    var imagem: String
): Serializable
