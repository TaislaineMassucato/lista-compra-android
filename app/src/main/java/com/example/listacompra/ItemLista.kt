package com.example.listacompra

data class ItemLista(
    val id: Int? = null,
    var nome: String,
    var categoria: String = "Geral",
    var grupo: String = "Mercado",
    var quantidade: Int = 1,
    var marcado: Boolean = false,
    val dataCriacao: String? = null
)