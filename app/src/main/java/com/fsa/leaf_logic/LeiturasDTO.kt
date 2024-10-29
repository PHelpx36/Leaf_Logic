package com.fsa.leaf_logic

data class Leituras(
    val Temperaturas: MutableList<Double> = mutableListOf(),
    val Umidades: MutableList<Double> = mutableListOf(),
    val Luminosidades: MutableList<Double> = mutableListOf()
)