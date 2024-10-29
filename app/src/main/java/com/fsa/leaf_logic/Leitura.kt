package com.fsa.leaf_logic

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Leitura(
    val id: Int,
    @SerializedName("temperatura") val temperatura: Double,
    @SerializedName("luminosidade") val luminosidade: Double,
    @SerializedName("umidade") val umidade: Double
): Serializable
