package com.fsa.leaf_logic

import java.io.Serializable

data class User(
    val id: Int = 0, // O ID será gerado pelo servidor
    val nome: String,
    val email: String,
    val senha: String,
    val dataCadastro: String,
    val dataUpdate: String
): Serializable
